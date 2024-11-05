package com.github.krakenninja.demo.confluent.cloud;

import com.github.krakenninja.demo.constant.ConfluentSettingNames;
import io.confluent.flink.plugin.ConfluentSettings;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A table program example to get started with the Apache Flink® Table API.
 *
 * <p>
 * It executes two foreground statements in Confluent Cloud. The results of 
 * both statements are printed to the console.
 * </p>
 * @see <a href="https://docs.confluent.io/cloud/current/flink/get-started/quick-start-java-table-api.html">Java Table API Quick Start on Confluent Cloud for Apache Flink</a>
 */
@Slf4j
public class HelloTableApiJUnitTest
{
    @BeforeAll
    public static void setUpClass() {}
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp() {}
    
    @AfterEach
    public void tearDown() {}
    
    // make sure you set in your OS profile (i.e. `~/.zshrc` the following env 
    // name-values (replace "xxx" with your actual values)
    //     export CLOUD_PROVIDER="xxx"
    //     export CLOUD_REGION="xxx"
    //     export FLINK_API_KEY="xxx"
    //     export FLINK_API_SECRET="xxx"
    //     export ORG_ID="xxx"
    //     export ENV_ID="xxx"
    //     export COMPUTE_POOL_ID="xxx"
    @Test
    void tableApi_Print_HelloWorld_Expect_OK()
    {
        // Use the fromArgs(args) method if you want to run with command-line arguments.
        final String[] args = ConfluentSettingNames.getSettings();
        log.info(
            "Using Confluent Settings --- \n\t{}",
            Arrays.toString(
                args
            )
        );
        
        final EnvironmentSettings settings = ConfluentSettings.fromArgs(
            args
        );
        
        // Initialize the session context to get started.
        final TableEnvironment environment = TableEnvironment.create(
            settings
        );
        
        // The Table API centers on 'Table' objects, which help in defining data 
        // pipelines fluently. You can define pipelines fully programmatically
        final Table table = environment.fromValues(
            "Hello world!"
        );
        
        // Once the pipeline is defined, execute it on Confluent Cloud.
        // If no target table has been defined, results are streamed back and 
        // can be printed locally. This can be useful for development and 
        // debugging.
        table.execute().print();
    }
}
