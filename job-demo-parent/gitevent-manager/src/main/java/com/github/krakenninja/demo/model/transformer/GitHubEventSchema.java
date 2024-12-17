package com.github.krakenninja.demo.model.transformer;

import jakarta.annotation.Nonnull;
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
