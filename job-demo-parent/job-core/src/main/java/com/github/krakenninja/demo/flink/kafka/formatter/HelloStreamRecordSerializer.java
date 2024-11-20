package com.github.krakenninja.demo.flink.kafka.formatter;

import com.github.krakenninja.demo.models.imp.HelloStreamRecord;
import org.apache.flink.formats.json.JsonSerializationSchema;

/**
 * Flink Kafka {@link com.github.krakenninja.demo.models.imp.HelloStreamRecord} 
 * JSON serialization schema implementation
 * @since 1.0.0
 * @author Christopher CKW
 */
public class HelloStreamRecordSerializer 
       extends JsonSerializationSchema<HelloStreamRecord>
{
    /**
     * Constructor to create a new {@link org.apache.flink.formats.json.JsonDeserializationSchema} 
     * instance
     * @since 1.0.0
     */
    public HelloStreamRecordSerializer()
    {
        super(
            () -> new org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.JsonMapper().configure(
                org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, 
                false
            ).setSerializationInclusion(
                org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
            )
        );
    }
}
