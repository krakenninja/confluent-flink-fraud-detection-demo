package com.github.krakenninja.demo.confluent.schema.hello;

import com.github.krakenninja.demo.confluent.configuration.ConfluentCloudConfiguration;
import com.github.krakenninja.demo.exceptions.InternalException;
import io.confluent.flink.plugin.ConfluentTableDescriptor;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.flink.table.api.DataTypes;
import static org.apache.flink.table.api.Expressions.*;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.ValidationException;
import org.springframework.stereotype.Component;

/**
 * Table for {@link com.github.krakenninja.demo.confluent.models.hello.HelloStreamRecord}
 * @author Christopher CKW
 * @since 1.0.0
 * @see <a href="https://docs.confluent.io/cloud/current/flink/reference/table-api.html">Confluent table descriptor</a>
 * @see <a href="https://docs.confluent.io/cloud/current/flink/reference/statements/create-table.html">CREATE TABLE Statement in Confluent Cloud for Apache Flink</a>
 * @see <a href="https://www.confluent.io/blog/getting-started-with-apache-flink-table-api/">Your Guide to the Apache Flink® Table API: An In-Depth Exploration</a>
 * @see <a href="https://docs.confluent.io/platform/current/ksqldb/developer-guide/ksqldb-reference/create-table.html">CREATE TABLE statement in ksqlDB for Confluent Platform</a>
 */
@Accessors(
    chain = true
)
@Getter
@Setter
@RequiredArgsConstructor
@Component
public class HelloTableRecord
{
    /**
     * Reference to the configuration {@link com.github.krakenninja.demo.confluent.configuration.ConfluentCloudConfiguration}
     * @since 1.0.0
     */
    @Getter(
        AccessLevel.PROTECTED
    )
    @NonNull
    private final ConfluentCloudConfiguration confluentCloudConfiguration;
    
    /**
     * Column name {@code uuid}
     * @since 1.0.0
     */
    private static final String COLUMN_NAME_UUID = "uuid";
    
    /**
     * Column name {@code message}
     * <p>
     * Holds bytes of {@link com.github.krakenninja.demo.confluent.models.hello.HelloStreamRecord#toBytes()} 
     * as the message value
     * </p>
     * @since 1.0.0
     */
    private static final String COLUMN_NAME_MESSAGE = "message";
    
    /**
     * Get table for model {@link com.github.krakenninja.demo.confluent.models.hello.HelloStreamRecord}
     * <p>
     * If the table not found, it attempts to call {@link #createTable()}
     * </p>
     * @return                                  {@link org.apache.flink.table.api.Table}, 
     *                                          never {@code null}
     * @throws InternalException                If unable to get the table
     * @since 1.0.0
     */
    @Nonnull
    public Table getTable()
    {
        final String tablePath = getClass().getSimpleName();
        try
        {
            try
            {
                return getTableEnvironment().from(
                    tablePath
                );
            }
            catch(ValidationException e)
            {
                if(e.getMessage().contains(
                    String.format(
                        "Table `%s` was not found",
                        tablePath
                    )
                ))
                {
                    return createTable();
                }
                throw e;
            }
        }
        catch(InternalException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new InternalException(
                String.format(
                    "Get table '%s' ENCOUNTERED FAILURE ; %s",
                    tablePath,
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Create table for model {@link com.github.krakenninja.demo.confluent.models.hello.HelloStreamRecord}
     * @return                                  {@link org.apache.flink.table.api.Table}, 
     *                                          never {@code null}
     * @throws InternalException                If unable to create the table
     * @since 1.0.0
     */
    @Nonnull
    public Table createTable()
    {
        final String tablePath = getClass().getSimpleName();
        try
        {
            getTableEnvironment().createTable(
                tablePath,
                getTableDescriptor()
            );
            return getTableEnvironment().from(
                tablePath
            );
        }
        catch(Exception e)
        {
            throw new InternalException(
                String.format(
                    "Create table '%s' ENCOUNTERED FAILURE ; %s",
                    tablePath,
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Get {@link org.apache.flink.table.api.TableEnvironment} specific to 
     * manage the CRUD operation(s) on this table 
     * {@link com.github.krakenninja.demo.confluent.schema.hello.HelloTableRecord} 
     * type
     * @return                                  {@link org.apache.flink.table.api.TableEnvironment}, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @Nonnull
    protected TableEnvironment getTableEnvironment()
    {
        final TableEnvironment tableEnvironment = TableEnvironment.create(
            getConfluentCloudConfiguration().getConfluentSettings()
        );
        tableEnvironment.useCatalog(
            getConfluentCloudConfiguration().getTableApi().getUseCatalog()
        );
        tableEnvironment.useDatabase(
            getConfluentCloudConfiguration().getTableApi().getUseDatabase()
        );
        return tableEnvironment;
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
        return ConfluentTableDescriptor.forManaged().schema(
            getSchema()
        ).distributedBy(
            4, 
            COLUMN_NAME_UUID
        ).option(
            "kafka.retention.time", 
            "0"
        ).option(
            "key.format", 
            "json-registry"
        ).option(
            "value.format", 
            "json-registry"
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
            COLUMN_NAME_MESSAGE, // serialized form of `com.github.krakenninja.demo.confluent.models.hello.HelloStreamRecord`
            DataTypes.BYTES().notNull()
        ).primaryKey(
            COLUMN_NAME_UUID
        ).watermark(
            // access $rowtime system column
            // https://docs.confluent.io/cloud/current/flink/concepts/timely-stream-processing.html
            "$rowtime", 
            $(
                "$rowtime"
            ).minus(
                lit(
                    5
                ).seconds()
            )
        ).build();
    }
}
