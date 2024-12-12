package com.github.krakenninja.demo.confluent.service.impl;

import com.github.krakenninja.demo.confluent.configuration.ConfluentCloudConfiguration;
import com.github.krakenninja.demo.confluent.service.HelloService;
import com.github.krakenninja.demo.exceptions.InternalException;
import com.github.krakenninja.demo.exceptions.NotFoundException;
import io.confluent.flink.plugin.ConfluentTableDescriptor;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.DataTypes;
import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.lit;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.ValidationException;
import org.springframework.stereotype.Service;

/**
 * A simple {@link com.github.krakenninja.demo.confluent.service.HelloService} 
 * implementation
 * @since 1.0.0
 * @author Christopher CKW
 */
@Slf4j
@Accessors(
    chain = true
)
@Getter
@Setter
@RequiredArgsConstructor
@Service
public class DefaultHelloService
       implements HelloService
{
    /**
     * Table name {@code helloTable}
     * @since 1.0.0
     */
    private static final String TABLE_NAME_HELLOTABLE = "helloTable";
    
    /**
     * Column name {@code uuid}
     * <p>
     * A primary key to uniquely identify this row
     * </p>
     * @since 1.0.0
     */
    private static final String COLUMN_NAME_UUID = "uuid";
    
    /**
     * Column name {@code event_time}
     * <p>
     * Used as a window event time instead of its built-in internal time
     * </p>
     * @since 1.0.0
     */
    private static final String COLUMN_NAME_EVENTTIME = "event_time";
    
    /**
     * Column name {@code who}
     * <p>
     * "who" this message is for
     * </p>
     * @since 1.0.0
     */
    private static final String COLUMN_NAME_WHO = "who";
    
    /**
     * Column name {@code message}
     * <p>
     * "what" is the message
     * </p>
     * @since 1.0.0
     */
    private static final String COLUMN_NAME_MESSAGE = "message";
    
    /**
     * Confluent cloud configuration
     * <p>
     * This will be auto wired
     * </p>
     * @since 1.0.0
     */
    @Getter(
        AccessLevel.PROTECTED
    )
    @NonNull
    private final ConfluentCloudConfiguration confluentCloudConfiguration;
    
    /**
     * Select all "Hello Table" records
     * @return                                  {@link org.apache.flink.table.api.TableResult} 
     *                                          of all the "Hello Table" records, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    @Override
    public TableResult allHellos()
    {
        return allHellos(
            getConfluentCloudConfiguration().getTableEnvironment()
        );
    }
    
    /**
     * Creates the "Hello Table"
     * <p>
     * Currently in Flink's Java API, there is no direct method equivalent to 
     * SQL's IF NOT EXISTS ; thus for us to handle by using 
     * "{@code execute SQL statement 'CREATE TABLE IF NOT EXISTS'} programmatically
     * </p>
     * <p>
     * We will however attempt to apply {@code try-catch} instead in this 
     * default implementation
     * </p>
     * @return                                  {@link org.apache.flink.table.api.Table} 
     *                                          of the created "Hello Table", 
     *                                          never {@code null}
     * @throws InternalException                If unable to create/get the 
     *                                          created {@link org.apache.flink.table.api.Table}
     * @since 1.0.0
     * @see <a href="https://github.com/confluentinc/learn-apache-flink-table-api-for-java-exercises/blob/main/solutions/03-building-a-streaming-pipeline/src/main/java/marketplace/OrderService.java#L25C12-L25C23">solutions/03-building-a-streaming-pipeline/src/main/java/marketplace/OrderService.java</a>
     */
    @Override
    public Table createHelloTable()
    {
        return createHelloTable(
            getConfluentCloudConfiguration().getTableEnvironment()
        );
    }
    
    /**
     * Get all hello record(s)
     * @param tableEnvironment                  Table environment to use to 
     *                                          get all of the "Hello Table" 
     *                                          record(s). Must not be 
     *                                          {@code null}
     * @return                                  {@link TableResult}, never 
     *                                          {@code null}
     * @since 1.0.0
     * @see <a href="https://github.com/confluentinc/learn-apache-flink-table-api-for-java-exercises/blob/main/solutions/02-querying-flink-tables/src/main/java/marketplace/CustomerService.java#L20">solutions/02-querying-flink-tables/src/main/java/marketplace/CustomerService.java</a>
     * @see <a href="https://docs.confluent.io/platform/current/streams/code-examples.html">Kafka Streams Code Examples for Confluent Platform</a>
     */
    @Nonnull
    protected TableResult allHellos(@Nonnull
                                    final TableEnvironment tableEnvironment)
    {
        final TableEnvironment env = getConfiguredTableEnvironment(
            tableEnvironment
        );
        
        return getTable(
            env
        ).select(
            $(
                COLUMN_NAME_UUID
            ),
            $(
                COLUMN_NAME_WHO
            ),
            $(
                COLUMN_NAME_MESSAGE
            ),
            $(
                COLUMN_NAME_EVENTTIME
            )
        ).execute();
    }
    
    /**
     * Creates the "Hello Table"
     * <p>
     * Currently in Flink's Java API, there is no direct method equivalent to 
     * SQL's IF NOT EXISTS ; thus for us to handle by using 
     * "{@code execute SQL statement 'CREATE TABLE IF NOT EXISTS'} programmatically
     * </p>
     * <p>
     * We will however attempt to apply {@code try-catch} instead in this 
     * default implementation
     * </p>
     * @param tableEnvironment                  Table environment to use to 
     *                                          create the "Hello Table". Must 
     *                                          not be {@code null}. See also 
     *                                          {@link ConfluentCloudConfiguration#getTableEnvironment()} 
     *                                          bean reference
     * @return                                  {@link org.apache.flink.table.api.Table} 
     *                                          of the created "Hello Table", 
     *                                          never {@code null}
     * @throws InternalException                If unable to create/get the 
     *                                          created {@link org.apache.flink.table.api.Table}
     * @since 1.0.0
     * @see <a href="https://github.com/confluentinc/learn-apache-flink-table-api-for-java-exercises/blob/main/solutions/03-building-a-streaming-pipeline/src/main/java/marketplace/OrderService.java#L25C12-L25C23">solutions/03-building-a-streaming-pipeline/src/main/java/marketplace/OrderService.java</a>
     */
    protected Table createHelloTable(@Nonnull
                                     final TableEnvironment tableEnvironment)
    {
        final TableEnvironment env = getConfiguredTableEnvironment(
            tableEnvironment
        );
        
        try
        {
            return getTable(
                env
            );
        }
        catch(NotFoundException nfe)
        {
            try
            {
                // create the table
                env.createTable(
                    TABLE_NAME_HELLOTABLE, 
                    getTableDescriptor()
                );
            }
            catch(Exception e)
            {
                throw new InternalException(
                    String.format(
                        "Create table '%s' ENCOUNTERED FAILURE ; %s",
                        TABLE_NAME_HELLOTABLE,
                        e.getMessage()
                    ),
                    e
                );
            }
            return getTable(
                env
            );
        }
    }
    
    /**
     * Get configured table environment (defaults to pre-configure it to use 
     * if available the user defined {@code catalog} and/or {@code database})
     * @param tableEnvironment                  Table environment to set/define 
     *                                          use {@code catalog} and/or 
     *                                          {@code database}. Must 
     *                                          not be {@code null}
     * @return                                  Configured table environment, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    protected TableEnvironment getConfiguredTableEnvironment(@Nonnull
                                                             final TableEnvironment tableEnvironment)
    {
        // use defined catalog (if available)
        Optional.ofNullable(
            getConfluentCloudConfiguration().getTableApi().getUseCatalog()
        ).filter(
            useCatalogToProcess -> !useCatalogToProcess.trim().equals(
                ""
            )
        ).ifPresent(
            tableEnvironment::useCatalog
        );
        
        // use defined database (if available)
        Optional.ofNullable(
            getConfluentCloudConfiguration().getTableApi().getUseDatabase()
        ).filter(
            useDatabaseToProcess -> !useDatabaseToProcess.trim().equals(
                ""
            )
        ).ifPresent(
            tableEnvironment::useDatabase
        );
        
        return tableEnvironment;
    }
    
    /**
     * Get table
     * @param tableEnvironment                  Table environment to use to 
     *                                          create the "Hello Table". Must 
     *                                          not be {@code null}
     * @return                                  {@link org.apache.flink.table.api.Table} 
     *                                          IF EXIST, never {@code null}
     * @throws NotFoundException                If table NOT EXIST
     * @throws InternalException                If unable to get table
     * @since 1.0.0
     */
    @Nonnull
    protected Table getTable(@Nonnull
                             final TableEnvironment tableEnvironment)
    {
        try
        {
            // to counter the lack of support using Java API for table 
            // 'IF NOT EXISTS'
            return tableEnvironment.from(
                TABLE_NAME_HELLOTABLE
            );
        }
        catch(ValidationException e)
        {
            if(e.getMessage().contains(
                String.format(
                    "Table `%s` was not found",
                    TABLE_NAME_HELLOTABLE
                )
            ))
            {
                throw new NotFoundException(
                    String.format(
                        "Table '%s' IS NOT FOUND ; using table environment catalog '%s', database '%s'",
                        TABLE_NAME_HELLOTABLE,
                        tableEnvironment.getCurrentCatalog(),
                        tableEnvironment.getCurrentDatabase()
                    )
                );
            }
            throw new InternalException(
                String.format(
                    "Get table '%s' ENCOUNTERED FAILURE ; %s%n\tusing table environment catalog '%s', database '%s'",
                    TABLE_NAME_HELLOTABLE,
                    e.getMessage(),
                    tableEnvironment.getCurrentCatalog(),
                    tableEnvironment.getCurrentDatabase()
                ),
                e
            );
        }
    }
    
    /**
     * Table descriptor for creating table programmatically
     * @return                                  {@link org.apache.flink.table.api.TableDescriptor}, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    protected TableDescriptor getTableDescriptor()
    {
        // Create a table programmatically:
        // The table...
        //   - is backed by an equally named "HelloTableRecord" Kafka topic
        //   - stores its payload in JSON
        //   - will reference two Schema Registry subjects for Kafka message key and value
        //   - is distributed across 4 Kafka partitions based on the Kafka message key "uuid"
        // Supported options:
        //    changelog.mode
        //    connector
        //    kafka.cleanup-policy
        //    kafka.consumer.isolation-level
        //    kafka.max-message-size
        //    kafka.producer.compression.type
        //    kafka.retention.size
        //    kafka.retention.time
        //    key.fields-prefix
        //    key.format
        //    key.json-registry.schema-context
        //    key.json-registry.subject-name
        //    key.json-registry.validate-writes
        //    key.json-registry.wire-encoding
        //    scan.bounded.mode
        //    scan.bounded.specific-offsets
        //    scan.bounded.timestamp-millis
        //    scan.startup.mode
        //    scan.startup.specific-offsets
        //    scan.startup.timestamp-millis
        //    value.fields-include
        //    value.format
        //    value.json-registry.schema-context
        //    value.json-registry.subject-name
        //    value.json-registry.validate-writes
        //    value.json-registry.wire-encoding
        return ConfluentTableDescriptor.forManaged().schema(
            getSchema()
        ).distributedInto(
            1
        ).option(
            "kafka.retention.time", 
            "1 h"
        ).option(
            "scan.startup.mode", 
            "earliest-offset"
        ).build();
    }
    
    /**
     * Table schema for creating table programmatically
     * @return                                  {@link org.apache.flink.table.api.Schema}, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    protected Schema getSchema()
    {
        return Schema.newBuilder().column(
            COLUMN_NAME_UUID, 
            DataTypes.CHAR(
                36 // `java.util.UUID` contains 32 hex digits along with 4 "-" symbols
            ).notNull()
        ).column(
            COLUMN_NAME_WHO, 
            DataTypes.STRING()
        ).column(
            COLUMN_NAME_MESSAGE, 
            DataTypes.STRING()
        ).column(
            COLUMN_NAME_EVENTTIME, 
            DataTypes.TIMESTAMP_LTZ(
                3 // see `org.apache.flink.table.types.logical.LocalZonedTimestampType`
                  // Range: 0 to 9 (inclusive)
                  //     0: No fractional seconds
                  //     3: Millisecond precision (default)
                  //     6: Microsecond precision
                  //     9: Nanosecond precision
            )
        ) .primaryKey(
            COLUMN_NAME_UUID
        ).watermark(
            COLUMN_NAME_EVENTTIME, 
            $(
                COLUMN_NAME_EVENTTIME
            ).minus(
                lit(
                    5
                ).seconds()
            )
        ).build();
    }
}
