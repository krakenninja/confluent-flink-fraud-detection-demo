package com.github.krakenninja.demo.exceptions;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Internal exception
 * @since 1.0.0
 * @author Christopher CKW
 */
@Accessors(
    fluent=false,
    chain=true
)
@Getter
@Setter
@StandardException
@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR
)
public class InternalException
       extends RuntimeException
{
    /**
     * Code
     * @since 1.0.0
     */
    @Nullable
    private Long code;
}

