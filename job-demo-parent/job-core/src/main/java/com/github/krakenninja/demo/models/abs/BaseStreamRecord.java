package com.github.krakenninja.demo.models.abs;

/**
 * Simple abstract {@link com.github.krakenninja.demo.models.BaseStreamRecord} 
 * implementation that defaults to have a {@link #toString()} that produces 
 * output as JSON formatted 
 * @param <T>                                   Subclass concrete implementation 
 *                                              type of {@link com.github.krakenninja.demo.models.abs.BaseStreamRecord}
 * @since 1.0.0                           
 * @author Christopher CKW
 */
public abstract class BaseStreamRecord<T extends BaseStreamRecord<T>>
       extends com.github.krakenninja.demo.models.abs.Base<T>
       implements com.github.krakenninja.demo.models.BaseStreamRecord<T>
{
}
