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
    @Getter(
        AccessLevel.PROTECTED
    )
    @NonNull
    private final ConfluentCloudConfiguration confluentCloudConfiguration;
    
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
    
    @Nonnull
    public Table createTable()
    {
        final String tablePath = getClass().getSimpleName();
        getTableEnvironment().createTable(
            tablePath,
            getTableDescriptor()
        );
        return getTableEnvironment().from(
            tablePath
        );
    }
    
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
    
    @Nonnull
    protected TableDescriptor getTableDescriptor()
    {
        return ConfluentTableDescriptor.forManaged().schema(
            getSchema()
        ).distributedBy(
            4, 
            "uuid"
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
    
    @Nonnull
    protected Schema getSchema()
    {
        return Schema.newBuilder().column(
            "uuid", 
            DataTypes.CHAR(
                36 // `java.util.UUID` contains 32 hex digits along with 4 "-" symbols
            ).notNull()
        ).column(
            "message", // serialized form of `com.github.krakenninja.demo.confluent.models.hello.HelloStreamRecord`
            DataTypes.BYTES().notNull()
        ).primaryKey(
            "uuid"
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
