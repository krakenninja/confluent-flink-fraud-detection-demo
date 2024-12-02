package com.github.krakenninja.demo.confluent.configuration;

import com.github.krakenninja.demo.exceptions.InternalException;
import com.github.krakenninja.demo.exceptions.InvalidConfigurationException;
import io.confluent.flink.plugin.ConfluentSettings;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileOutputStream;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Confluent Cloud : Spring Boot configuration
 * <p>
 * Looking at Spring Boot context resource "{@code application.yml}" having 
 * settings structure : 
 * <pre>
 *   confluent: 
 *     cloud: 
 *       properties: 
 *         # cloud region
 *         client.cloud: aws
 *         client.region: eu-west-1
 *         # compute resources
 *         client.organization-id: 00000000-0000-0000-0000-000000000000
 *         client.environment-id: env-xxxxx
 *         client.compute-pool-id: lfcp-xxxxxxxxxx
 *         # Security
 *         client.flink-api-key: XXXXXXXXXXXXXXXX
 *         client.flink-api-secret: XxXxXxXxXxXxXxXxXxXxXxXxXxXxXxXxXxXxXxXx
 *         # table API
 *         client.context: XXXXXXXXXXXXXXXX
 *         sql.local-time-zone: UTC
 * </pre>
 * </p>
 * @since 1.0.0
 * @author Christopher CKW
 * @see org.apache.flink.table.api.EnvironmentSettings.Builder#withConfiguration(org.apache.flink.configuration.Configuration) 
 * @see io.confluent.flink.plugin.ConfluentSettings
 * @see io.confluent.flink.plugin.ConfluentPluginOptions
 */
@Accessors(
    chain = true
)
@Getter
@Setter
@NoArgsConstructor
@Configuration
@ConfigurationProperties(
    prefix = "confluent.cloud"
)
public class ConfluentCloudConfiguration
{
    /**
     * Bean name for {@link org.apache.flink.table.api.TableEnvironment}
     * @since 1.0.0
     */
    public static final String BEAN_NAME_TABLE_ENVIRONMENT = "tableEnvironment";
    
    /**
     * Bean name for {@link org.apache.flink.table.api.EnvironmentSettings}
     * @since 1.0.0
     */
    public static final String BEAN_NAME_ENVIRONMENT_SETTINGS = "environmentSettings";
    
    /**
     * Properties that is loaded from the Spring {@code application.yml}
     * @since 1.0.0
     */
    @Nonnull
    @NotEmpty
    private Properties properties;
    
    /**
     * My custom table API user defined configuration that is not available by 
     * Confluent Cloud ; this is primarily used for my own to define additional 
     * configuration
     * @since 1.0.0
     */
    @Nonnull
    private TableApiUserDefined tableApi;
    
    /**
     * Get managed table environment singleton bean ; do not use this if you 
     * are going to change the catalog/database, instead construct a new 
     * {@link org.apache.flink.table.api.TableEnvironment} using sample code 
     * snippet below : 
     * <pre>
     *   // access the confluent settings
     *   org.apache.flink.table.api.EnvironmentSettings environmentSettings = confluentCloudConfiguration.getConfluentSettings();
     *   
     *   // create the table environment settings
     *   org.apache.flink.table.api.TableEnvironment tableEnvironment = org.apache.flink.table.api.TableEnvironment.create(
     *     environmentSettings
     *   );
     *   
     *   // change the catalog
     *   tableEnvironment.useCatalog("examples");
     * 
     *   // change the database
     *   tableEnvironment.useDatabase("marketplace");
     *   
     * </pre>
     * <p>
     * Requires {@link #getEnvironmentSettings()}
     * </p>
     * @return 
     * @since 1.0.0
     * @see <a href="https://docs.confluent.io/cloud/current/flink/reference/table-api.html">Table API on Confluent Cloud for Apache Flink</a>
     * @see <a href="https://github.com/confluentinc/learn-apache-flink-table-api-for-java-exercises/blob/main/solutions/01-connecting-to-confluent-cloud/src/main/java/marketplace/Marketplace.java">learn-apache-flink-table-api-for-java-exercises/solutions/01-connecting-to-confluent-cloud/src/main/java/marketplace/Marketplace.java</a>
     */
    @Nonnull
    @Bean(
        BEAN_NAME_TABLE_ENVIRONMENT
    )
    @DependsOn(
        value = {
            BEAN_NAME_ENVIRONMENT_SETTINGS
        }
    )
    public TableEnvironment getTableEnvironment()
    {
        return TableEnvironment.create(
            getEnvironmentSettings()
        );
    }
    
