package com.github.krakenninja.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.fasterxml.jackson.annotation.JsonInclude.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.krakenninja.demo.exceptions.UnprocessableEntityException;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base that provides basic/default object processing features 
 * @param <T>                                   Subclass concrete implementation 
 *                                              type of {@link com.github.krakenninja.demo.models.Base}
 * @since 1.0.0
 * @author Christopher CKW
 */
public interface Base<T extends Base<T>>
       extends Serializable
{
    /**
     * Obtain the {@link com.fasterxml.jackson.databind.ObjectMapper} to perform 
     * object serialization/de-serialization
     * <p>
     * Defaults to return {@link com.fasterxml.jackson.databind.json.JsonMapper} 
     * instance with following setup/configured : 
     * <ul>
     *   <li>{@link com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} to {@code false}</li>
     *   <li>{@link com.fasterxml.jackson.databind.json.JsonMapper#setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include)} to {@link com.fasterxml.jackson.annotation.JsonInclude.Include#NON_NULL}</li>
     * </ul>
     * </p>
     * @return                                  {@link com.fasterxml.jackson.databind.ObjectMapper}, 
     *                                          never {@code null}
     * @since 1.0.0
     */
    @JsonIgnore
    @Nonnull
    default ObjectMapper getObjectMapper()
    {
        return new JsonMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, 
            false
        ).setSerializationInclusion(
            Include.NON_NULL
        );
    }
    
    /**
     * Write/serialize the object to {@link byte[]}
     * <p>
     * Default implementation uses {@link com.fasterxml.jackson.databind.ObjectMapper#writeValueAsBytes(java.lang.Object)}
     * </p>
     * @return                                  {@link byte[]}, never {@code null} 
     *                                          or empty
     * @throws UnprocessableEntityException     If write/serialization failure
     * @since 1.0.0
     */
    @JsonIgnore
    @Nonnull
    @NotEmpty
    default byte[] toBytes()
    {
        try
        {
            return getObjectMapper().writeValueAsBytes(
                this
            );
        }
        catch(Exception e)
        {
            throw new UnprocessableEntityException(
                String.format(
                    "Write value object `%s` type as bytes using object `%s` mapper ENCOUNTERED FAILURE ; %s",
                    getClass().getName(),
                    getObjectMapper().getClass().getName(),
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Read/de-serialize the {@link byte[]} to object {@link T}
     * <p>
     * Default implementation uses {@link com.fasterxml.jackson.databind.ObjectMapper#readValue(byte[], com.fasterxml.jackson.core.type.TypeReference)}
     * </p>
     * @param bytes                             {@link byte[]} to use to convert 
     *                                          back to {@link T}
     * @return                                  {@link T} instance, never 
     *                                          {@code null}
     * @throws UnprocessableEntityException     If read/de-serialization failure
     * @since 1.0.0
     */
    @Nonnull
    default T fromBytes(@Nonnull
                        @NotEmpty
                        final byte[] bytes)
    {
        final AtomicReference<Class<T>> thisClazz = new AtomicReference<>(
            validateAcceptableTyped(
                (Class<T>)getClass()
            )
        );
        try
        {
            return getObjectMapper().readValue(
                bytes,
                new TypeReference<T>()
                {
                    @Override
                    public Type getType()
                    {
                        return thisClazz.get();
                    }
                }
            );
        }
        catch(Exception e)
        {
            throw new UnprocessableEntityException(
                String.format(
                    "Read value from bytes to object `%s` type using object `%s` mapper ENCOUNTERED FAILURE ; %s",
                    thisClazz.get().getName(),
                    getObjectMapper().getClass().getName(),
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Write/serialize the object to {@link String} (JSON formatted)
     * <p>
     * Default implementation uses {@link #toBytes()} that creates a new JSON 
     * formatted string output
     * </p>
     * @return                                  {@link String}, never {@code null} 
     *                                          or empty
     * @throws UnprocessableEntityException     If write/serialization failure
     * @since 1.0.0
     */
    @JsonIgnore
    @Nonnull
    @NotBlank
    @NotEmpty
    default String toJson()
    {
        return new String(
            toBytes(), 
            StandardCharsets.UTF_8
        );
    }
    
    /**
     * Read/de-serialize the {@link String} (JSON formatted) to object {@link T}
     * <p>
     * Default implementation uses {@link #fromBytes(byte[])}
     * </p>
     * @param json                              {@link String} (JSON formatted) 
     *                                          to use to convert back to 
     *                                          {@link T}
     * @return                                  {@link T} instance, never 
     *                                          {@code null}
     * @throws UnprocessableEntityException     If read/de-serialization failure
     * @since 1.0.0
     */
    @Nonnull
    default T fromJson(@Nonnull
                       @NotBlank
                       @NotEmpty
                       final String json)
    {
        return fromBytes(
            json.getBytes(
                StandardCharsets.UTF_8
            )
        );
    }
    
    /**
     * Write/serialize the object to {@link java.util.Map} (key-value map ; 
     * where {@code K}={@link java.lang.String} representing field-name, 
     * {@code V}={@link java.lang.Object} representing field-value)
     * <p>
     * Default implementation uses {@link com.fasterxml.jackson.databind.ObjectMapper#convertValue(java.lang.Object, com.fasterxml.jackson.core.type.TypeReference)} 
     * that converts to a new object {@link java.util.LinkedHashMap} type
     * </p>
     * @return                                  {@link java.util.Map}, never 
     *                                          {@code null}
     * @throws UnprocessableEntityException     If write/serialization failure
     * @since 1.0.0
     */
    @JsonIgnore
    @Nonnull
    default Map<String, Object> toMap()
    {
        try
        {
            return getObjectMapper().convertValue(
                this,
                new TypeReference<LinkedHashMap<String, Object>>() {}
            );
        }
        catch(Exception e)
        {
            throw new UnprocessableEntityException(
                String.format(
                    "Convert value object `%s` type as map using object `%s` mapper ENCOUNTERED FAILURE ; %s",
                    getClass().getName(),
                    getObjectMapper().getClass().getName(),
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Read/de-serialize the {@link java.util.Map} to object {@link T}
     * <p>
     * Default implementation uses {@link com.fasterxml.jackson.databind.ObjectMapper#convertValue(java.lang.Object, com.fasterxml.jackson.core.type.TypeReference)} 
     * that converts to a new object {@link T} type
     * </p>
     * @param map                               {@link java.util.Map} to use to 
     *                                          convert back to {@link T}
     * @return                                  {@link T} instance, never 
     *                                          {@code null}
     * @throws UnprocessableEntityException     If read/de-serialization failure
     * @since 1.0.0
     */
    @Nonnull
    default T fromMap(@Nonnull
                      final Map<String, Object> map)
    {
        final AtomicReference<Class<T>> thisClazz = new AtomicReference<>(
            validateAcceptableTyped(
                (Class<T>)getClass()
            )
        );
        try
        {
            return getObjectMapper().convertValue(
                map,
                new TypeReference<T>()
                {
                    @Override
                    public Type getType()
                    {
                        return thisClazz.get();
                    }
                }
            );
        }
        catch(Exception e)
        {
            throw new UnprocessableEntityException(
                String.format(
                    "Convert value from map to object `%s` type using object `%s` mapper ENCOUNTERED FAILURE ; %s",
                    thisClazz.get().getName(),
                    getObjectMapper().getClass().getName(),
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Validates if {@code theClazz} is an acceptable type to be processed
     * @param theClazz                          {@link Class} type to validate. 
     *                                          Must not be {@code null}
     * @return                                  If validated, returns back 
     *                                          {@code theClazz}
     * @throws UnprocessableEntityException     If {@code theClazz} is not 
     *                                          acceptable for further processing
     * @since 1.0.0
     */
    @Nonnull
    private Class<T> validateAcceptableTyped(@Nonnull
                                             final Class<T> theClazz)
    {
        return Optional.of(
            theClazz
        ).filter(
            theClazzToProcess -> !theClazzToProcess.isInterface()
        ).filter(
            theClazzToProcess -> !Modifier.isAbstract(
                theClazzToProcess.getModifiers()
            )
        ).orElseThrow(
            () -> new UnprocessableEntityException(
                String.format(
                    "Class `%s` type IS UNACCEPTABLE ; not a concrete implementation object `%s` type",
                    theClazz.getName(),
                    Base.class.getName()
                ),
                new InvalidClassException(
                    "Not a concrete class type"
                )
            )
        );
    }
}
