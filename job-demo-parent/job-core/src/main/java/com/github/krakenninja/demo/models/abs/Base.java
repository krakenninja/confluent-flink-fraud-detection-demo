package com.github.krakenninja.demo.models.abs;

/**
 * Simple abstract {@link com.github.krakenninja.demo.models.Base} implementation 
 * that defaults to have a {@link #toString()} that produces output as JSON 
 * formatted 
 * @param <T>                                   Subclass concrete implementation 
 *                                              type of {@link com.github.krakenninja.demo.models.abs.Base}
 * @since 1.0.0                           
 * @author Christopher CKW
 */
public abstract class Base<T extends Base<T>>
       implements com.github.krakenninja.demo.models.Base<T>
{
    /**
     * Default implementation prints a string output view of this object type 
     * and its JSON formatted view
     * @return 
     */
    @Override
    public String toString()
    {
        return String.format(
            "%nType : `%s`%nJSON View : %n\t%s%n%n",
            getClass().getName(),
            toJson()
        );
    }
}
