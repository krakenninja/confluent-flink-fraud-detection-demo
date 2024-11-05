package com.github.krakenninja.demo.constant;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

/**
 * Confluent setting name constants
 * @since 1.0.0
 * @see io.confluent.flink.plugin.ConfluentSettings#fromGlobalVariables() 
 * @see io.confluent.flink.plugin.ConfluentSettings#fromArgs(java.lang.String[]) 
 * @author Christopher CKW
 */
@RequiredArgsConstructor
public enum ConfluentSettingNames
{
    /**
     * Cloud provider
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_CLOUD_PROVIDER
     * @see ConfluentSettingNames#ARGPARAM_CLOUD_PROVIDER
     */
    ENV_CLOUD_PROVIDER(
        ConfluentSettingNames.SYSENV_CLOUD_PROVIDER,
        ConfluentSettingNames.ARGPARAM_CLOUD_PROVIDER,
        "Cloud provider"
    ),
    
    /**
     * Cloud region
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_CLOUD_REGION
     * @see ConfluentSettingNames#ARGPARAM_CLOUD_REGION
     */
    ENV_CLOUD_REGION(
        ConfluentSettingNames.SYSENV_CLOUD_REGION,
        ConfluentSettingNames.ARGPARAM_CLOUD_REGION,
        "Cloud region"
    ),
    
    /**
     * Flink API key
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_FLINK_API_KEY
     * @see ConfluentSettingNames#ARGPARAM_FLINK_API_KEY
     */
    ENV_FLINK_API_KEY(
        ConfluentSettingNames.SYSENV_FLINK_API_KEY,
        ConfluentSettingNames.ARGPARAM_FLINK_API_KEY,
        "Flink API key"
    ),
    
    /**
     * Flink API secret
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_FLINK_API_SECRET
     * @see ConfluentSettingNames#ARGPARAM_FLINK_API_SECRET
     */
    ENV_FLINK_API_SECRET(
        ConfluentSettingNames.SYSENV_FLINK_API_SECRET,
        ConfluentSettingNames.ARGPARAM_FLINK_API_SECRET,
        "Flink API secret"
    ),
    
    /**
     * Organization ID
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_ORG_ID
     * @see ConfluentSettingNames#ARGPARAM_ORG_ID
     */
    ENV_ORGANIZATION_ID(
        ConfluentSettingNames.SYSENV_ORG_ID,
        ConfluentSettingNames.ARGPARAM_ORG_ID,
        "Organization ID"
    ),
    
    /**
     * Environment ID
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_ENV_ID
     * @see ConfluentSettingNames#ARGPARAM_ENV_ID
     */
    ENV_ENVIRONMENT_ID(
        ConfluentSettingNames.SYSENV_ENV_ID,
        ConfluentSettingNames.ARGPARAM_ENV_ID,
        "Environment ID"
    ),
    
    /**
     * Compute Pool ID
     * @since 1.0.0
     * @see ConfluentSettingNames#SYSENV_COMPUTE_POOL_ID
     * @see ConfluentSettingNames#ARGPARAM_COMPUTE_POOL_ID
     */
    ENV_COMPUTE_POOL_ID(
        ConfluentSettingNames.SYSENV_COMPUTE_POOL_ID,
        ConfluentSettingNames.ARGPARAM_COMPUTE_POOL_ID,
        "Compute Pool ID"
    ),
    
    ;
    
    /**
     * Cloud provider ; e.g. `aws`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_CLOUD_PROVIDER = "CLOUD_PROVIDER";
    
    /**
     * Cloud region ; e.g. `us-east-1`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_CLOUD_REGION = "CLOUD_REGION";
    
    /**
     * Flink API key ; e.g. `H4CDxxxxxxxxxxxx`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_FLINK_API_KEY = "FLINK_API_KEY";
    
    /**
     * Flink API secret ; e.g. `bZEcP+xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx...`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_FLINK_API_SECRET = "FLINK_API_SECRET";
    
    /**
     * Organization ID ; e.g. `78c73cf1-xxxx-xxxx...`
     * <p>
     * Get from {@code https://confluent.cloud/settings/organizations/edit}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_ORG_ID = "ORG_ID";
    
    /**
     * Environment ID ; e.g. `env-xxxxxx`
     * <p>
     * Get from {@code https://confluent.cloud/environments}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_ENV_ID = "ENV_ID";
    
    /**
     * Compute Pool ID ; e.g. `lfcp-xxxxxx`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/pools}
     * </p>
     * @since 1.0.0
     */
    public static final String SYSENV_COMPUTE_POOL_ID = "COMPUTE_POOL_ID";
    
    /**
     * Cloud provider ; e.g. `aws`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_CLOUD_PROVIDER = "--cloud";
    
    /**
     * Cloud region ; e.g. `us-east-1`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_CLOUD_REGION = "--region";
    
    /**
     * Flink API key ; e.g. `H4CDxxxxxxxxxxxx`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_FLINK_API_KEY = "--flink-api-key";
    
    /**
     * Flink API secret ; e.g. `bZEcP+xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx...`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/api-keys/<YOUR_FLINK_API_KEY>}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_FLINK_API_SECRET = "--flink-api-secret";
    
    /**
     * Organization ID ; e.g. `78c73cf1-xxxx-xxxx...`
     * <p>
     * Get from {@code https://confluent.cloud/settings/organizations/edit}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_ORG_ID = "--organization-id";
    
    /**
     * Environment ID ; e.g. `env-xxxxxx`
     * <p>
     * Get from {@code https://confluent.cloud/environments}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_ENV_ID = "--environment-id";
    
    /**
     * Compute Pool ID ; e.g. `lfcp-xxxxxx`
     * <p>
     * Get from {@code https://confluent.cloud/environments/<YOUR_ENV_ID>/flink/pools}
     * </p>
     * @since 1.0.0
     */
    public static final String ARGPARAM_COMPUTE_POOL_ID = "--compute-pool-id";
    
