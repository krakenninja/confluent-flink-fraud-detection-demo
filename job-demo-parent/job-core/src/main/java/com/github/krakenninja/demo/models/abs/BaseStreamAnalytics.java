package com.github.krakenninja.demo.models.abs;

/**
 * Simple abstract {@link com.github.krakenninja.demo.models.BaseStreamAnalytics} 
 * implementation that defaults to have a {@link #toString()} that produces 
 * output as JSON formatted 
 * @param <T>                                   Subclass concrete implementation 
 *                                              type of {@link com.github.krakenninja.demo.models.abs.BaseStreamAnalytics}
 * @since 1.0.0                           
 * @author Christopher CKW
 */
public abstract class BaseStreamAnalytics<T extends BaseStreamAnalytics<T>>
       extends com.github.krakenninja.demo.models.abs.Base<T>
       implements com.github.krakenninja.demo.models.BaseStreamAnalytics<T>
{
}
