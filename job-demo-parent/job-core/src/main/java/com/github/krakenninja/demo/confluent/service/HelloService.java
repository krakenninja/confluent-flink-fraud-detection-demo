package com.github.krakenninja.demo.confluent.service;

import jakarta.annotation.Nonnull;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

/**
 * A simple "Hello Service" definition
 * @since 1.0.0
 * @author Christopher CKW
 */
public interface HelloService
{
    /**
     * Creates the "Hello Table"
     * @return                                  {@link org.apache.flink.table.api.TableResult} 
     *                                          of the created "Hello Table", 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    Table createHelloTable();
}
