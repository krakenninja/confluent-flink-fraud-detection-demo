package com.github.krakenninja.demo.constants;

import lombok.NoArgsConstructor;

/**
 * Application related constants
 * @since 1.0.0
 * @author Christopher CKW
 */
@NoArgsConstructor
public final class AppConstants
{
    /**
     * Confluent Cloud
     * <p>
     * Indication for Confluent Cloud deployment type
     * </p>
     * @since 1.0.0
     */
    public static final String APP_PROFILE_CONFLUENT_CLOUD = "confluentcloud-deployment";
    
    /**
     * Default GitHub Event schema
     * <p>
     * Indication for Confluent Cloud deployment type
     * </p>
     * @since 1.0.0
     * @see com.github.krakenninja.demo.model.transformer.impl.DefaultGitHubEventSchema
     */
    public static final String APP_PROFILE_GITHUB_EVENT_SCHEMA_DEFAULT = "githubevent-schema-default";
}
