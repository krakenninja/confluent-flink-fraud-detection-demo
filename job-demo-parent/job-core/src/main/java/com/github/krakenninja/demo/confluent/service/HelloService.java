package com.github.krakenninja.demo.confluent.service;

import jakarta.annotation.Nonnull;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;

/**
 * A simple "Hello Service" definition
 * @since 1.0.0
 * @author Christopher CKW
 */
public interface HelloService
{
    /**
     * Creates the "Hello Table"
     * @return                                  {@link org.apache.flink.table.api.Table} 
     *                                          of the created "Hello Table", 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    Table createHelloTable();
    
    /**
     * Select all "Hello Table" records
     * @return                                  {@link org.apache.flink.table.api.TableResult} 
     *                                          of all the "Hello Table" records, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    TableResult allHellos();
}
