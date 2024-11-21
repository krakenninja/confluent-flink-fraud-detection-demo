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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Nonnull
    @NotEmpty
    private Properties properties;
    
    /**
     * Get table API settings
     * @return                                  {@link org.apache.flink.table.api.EnvironmentSettings}, 
     *                                          never {@code null}
     * @since 1.0.0
     * @see io.confluent.flink.plugin.ConfluentSettings
     */
    @Nonnull
    @Bean
    public EnvironmentSettings getTableApiSettings()
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
