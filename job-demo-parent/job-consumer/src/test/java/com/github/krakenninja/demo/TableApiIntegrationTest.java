package com.github.krakenninja.demo;

import com.github.krakenninja.demo.flink.configuration.ConfluentCloudConfiguration;
import io.confluent.flink.plugin.ConfluentTools;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.types.Row;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * To test basic/simple table API integration
 * <p>
 * Make sure you set your system environment with the following KVs : 
 * <pre>
 *   export CONFLUENT_CLOUD_CLI_CLOUD_TYPE="xxx"
 *   export CONFLUENT_CLOUD_CLI_CLOUD_REGION="xx-xxx-x"
 *   export CONFLUENT_CLOUD_CLI_ORGANIZATION_ID="00000000-0000-0000-0000-000000000000"
 *   export CONFLUENT_CLOUD_CLI_ENVIRONMENT_ID="env-xxxxxx"
 *   export CONFLUENT_CLOUD_CLI_COMPUTE_POOL_ID="lfcp-xxxxxx"
 *   export CONFLUENT_CLOUD_CLI_FLINK_API_KEY="xxxxxxxxxxxxxxxx"
 *   export CONFLUENT_CLOUD_CLI_FLINK_API_SECRET="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
 * </pre>
 * </p>
 * <p>
 * Then you can run this integration test using command below : 
 * <pre>
 *   cd job-demo-parent
 *   mvn clean install
 *   mvn test -f job-consumer/pom.xml -Dtest=com.github.krakenninja.demo.TableApiIntegrationTest
 * </pre>
 * </p>
 * @since 1.0.0
 * @author Christopher CKW
 * @see <a href="https://github.com/confluentinc/flink-table-api-java-examples">Java Examples for Table API on Confluent Cloud</a>
 */
@Slf4j
@SpringBootTest
@ExtendWith(
    MockitoExtension.class
)
public class TableApiIntegrationTest
{
    @Autowired(
        required = false
    )
    private ConfluentCloudConfiguration confluentCloudConfiguration;
    
    @Autowired(
        required = false
    )
    private EnvironmentSettings environmentSettings;
    
    @BeforeAll
    public static void setUpClass() {}
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp() {}
    
    @AfterEach
    public void tearDown() {}

    /**
     * If this runs, your Confluent Cloud setup and configuration is GOOD & 
     * READY
     */
    @Test
    public void check_ConfluentCloudConfiguration_Expect_OK()
    {
        assertNotNull(
            confluentCloudConfiguration
        );
        
        assertNotNull(
            environmentSettings
        );
        
        // initialize the session context to get started
        final TableEnvironment tableEnvironment = TableEnvironment.create(
            environmentSettings
        );

        // the Table API is centered around 'Table' objects which help in 
        // defining data pipelinesfluently. Pipelines can be defined fully 
        // programmatic...
        final Table table = tableEnvironment.fromValues(
            "Hello world!"
        );
        // ... or with embedded Flink SQL
        // Table table = env.sqlQuery("SELECT 'Hello world!'");

        // once the pipeline is defined, execute it on Confluent Cloud. If no 
        // target table has been defined, results are streamed back and can be 
        // printed locally. This can be useful for development and debugging.
        table.execute().print();

        // results can not only be printed but also collected locally and 
        // accessed individually. This can be useful for testing
        final Table moreHellos = tableEnvironment.fromValues(
            "Hello Bob", 
            "Hello Alice", 
            "Hello Peter"
        ).as(
            "greeting"
        );
        final List<Row> rows = ConfluentTools.collectChangelog(
            moreHellos, 
            10
        );
        rows.forEach(
            row -> {
                final String column = row.getFieldAs(
                    "greeting"
                );
                log.info(
                    "Greeting: {}",
                    column
                );
            }
        );
    }
}
