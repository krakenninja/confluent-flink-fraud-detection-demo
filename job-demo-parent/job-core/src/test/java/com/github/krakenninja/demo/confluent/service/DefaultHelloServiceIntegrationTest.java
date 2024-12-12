package com.github.krakenninja.demo.confluent.service;

import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
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
 * To test custom/defined table API integration
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
 *   mvn test -f job-core/pom.xml -Dtest=com.github.krakenninja.demo.confluent.service.DefaultHelloServiceIntegrationTest
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
public class DefaultHelloServiceIntegrationTest
{
    @Autowired
    private HelloService helloService;

    @BeforeAll
    public static void setUpClass() {}
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp() {}
    
    @AfterEach
    public void tearDown() {}

    /**
     * Following calls the {@link com.github.krakenninja.demo.confluent.service.HelloService#createHelloTable()}
     * @since 1.0.0
     */
    @Order(1)
    @Test
    public void defaultHelloService_CreateHelloTable_Expect_OK()
    {
        final Table helloTable = helloService.createHelloTable();
        assertNotNull(
            helloTable
        );
        
        //    2024-12-03T12:43:57.746+08:00  INFO 46263 --- [Core (TEST)] [           main] d.c.s.DefaultHelloServiceIntegrationTest : Successfully created table 'Hello Table' --- schema details below ...
        //    (
        //      `uuid` CHAR(36) NOT NULL,
        //      `who` STRING,
        //      `message` STRING,
        //      `event_time` TIMESTAMP_LTZ(3) *ROWTIME*,
        //      `$rowtime` TIMESTAMP_LTZ(3) NOT NULL METADATA VIRTUAL COMMENT 'SYSTEM',
        //      WATERMARK FOR `event_time`: TIMESTAMP_LTZ(3) AS `event_time` - INTERVAL '0 00:00:05.0' DAY TO SECOND(3),
        //      CONSTRAINT `PRIMARY` PRIMARY KEY (`uuid`) NOT ENFORCED
        //    )
        log.info(
            "Successfully created table 'Hello Table' --- schema details below ..."
        );
        helloTable.printSchema();
    }
    
    /**
     * Following calls the {@link com.github.krakenninja.demo.confluent.service.HelloService#allHellos()}
     * @since 1.0.0
     */
    @Order(2)
    @Test
    public void defaultHelloService_AllHellos_Expect_OK_NoResults()
    {
        final TableResult helloTableResult = helloService.allHellos();
        assertNotNull(
            helloTableResult
        );
        
        final Iterable<Row> iterable = helloTableResult::collect;
        assertNotNull(
            iterable
        );
        
        final List<Row> actual = StreamSupport.stream(
            iterable.spliterator(), 
            false
        ).limit(
            5
        ).toList();
        assertNotNull(
            actual
        );
        assertTrue(
            actual.isEmpty()
        );
        log.info(
            "Successfully retrieved all table 'Hello Table' records expecting NO RESULT"
        );
    }
}
