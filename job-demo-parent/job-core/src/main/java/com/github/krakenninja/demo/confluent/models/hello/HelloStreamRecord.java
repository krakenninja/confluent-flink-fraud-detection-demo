package com.github.krakenninja.demo.confluent.models.hello;

import com.github.krakenninja.demo.models.abs.BaseStreamRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A simple hello stream record to record how many times the "who" had been 
 * greeted hello
 * @since 1.0.0
 * @author Christopher CKW
 */
@Data
@EqualsAndHashCode(
    callSuper = true,
    onlyExplicitlyIncluded = true
)
@Accessors(
    chain = true,
    fluent = false
)
@Getter
@Setter
public class HelloStreamRecord
       extends BaseStreamRecord<HelloStreamRecord>
{
    @EqualsAndHashCode.Include
    private String who; // assuming unique to calculate how many times this 'who' is greeted hello
}
