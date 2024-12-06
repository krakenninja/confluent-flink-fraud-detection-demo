package com.github.krakenninja.demo;

import com.github.krakenninja.demo.confluent.configuration.ConfluentCloudConfiguration;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
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
 *   export CONFLUENT_TABLE_API_LOCAL_TIMEZONE="UTC"
 *   export CONFLUENT_TABLE_API_USE_CATALOG="xxxxxxxxxxxxxxxx"
 *   export CONFLUENT_TABLE_API_USE_DATABASE="xxxxxxxxxxxxxxxx"
 * </pre>
 * </p>
 * <p>
 * Then you can run this integration test using command below : 
 * <pre>
 *   cd job-demo-parent
 *   mvn clean install
 *   mvn test -f gitevent-manager/pom.xml -Dtest=com.github.krakenninja.demo.GitEventMgrMainIntegrationTest
 * </pre>
 * </p>
 * @since 1.0.0
 * @author Christopher CKW
 * @see <a href="https://github.com/confluentinc/learn-apache-flink-table-api-for-java-exercises">Apache Flink Table API for Java</a>
 * @see <a href="https://github.com/confluentinc/flink-table-api-java-examples">Java Examples for Table API on Confluent Cloud</a>
 * @see <a href="https://docs.confluent.io/cloud/current/flink/get-started/quick-start-java-table-api.html">Java Table API Quick Start on Confluent Cloud for Apache Flink</a>
 */
@Slf4j
@SpringBootTest
@ExtendWith(
    MockitoExtension.class
)
@TestMethodOrder(
    MethodOrderer.OrderAnnotation.class
)
public class GitEventMgrMainIntegrationTest
{
    @Autowired
    private ConfluentCloudConfiguration confluentCloudConfiguration;
    
    @BeforeAll
    public static void setUpClass() {}
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp() {}
    
    @AfterEach
    public void tearDown() {}
    
    /**
     * Taken out lesson {@code 01-connecting-to-confluent-cloud} to a Unit Test
     * <p>
     * On success, you will be able to see the following in console :
     * <pre>
     *   [main] INFO com.github.krakenninja.demo.GitEventMgrMainIntegrationTest - clicks
     *   [main] INFO com.github.krakenninja.demo.GitEventMgrMainIntegrationTest - customers
     *   [main] INFO com.github.krakenninja.demo.GitEventMgrMainIntegrationTest - orders
     *   [main] INFO com.github.krakenninja.demo.GitEventMgrMainIntegrationTest - products
     * </pre>
     * </p>
     * <p>
     * @see <a href="https://github.com/confluentinc/learn-apache-flink-table-api-for-java-exercises/tree/main/solutions/01-connecting-to-confluent-cloud">01-connecting-to-confluent-cloud</a>
     */
    @Order(1)
    @Test
    void learnConfluentCloud_Marketplace_Expect_OK()
    {
        final String catalogName = "examples";
        
        final String databaseName = "marketplace";
        
        final EnvironmentSettings environmentSettings = confluentCloudConfiguration.getConfluentSettings();
        assertNotNull(
            environmentSettings
        );
        
        final TableEnvironment tableEnvironment = TableEnvironment.create(
            environmentSettings
        );
        assertNotNull(
            tableEnvironment
        );
        
        tableEnvironment.useCatalog(
            catalogName
        );
        
        tableEnvironment.useDatabase(
            databaseName
        );
        
        Arrays.stream(
            tableEnvironment.listTables()
        ).forEach(
            log::info
        );
    }
}