    /**
     * Get confluent environment settings (Confluent Cloud settings ; i.e. cloud, 
     * region, org, env, compute pool, key and secret)
     * <p>
     * The settings are defined/configured in the Spring {@code application.yml}
     * </p>
     * @return                                  {@link org.apache.flink.table.api.EnvironmentSettings}, 
     *                                          never {@code null}
     * @since 1.0.0
     * @see io.confluent.flink.plugin.ConfluentSettings
     * @see <a href="https://docs.confluent.io/cloud/current/flink/reference/table-api.html">Table API on Confluent Cloud for Apache Flink</a>
     */
    @Nonnull
    public EnvironmentSettings getConfluentSettings()
    {
        final File confluentSettingProperties = getPropertiesAsFile();
        // code sets the session name and SQL-specific options
        return Optional.ofNullable(
            ConfluentSettings.newBuilderFromFile(
                confluentSettingProperties
            )
        ).map(
            confluentSettingsToProcess -> {
                if(getProperties().containsKey("client.context"))
                {
                    confluentSettingsToProcess.setContextName(
                        getProperties().getProperty(
                            "client.context"
                        )
                    );
                }
                if(getProperties().containsKey("sql.local-time-zone"))
                {
                    confluentSettingsToProcess.setOption(
                        "sql.local-time-zone",
                        getProperties().getProperty(
                            "sql.local-time-zone"
                        )
                    );
                }
                return confluentSettingsToProcess.build();
            }
        ).orElseThrow(
            () -> new InternalException(
                String.format(
                    "New builer from file '%s' using `%s::newBuilderFromFile(%s)` IS NULL",
                    confluentSettingProperties.getPath(),
                    ConfluentSettings.class.getName(),
                    File.class.getName()
                )
            )
        );
    }
    
    /**
     * Get environment settings (Confluent Cloud settings ; i.e. cloud, 
     * region, org, env, compute pool, key and secret)
     * <p>
     * The settings are defined/configured in the Spring {@code application.yml}
     * </p>
     * @return                                  {@link org.apache.flink.table.api.EnvironmentSettings}, 
     *                                          never {@code null}
     * @since 1.0.0
     * @see <a href="https://docs.confluent.io/cloud/current/flink/reference/table-api.html">Table API on Confluent Cloud for Apache Flink</a>
     */
    @Nonnull
    @Bean(
        BEAN_NAME_ENVIRONMENT_SETTINGS
    )
    protected EnvironmentSettings getEnvironmentSettings()
    {
        final org.apache.flink.configuration.Configuration configuration = org.apache.flink.configuration.Configuration.fromMap(
            getProperties().entrySet().stream().filter(
                entryToProcess -> String.class.isAssignableFrom(
                    entryToProcess.getKey().getClass()
                )
            ).filter(
                entryToProcess -> String.class.isAssignableFrom(
                    entryToProcess.getValue().getClass()
                )
            ).map(
                entryToProcess -> new AbstractMap.SimpleEntry<String, String>(
                    (String)entryToProcess.getKey(),
                    (String)entryToProcess.getValue()
                )
            ).collect(
                Collectors.toMap(
                    Map.Entry::getKey, 
                    Map.Entry::getValue, 
                    (k1, k2) -> k2, 
                    LinkedHashMap::new
                )
            )
        );
        return new EnvironmentSettings.Builder().withConfiguration(
            configuration
        ).build();
    }
    
