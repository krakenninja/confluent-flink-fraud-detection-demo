package com.github.krakenninja.demo.models;

/**
 * Base stream analytics model
 * <p>
 * This would be our base demo object output response represented as "stream 
 * analytics" typed (basically a processed job output response)
 * </p>
 * @param <T>                                   Subclass concrete implementation 
 *                                              type of {@link com.github.krakenninja.demo.models.BaseStreamAnalytics}
 * @since 1.0.0
 * @author Christopher CKW
 */
public interface BaseStreamAnalytics<T extends BaseStreamAnalytics<T>>
       extends Base<T>
{
}
