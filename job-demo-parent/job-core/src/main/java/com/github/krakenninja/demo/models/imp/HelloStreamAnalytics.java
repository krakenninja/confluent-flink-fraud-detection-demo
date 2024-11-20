package com.github.krakenninja.demo.models.imp;

import com.github.krakenninja.demo.models.abs.BaseStreamRecord;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A simple hello stream analytics to analyze how many times the "who" had been 
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
public class HelloStreamAnalytics
       extends BaseStreamRecord<HelloStreamAnalytics>
{
    @EqualsAndHashCode.Include
    private String who; // assuming unique to calculate how many times this 'who' is greeted hello
    
    private long totalCount; // total accumulative hits count for this "who"

    private long timestamp; // last timestamp
}
