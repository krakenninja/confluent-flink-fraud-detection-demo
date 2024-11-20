package com.github.krakenninja.demo.models;

/**
 * Base stream record model
 * <p>
 * This would be our base demo object input request represented as "stream 
 * record" typed (basically a record/entity to be processed for job analytics)
 * </p>
 * @param <T>                                   Subclass concrete implementation 
 *                                              type of {@link com.github.krakenninja.demo.models.BaseStreamRecord}
 * @since 1.0.0
 * @author Christopher CKW
 */
public interface BaseStreamRecord<T extends BaseStreamRecord<T>>
       extends Base<T>
{
}
