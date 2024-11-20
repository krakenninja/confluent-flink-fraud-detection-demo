package com.github.krakenninja.demo.flink.kafka;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Kafka configuration
 * @since 1.0.0
 * @author Christopher CKW
 */
@Accessors(
    chain = true
)
@Getter
@Setter
public class Configuration
{
    @Nonnull
    private Properties properties;
    
    @Nonnull
    @NotBlank
    @NotEmpty
    private String deserializerType;
    
    @Nonnull
    @NotBlank
    @NotEmpty
    private String serializerType;
}
