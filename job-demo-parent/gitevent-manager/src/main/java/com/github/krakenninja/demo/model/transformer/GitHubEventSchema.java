package com.github.krakenninja.demo.model.transformer;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.InputStream;

/**
 * GitHub event schema
 * @author Christopher CKW
 * @since 1.0.0
 * @see <a href="https://docs.github.com/en/enterprise-cloud@latest/admin/monitoring-activity-in-your-enterprise/reviewing-audit-logs-for-your-enterprise/audit-log-events-for-your-enterprise">Audit log events for your enterprise</a>
 * @see <a href="https://docs.github.com/en/enterprise-cloud@latest/organizations/keeping-your-organization-secure/managing-security-settings-for-your-organization/audit-log-events-for-your-organization">Audit log events for your organization</a>
 * @see <a href="https://docs.github.com/en/enterprise-cloud@latest/organizations/keeping-your-organization-secure/managing-security-settings-for-your-organization/reviewing-the-audit-log-for-your-organization">Reviewing the audit log for your organization</a>
 */
public interface GitHubEventSchema
{
    /**
     * Transform the JSON input stream to {@link T} typed
     * @param <T>                               Concrete subclass type to 
     *                                          transform to
     * @param jsonStream                        JSON input stream to transform. 
     *                                          Must not be {@code null}
     * @param toType                            Transform the {@code jsonStream} 
     *                                          to typed. Must not be {@code null}
     * @return                                  {@link T} object ; may be 
     *                                          {@code null} depending on 
     *                                          implementation
     * @since 1.0.0
     */
    @Nullable
    <T extends Object> T transform(@Nonnull
                                   final InputStream jsonStream,
                                   @Nonnull
                                   final TypeReference<T> toType);
    
    /**
     * Transform the JSON string to {@link T} typed
     * @param <T>                               Concrete subclass type to 
     *                                          transform to
     * @param json                              JSON string to validate. Must not 
     *                                          be {@code null} or blank/empty
     * @param toType                            Transform the {@code jsonStream} 
     *                                          to typed. Must not be {@code null}
     * @return                                  {@link T} object ; may be 
     *                                          {@code null} depending on 
     *                                          implementation
     * @since 1.0.0
     */
    @Nullable
    <T extends Object> T transform(@Nonnull
                                   @NotBlank
                                   @NotEmpty
                                   final String json,
                                   @Nonnull
                                   final TypeReference<T> toType);
    
    /**
     * Validate the JSON input stream
     * @param jsonStream                        JSON input stream to validate. 
     *                                          Must not be {@code null}
     * @return                                  {@code true} if no validation 
     *                                          errors, otherwise {@code false}
     * @since 1.0.0
     */
    boolean validate(@Nonnull
                     final InputStream jsonStream);
    
    /**
     * Validate the JSON string
     * @param json                              JSON string to validate. Must not 
     *                                          be {@code null} or blank/empty
     * @return                                  {@code true} if no validation 
     *                                          errors, otherwise {@code false}
     * @since 1.0.0
     */
    boolean validate(@Nonnull
                     @NotBlank
                     @NotEmpty
                     final String json);
}
