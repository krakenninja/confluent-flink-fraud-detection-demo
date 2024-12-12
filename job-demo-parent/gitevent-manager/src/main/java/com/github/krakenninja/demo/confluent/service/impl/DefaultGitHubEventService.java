package com.github.krakenninja.demo.confluent.service.impl;

import com.github.krakenninja.demo.confluent.configuration.ConfluentCloudConfiguration;
import com.github.krakenninja.demo.confluent.service.GitHubEventService;
import com.github.krakenninja.demo.exceptions.InternalException;
import io.confluent.flink.plugin.ConfluentSettings;
import io.confluent.flink.plugin.ConfluentTableDescriptor;
import io.confluent.flink.plugin.ConfluentTools;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import static org.apache.flink.table.api.Expressions.*;
import org.apache.flink.table.api.FormatDescriptor;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TablePipeline;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.springframework.stereotype.Service;

/**
 * Default GitHub event service implementation
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
public class DefaultGitHubEventService
       implements GitHubEventService
{
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
    
    @Override
    public void stream()
    {
//        EnvironmentSettings settings = ConfluentSettings.fromResource(
//            "/cloud.properties"
//        );
//        TableEnvironment env = TableEnvironment.create(
//            settings
//        );
        
        
        stream(
            getConfiguredTableEnvironment(
                confluentCloudConfiguration.getTableEnvironment()
//                env
            )
        );
    }
    
    protected void stream(final TableEnvironment tableEnvironment)
    {
//        tableEnvironment.from(
//            "crediogithub"
//        ).select(
//            $(
//                "*"
//            )
//        ).execute().print();
        
//        tableEnvironment.sqlQuery(
//            "SELECT *, JSON_VALUE(val, '$.@timestamp' RETURNING INT) AS ts FROM crediogithub ORDER BY ts LIMIT 5"
//        ).execute().print();
        
        // table descriptor
        final TableDescriptor tableDescriptor = ConfluentTableDescriptor.forManaged().schema(
            org.apache.flink.table.api.Schema.newBuilder().column(
                "gitevent_json", 
                DataTypes.STRING()
            ).build()
        ).distributedInto(
            1
        ).build();
        
        // create datastream managed table
        tableEnvironment.createTable(
            "crediogithub_process",
            tableDescriptor
        );
        
        // select key, val, $rowtime from `fraud-detection-demo`.`cluster_fraud_detection_demo_0`.`crediogithub`
        //  WHERE $rowtime BETWEEN TIMESTAMP '2024-12-05 00:00:00' AND TIMESTAMP '2024-12-05 23:59:59' LIMIT 5; 
        
        final TableResult result = tableEnvironment.sqlQuery(
            "SELECT CAST(`val` AS STRING) AS gitevent_json FROM crediogithub LIMIT 5"
        ).execute();
        final List<Row> resultRows = ConfluentTools.collectMaterialized(
            result
        );
        
        final List<Row> giteventJsons = new ArrayList<>();
        resultRows.stream().forEach(
            resultRowToProcess -> {
                final String giteventJson = resultRowToProcess.<String>getFieldAs(
                    "gitevent_json"
                );

                log.info("Got ROW field name '{}' AS JAVA-TYPE `{}`",
                    "gitevent_json",
                    Optional.ofNullable(
                        giteventJson
                    ).map(
                        giteventJsonToProcess -> giteventJsonToProcess.getClass().getName()
                    ).orElse(
                        null
                    )
                );
                giteventJsons.add(
                    Row.of(
                        giteventJson
                    )
                );
            }
        );
        log.info("Processed row SOURCE '{}' COUNT : ",
            "crediogithub",
            giteventJsons.size()
        );
        
        final TablePipeline pipeline = tableEnvironment.fromValues(
            giteventJsons
        ).insertInto(
            "crediogithub_process"
        );
        log.info("Insert pipeline SOURCE '{}' TO TARGET '{}'",
            "crediogithub",
            "crediogithub_process"
        );
        
        // execution happens async by default, use await() to attach to the 
        // execution in case all sources are finite (i.e. bounded).
        // For infinite (i.e. unbounded) sources, waiting for completion would 
        // not make much sense.
        try
        {
            pipeline.execute();
        }
        catch(Exception e)
        {
            throw new InternalException(
                String.format(
                    "Pipeline execution ENCOUNTERED FAILURE ; %s",
                    e.getMessage()
                ),
                e
            );
        }
        log.info("Executed insert pipeline SOURCE '{}' TO TARGET '{}'",
            "crediogithub",
            "crediogithub_process"
        );
        
        tableEnvironment.sqlQuery(
            "SELECT * FROM crediogithub_process"
        ).execute().print();
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
}
