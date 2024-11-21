package com.github.krakenninja.demo.confluent.configuration;

import io.confluent.flink.plugin.ConfluentSettings;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
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
 * Introduce so that we can make use of Spring Boot configuration
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
 * </pre>
 * </p>
 * @since 1.0.0
 * @author Christopher CKW
 * @see org.apache.flink.table.api.EnvironmentSettings.Builder#withConfiguration(org.apache.flink.configuration.Configuration) 
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
     * Properties that is loaded from the Spring {@code application.yml}
     * @since 1.0.0
     */
    @Nonnull
    @NotEmpty
    private Properties properties;
    
    /**
     * Get table environment session name and SQL-specific options etc.
     * <p>
     * Requires {@link #getTableEnvironmentSettings()}
     * </p>
     * @return 
     * @since 1.0.0
     * @see <a href="https://docs.confluent.io/cloud/current/flink/reference/table-api.html">Table API on Confluent Cloud for Apache Flink</a>
     */
    @Nonnull
    @Bean(
        "tableEnvironment"
    )
    @DependsOn(
        value = {
            "tableEnvironmentSettings"
        }
    )
    public TableEnvironment getTableEnvironment()
    {
        return TableEnvironment.create(
            getTableEnvironmentSettings()
        );
    }

    /**
     * Get table environment settings (Confluent Cloud settings ; i.e. cloud, 
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
    @Bean(
        "tableEnvironmentSettings"
    )
    public EnvironmentSettings getTableEnvironmentSettings()
    {
        final org.apache.flink.configuration.Configuration confluentCloudConfiguration = org.apache.flink.configuration.Configuration.fromMap(
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
            confluentCloudConfiguration
        ).build();
    }
}