    /**
     * Get the properties as {@link File}
     * @return                                  {@link File}, never {@code null}
     * @throws InternalException                If unable to create file
     * @since 1.0.0
     */
    @Nonnull
    protected File getPropertiesAsFile()
    {
        try
        {
            final File tmpProperties = File.createTempFile(
                String.format(
                    "confluent-cloud-%s", 
                    UUID.randomUUID().toString()
                ),
                ".properties"
            );
            tmpProperties.deleteOnExit();
            getProperties().store(
                new FileOutputStream(
                    tmpProperties
                ),
                null
            );
            return tmpProperties;
        }
        catch(Exception e)
        {
            throw new InternalException(
                String.format(
                    "Get properties as object `%s` type ENCOUNTERED FAILURE ; %s",
                    File.class.getName(),
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Custom user-defined table API configuration
     * @author Christopher CKW
     * @since 1.0.0
     */
    @Accessors(
        chain = true
    )
    @Getter
    @Setter
    @NoArgsConstructor
    public static final class TableApiUserDefined
    {
        /**
         * Use catalog property name
         * @since 1.0.0
         * @see org.apache.flink.table.api.TableEnvironment#useCatalog(java.lang.String) 
         */
        private static final String PROPERTY_NAME_USE_CATALOG = "use.catalog";
        
        /**
         * Use database property name
         * @since 1.0.0
         * @see org.apache.flink.table.api.TableEnvironment#useDatabase(java.lang.String) 
         */
        private static final String PROPERTY_NAME_USE_DATABASE = "use.database";
        
        /**
         * Properties to obtain the custom configuration
         * @since 1.0.0
         */
        private Properties properties;
        
        /**
         * Get "use catalog"
         * @return 
         * @since 1.0.0
         * @see org.apache.flink.table.api.TableEnvironment#useCatalog(java.lang.String) 
         */
        @Nonnull
        @NotBlank
        @NotEmpty
        public String getUseCatalog()
        {
            return Optional.ofNullable(
                getProperties()
            ).filter(
                propertiesToProcess -> propertiesToProcess.containsKey(
                    PROPERTY_NAME_USE_CATALOG
                )
            ).map(
                propertiesToProcess -> propertiesToProcess.getProperty(
                    PROPERTY_NAME_USE_CATALOG
                )
            ).filter(
                useCatalogToProcess -> !useCatalogToProcess.trim().equals(
                    ""
                )
            ).orElseThrow(
                () -> getInvalidConfigurationException(
                    PROPERTY_NAME_USE_CATALOG
                )
            );
        }
        
        /**
         * Get "use database"
         * @return 
         * @since 1.0.0
         * @see org.apache.flink.table.api.TableEnvironment#useDatabase(java.lang.String) 
         */
        @Nonnull
        @NotBlank
        @NotEmpty
        public String getUseDatabase()
        {
            return Optional.ofNullable(
                getProperties()
            ).filter(
                propertiesToProcess -> propertiesToProcess.containsKey(
                    PROPERTY_NAME_USE_DATABASE
                )
            ).map(
                propertiesToProcess -> propertiesToProcess.getProperty(
                    PROPERTY_NAME_USE_DATABASE
                )
            ).filter(
                useDatabaseToProcess -> !useDatabaseToProcess.trim().equals(
                    ""
                )
            ).orElseThrow(
                () -> getInvalidConfigurationException(
                    PROPERTY_NAME_USE_DATABASE
                )
            );
        }
        
        /**
         * Shared method to return {@link InvalidConfigurationException} instance
         * @param configurationName             Configuration name
         * @return 
         */
        @Nonnull
        private InvalidConfigurationException getInvalidConfigurationException(@Nonnull
                                                                               @NotBlank
                                                                               @NotEmpty
                                                                               final String configurationName)
        {
            return new InvalidConfigurationException(
                String.format(
                    "Spring Boot configuration name '%s' IS NOT AVAILABLE",
                    configurationName
                )
            );
        }
    }
}