    /**
     * Environment name
     * @since 1.0.0
     * @see System#getenv(java.lang.String) 
     */
    private final String envName;
    
    /**
     * Argument name (from CLI parameters/arguments)
     * @since 1.0.0
     */
    private final String argName;
    
    /**
     * Description for this setting
     * @since 1.0.0
     */
    private final String desc;
    
    /**
     * Get settings that is to be used for {@link io.confluent.flink.plugin.ConfluentSettings#fromArgs(java.lang.String[])}
     * @return                                  Argument based settings
     * @since 1.0.0
     * @see io.confluent.flink.plugin.ConfluentSettings#fromArgs(java.lang.String[])
     */
    @Nonnull
    public static String[] getSettings()
    {
        String[] settings = new String[0];
        for(ConfluentSettingNames confluentSettingName:ConfluentSettingNames.values())
        {
            settings = Stream.concat(
                Arrays.stream(
                    settings
                ), 
                Arrays.stream(
                    new String[]
                    {
                        confluentSettingName.argName,
                        ConfluentSettingNames.getSetting(
                            confluentSettingName
                        ).orElseThrow(
                            () -> new IllegalStateException(
                                String.format(
                                    "System environment name '%s' value IS UNDEFINED",
                                    confluentSettingName.envName
                                )
                            )
                        )
                    }
                )
            ).toArray(
                String[]::new
            );
        }
        return settings;
    }
    
    /**
     * Get setting by looking up arg value first followed by env
     * @param settingName                       Setting name to get. Must not be 
     *                                          {@code null}
     * @param args                              Arguments to process
     * @return                                  An optional environment value if 
     *                                          not {@code null} or empty
     * @since 1.0.0
     * @see ConfluentSettingNames#getArg(java.lang.String...) 
     * @see ConfluentSettingNames#getEnv() 
     */
    @Nonnull
    public static Optional<String> getSetting(@Nonnull
                                              final ConfluentSettingNames settingName, 
                                              final String... args)
    {
        return Optional.ofNullable(
            settingName.getArg(
                args
            ).orElseGet(
                () -> getEnv(
                    settingName.envName
                ).orElse(
                    null
                )
            )
        );
    }

    /**
     * Get environment value
     * @param envName                           Environment name. Must not be 
     *                                          {@code null} or blank/empty
     * @return                                  An optional environment value if 
     *                                          not {@code null} or empty
     * @since 1.0.0
     * @see System#getenv(java.lang.String) 
     */
    @Nonnull
    public static Optional<String> getEnv(@Nonnull
                                          @NotBlank
                                          @NotEmpty
                                          final String envName)
    {
        return Optional.ofNullable(
            System.getenv(
                envName
            )
        ).filter(
            envValueToProcess -> !envValueToProcess.trim().equals(
                ""
            )
        );
    }

    /**
     * Get environment value
     * @return                                  An optional environment value if 
     *                                          not {@code null} or empty
     * @since 1.0.0
     * @see System#getenv(java.lang.String) 
     */
    @Nonnull
    public Optional<String> getEnv()
    {
        return getEnv(
            envName
        );
    }
    
    /**
     * Get CLI argument value
     * @param args                              Arguments to process
     * @return                                  An optional environment value if 
     *                                          not {@code null} or empty
     * @since 1.0.0
     * @see ConfluentSettingNames#getCli(java.lang.String...) 
     */
    @Nonnull
    public Optional<String> getArg(final String... args)
    {
        return Optional.ofNullable(
            args
        ).filter(
            argsToProcess -> argsToProcess.length>0
        ).map(
            argsToProcess -> Optional.ofNullable(
                getCli(
                    argsToProcess
                ).getOptionValue(
                    argName
                )
            ).filter(
                argValueToProcess -> !argValueToProcess.trim().equals(
                    ""
                )
            ).orElse(
                null
            )
        );
    }
    
    /**
     * Get the {@link org.apache.commons.cli.CommandLine} 
     * @param args                              Arguments to process
     * @return                                  {@link org.apache.commons.cli.CommandLine}
     * @throws IllegalStateException            If unable to parse the {@code args} 
     *                                          to {@link org.apache.commons.cli.CommandLine}
     * @since 1.0.0
     */
    @Nonnull
    public static CommandLine getCli(final String... args)
    {
        final Options options = new Options();
        for(ConfluentSettingNames confluentSettingName:ConfluentSettingNames.values())
        {
            options.addOption(
                confluentSettingName.argName, 
                true, 
                confluentSettingName.desc
            );
        }
        final CommandLineParser parser = new DefaultParser();
        try
        {
            return parser.parse(
                options, 
                args
            );
        }
        catch(Exception e)
        {
            throw new IllegalStateException(
                String.format(
                    "Unable to parse args to CLI opts-args ENCOUNTERED FAILURE ; %s --- using args%n\t",
                    e.getMessage(),
                    Arrays.toString(
                        args
                    )
                ),
                e
            );
        }
    }
}
