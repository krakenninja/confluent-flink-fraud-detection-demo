package com.github.krakenninja.demo.flink.kafka.formatter;

import com.github.krakenninja.demo.models.imp.HelloStreamRecord;
import org.apache.flink.formats.json.JsonDeserializationSchema;

/**
 * Flink Kafka {@link com.github.krakenninja.demo.models.imp.HelloStreamRecord} 
 * JSON de-serialization schema implementation
 * @since 1.0.0
 * @author Christopher CKW
 */
public class HelloStreamRecordDeserializer 
       extends JsonDeserializationSchema<HelloStreamRecord>
{
    /**
     * Constructor to create a new {@link org.apache.flink.formats.json.JsonDeserializationSchema} 
     * instance
     * @since 1.0.0
     */
    public HelloStreamRecordDeserializer()
    {
        super(
            HelloStreamRecord.class,
            () -> new org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.json.JsonMapper().configure(
                org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, 
                false
            ).setSerializationInclusion(
                org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
            )
        );
    }
}
