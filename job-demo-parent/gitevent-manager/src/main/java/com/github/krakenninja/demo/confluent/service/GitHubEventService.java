package com.github.krakenninja.demo.confluent.service;

import jakarta.annotation.Nonnull;
import org.apache.flink.table.api.TableEnvironment;

/**
 * GitHub event service
 * <p>
 * Uses Flink Table API to obtain GitHub event(s) for processing 
 * </p>
 * @since 1.0.0
 * @author Christopher CKW
 */
public interface GitHubEventService
{
    void stream();
}
