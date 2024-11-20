package com.github.krakenninja.demo.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Unprocessable entity exception
 * @since 1.0.0
 * @author ChristopherCKW
 */
@Accessors(
    fluent=false,
    chain=true
)
@Getter
@Setter
@StandardException
@ResponseStatus(
    value = HttpStatus.UNPROCESSABLE_ENTITY
)
public class UnprocessableEntityException
       extends InternalException
{
}