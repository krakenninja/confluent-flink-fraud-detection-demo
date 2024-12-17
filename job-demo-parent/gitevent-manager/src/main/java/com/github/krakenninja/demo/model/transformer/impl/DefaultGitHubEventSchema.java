package com.github.krakenninja.demo.model.transformer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.krakenninja.demo.constants.AppConstants;
import com.github.krakenninja.demo.exceptions.InternalException;
import com.github.krakenninja.demo.exceptions.UnprocessableEntityException;
import com.github.krakenninja.demo.model.transformer.GitHubEventSchema;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.TeeInputStream;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * GitHub event schema transformer 
 * <p>
 * To basically validate against the JSON input VS the defined schema 
 * {@link com.github.krakenninja.demo.model.transformer.impl.DefaultGitHubEventSchema#SCHEMA_RESOURCE_LOCATION_JSON}
 * </p>
 * @author Christopher CKW
 * @since 1.0.0
 * @see <a href="https://github.com/networknt/json-schema-validator">networknt/json-schema-validator</a>
 * @see <a href="https://docs.github.com/en/enterprise-cloud@latest/admin/monitoring-activity-in-your-enterprise/reviewing-audit-logs-for-your-enterprise/audit-log-events-for-your-enterprise">Audit log events for your enterprise</a>
 * @see <a href="https://docs.github.com/en/enterprise-cloud@latest/authentication/keeping-your-account-and-data-secure/security-log-events">Security log events</a>
 */
@Profile(
    AppConstants.APP_PROFILE_GITHUB_EVENT_SCHEMA_DEFAULT // activate only-if-with this profile name
)
@Component
@Slf4j
@Accessors(
    chain = true
)
@Getter
@Setter
public class DefaultGitHubEventSchema
       implements GitHubEventSchema
{
    /**
     * GitHub schema resource root path
     * <p>
     * See {@code src/resources/schema} folder
     * </p>
     * @since 1.0.0
     */
    protected static final String SCHEMA_RESOURCE_ROOT = "schema";
    
    /**
     * GitHub schema resource web root path
     * @since 1.0.0
     */
    protected static final String SCHEMA_RESOURCE_WEB_ROOT = "https://credio.xyz";
    
    /**
     * GitHub event schema resource root JSON schema
     * @since 1.0.0
     */
    protected static final String SCHEMA_RESOURCE_ROOT_JSON = String.format(
        "%s/git", 
        SCHEMA_RESOURCE_ROOT
    );
    
    /**
     * GitHub event schema resource location JSON schema
     * @since 1.0.0
     */
    protected static final String SCHEMA_RESOURCE_LOCATION_JSON = String.format(
        "%s/%s/gitevent.schema.json", 
        SCHEMA_RESOURCE_WEB_ROOT,
        SCHEMA_RESOURCE_ROOT_JSON
    );
    
    /**
     * Use Jackson object mapper for JSON processing
     * @since 1.0.0
     */
    @Nonnull
    private ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Schema source URI
     * <p>
     * Defaults to {@code https://credio.xyz/schema/}
     * </p>
     * @since 1.0.0
     */
    @Nullable
    private URI schemaSource;
    
    /**
     * Schema mapped classpath
     * <p>
     * Defaults to {@code classpath:<SCHEMA_RESOURCE>}
     * </p>
     * @since 1.0.0
     */
    @Nullable
    private URI schemaMappedClasspath;
    
    /**
     * Schema location
     * <p>
     * Defaults to {@link DefaultGitHubEventSchema#SCHEMA_RESOURCE_LOCATION_JSON}
     * </p>
     * @since 1.0.0
     */
    @Nonnull
    private SchemaLocation schemaLocation = SchemaLocation.of(
        SCHEMA_RESOURCE_LOCATION_JSON
    );
    
    /**
     * Transform the JSON input stream to {@link T} typed
     * <p>
     * The following will invoke {@link #validate(java.io.InputStream)} before 
     * transforming
     * </p>
     * @param <T>                               Concrete subclass type to 
     *                                          transform to
     * @param jsonStream                        JSON input stream to transform. 
     *                                          Must not be {@code null}
     * @param toType                            Transform the {@code jsonStream} 
     *                                          to typed. Must not be {@code null}
     * @return                                  {@link T} object ; may be 
     *                                          {@code null} depending on 
     *                                          implementation
     * @throws UnprocessableEntityException     If unable to process the 
     *                                          {@code jsonStream}
     * @throws InternalException                If an unknown error occurs
     * @since 1.0.0
     * @see #validate(java.io.InputStream) 
     */
    @Override
    public <T extends Object> T transform(@Nonnull
                                          final InputStream jsonStream,
                                          @Nonnull
                                          final TypeReference<T> toType)
    {
        try(final ByteArrayOutputStream copyJsonStream = new ByteArrayOutputStream())
        {
            try(final TeeInputStream teeInputStream = new TeeInputStream(
                jsonStream, 
                copyJsonStream
            ))
            {
                return Optional.of(
                    validate(
                        teeInputStream
                    )
                ).filter(
                    isValidateSuccess -> isValidateSuccess
                ).map(
                    isValidateSuccess -> {
                        try(final InputStream jsonRestream = new ByteArrayInputStream(
                            copyJsonStream.toByteArray()
                        ))
                        {
                            return getMapper().convertValue(
                                processNode(
                                    getMapper().readTree(
                                        jsonRestream
                                    )
                                ),
                                toType
                            );
                        }
                        catch(Exception e)
                        {
                            throw new UnprocessableEntityException(
                                    String.format(
                                    "Transform JSON stream `%s` type to object `%s` type ENCOUNTERED READ VALUE FAILURE ; %s",
                                    jsonStream.getClass().getName(),
                                    toType.getType().getTypeName(),
                                    e.getMessage()
                                ),
                                e
                            );
                        }
                    }
                ).orElseThrow(
                    () -> new UnprocessableEntityException(
                        String.format(
                            "Transform JSON stream `%s` type to object `%s` type ENCOUNTERED VALIDATION FAILURE ; check logs above for details",
                            jsonStream.getClass().getName(),
                            toType.getType().getTypeName()
                        )
                    )
                );
            }
        }
        catch(InternalException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new UnprocessableEntityException(
                    String.format(
                    "Transform JSON stream `%s` type to object `%s` type ENCOUNTERED UNEXPECTED FAILURE ; %s",
                    jsonStream.getClass().getName(),
                    toType.getType().getTypeName(),
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Transform the JSON string to {@link T} typed
     * <p>
     * The following will invoke {@link #validate(java.lang.String)} before 
     * transforming
     * </p>
     * @param <T>                               Concrete subclass type to 
     *                                          transform to
     * @param json                              JSON string to validate. Must not 
     *                                          be {@code null} or blank/empty
     * @param toType                            Transform the {@code jsonStream} 
     *                                          to typed. Must not be {@code null}
     * @return                                  {@link T} object ; may be 
     *                                          {@code null} depending on 
     *                                          implementation
     * @since 1.0.0
     * @see #validate(java.lang.String) 
     */
    @Override
    public <T extends Object> T transform(@Nonnull
                                          final String json,
                                          @Nonnull
                                          final TypeReference<T> toType)
    {
        return Optional.of(
            validate(
                json
            )
        ).filter(
            isValidateSuccess -> isValidateSuccess
        ).map(
            isValidateSuccess -> {
                try
                {
                    return getMapper().convertValue(
                        processNode(
                            getMapper().readTree(
                                json
                            )
                        ),
                        toType
                    );
                }
                catch(Exception e)
                {
                    throw new UnprocessableEntityException(
                            String.format(
                            "Transform JSON data to object `%s` type ENCOUNTERED READ VALUE FAILURE ; %s --- JSON data%n\t%s",
                            toType.getType().getTypeName(),
                            e.getMessage(),
                            json
                        ),
                        e
                    );
                }
            }
        ).orElseThrow(
            () -> new UnprocessableEntityException(
                String.format(
                    "Transform JSON data to object `%s` type ENCOUNTERED VALIDATION FAILURE ; check logs above for details --- JSON data%n\t%s",
                    toType.getType().getTypeName(),
                    json
                )
            )
        );
    }
    
    /**
     * Validate the JSON input stream
     * <p>
     * Check logs for validation error details (if result is {@code false})
     * </p>
     * @param jsonStream                        JSON input stream to validate. 
     *                                          Must not be {@code null}
     * @return                                  {@code true} if no validation 
     *                                          errors, otherwise {@code false}
     * @throws InternalException                If unable to process validation
     * @since 1.0.0
     */
    @Nonnull
    @Override
    public boolean validate(@Nonnull
                            final InputStream jsonStream)
    {
        try
        {
            return processValidationResults(
                validate(
                    getMapper().readTree(
                        jsonStream
                    )
                )
            );
        }
        catch(Exception e)
        {
            throw new InternalException(
                String.format(
                    "Unable to validate JSON stream ENCOUNTERED FAILURE ; %s",
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Validate the JSON string
     * <p>
     * Check logs for validation error details (if result is {@code false})
     * </p>
     * @param json                              JSON string to validate. Must not 
     *                                          be {@code null} or blank/empty
     * @return                                  {@code true} if no validation 
     *                                          errors, otherwise {@code false}
     * @throws InternalException                If unable to process validation
     * @since 1.0.0
     */
    @Nonnull
    @Override
    public boolean validate(@Nonnull
                            final String json)
    {
        try
        {
            return processValidationResults(
                validate(
                    getMapper().readTree(
                        json
                    )
                )
            );
        }
        catch(Exception e)
        {
            throw new InternalException(
                String.format(
                    "Unable to validate JSON string ENCOUNTERED FAILURE ; %s",
                    e.getMessage()
                ),
                e
            );
        }
    }
    
    /**
     * Process validate error results
     * @param validateErrResults                Validation error message 
     *                                          result(s). Must not be 
     *                                          {@code null}
     * @return                                  {@code true} if has no errors 
     *                                          (means validation passed/success), 
     *                                          {@code false} otherwise
     * @since 1.0.0
     */
    protected boolean processValidationResults(@Nonnull
                                               final Set<ValidationMessage> validateErrResults)
    {
        return Optional.of(
            validateErrResults
        ).filter(
            Predicate.not(
                Set::isEmpty
            )
        ).map(
            validateErrResultsToProcess -> {
                log.error(
                    "Validate JSON using '{}' schema ENCOUNTERED VALIDATION ERROR(S) ; validation error(s) \n\t{}",
                    getSchemaLocation().toString(),
                    validateErrResultsToProcess.stream().map(
                        ValidationMessage::toString
                    ).collect(
                        Collectors.joining(
                            "\n\t"
                        )
                    )
                );
                return false;
            }
        ).orElse(
            true
        );
    }
    
    /**
     * Validate the given {@code json}, starting at the root of the data path.
     * @param jsonNode                          JSON node to validate. Must not 
     *                                          be {@code null}
     * @return                                  Empty {@link Set} if no errors, 
     *                                          otherwise a set of 
     *                                          {@link ValidationMessage} describing 
     *                                          validation errors in details
     * @throws InternalException                If unable to process validation
     * @since 1.0.0
     */
    @Nonnull
    protected Set<ValidationMessage> validate(@Nonnull
                                              final JsonNode jsonNode)
    {
        // This creates a schema factory that will use Draft 2020-12 as the 
        // default if $schema is not specified in the schema data. If 
        // $schema is specified in the schema data then that schema dialect 
        // will be used . instead and this version is ignored
        final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(
                SpecVersion.VersionFlag.V202012, 
            // This creates a mapping from $id which starts with 
            // https://www.example.org/ to the retrieval URI classpath:schema/
            builder -> builder.schemaMappers(
                schemaMappers -> schemaMappers.mapPrefix(
                    Optional.ofNullable(
                        getSchemaSource()
                    ).map(
                        schemaSourceToProcess -> schemaSourceToProcess.toString()
                    ).orElseGet(
                        () -> {
                            try
                            {
                                return new URI(
                                    String.format(
                                        "%s/%s/",
                                        SCHEMA_RESOURCE_WEB_ROOT,
                                        SCHEMA_RESOURCE_ROOT
                                    )
                                ).toString();
                            }
                            catch(Exception e)
                            {
                                throw new InternalException(
                                    String.format(
                                        "Unable to create default schema source ; %s",
                                        e.getMessage()
                                    ),
                                    e
                                );
                            }
                        }
                    ), 
                    Optional.ofNullable(
                        getSchemaMappedClasspath()
                    ).map(
                        schemaMappedClasspathToProcess -> schemaMappedClasspathToProcess.toString()
                    ).orElseGet(
                        () -> {
                            try
                            {
                                return new URI(
                                    String.format(
                                        "classpath:%s/",
                                        SCHEMA_RESOURCE_ROOT
                                    )
                                ).toString();
                            }
                            catch(Exception e)
                            {
                                throw new InternalException(
                                    String.format(
                                        "Unable to create default schema mapped classpath ; %s",
                                        e.getMessage()
                                    ),
                                    e
                                );
                            }
                        }
                    )
                )
            )
        );

        final SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        // by default JSON Path is used for reporting the instance location and evaluation path
        config.setPathType(
            PathType.JSON_POINTER
        );
        // By default the JDK regular expression implementation which is not 
        // ECMA 262 compliant is used. Note that setting this to true 
        // requires including the optional joni dependency
        //config.setEcma262Validator(true);

        // Due to the mapping the schema will be retrieved from the classpath 
        // at classpath:schema/example-main.json. If the schema data does not 
        // specify an $id the absolute IRI of the schema location will be 
        // used as the $id.
        final JsonSchema schema = jsonSchemaFactory.getSchema(
            getSchemaLocation(), 
            config
        );
        
        // now validate it
        final Set<com.networknt.schema.ValidationMessage> assertions = schema.validate(
            processNode(
                jsonNode
            ), 
            executionContext -> {
                // By default since Draft 2019-09 the format keyword only 
                // generates annotations and not assertions
                executionContext.getExecutionConfig().setFormatAssertionsEnabled(
                    true
                );
            }
        );
        Optional.of(
            assertions
        ).filter(
            assertionsToProcess -> !assertionsToProcess.isEmpty()
        ).ifPresentOrElse(
            assertionsToProcess -> {
                final String errMsg = String.format(
                    "JSON validation against schema CONTAINS VALIDATION ERROR(S) ; \n\t%s",
                    assertionsToProcess.stream().map(
                        assertionToProcess -> assertionToProcess.toString()
                    ).collect(
                        Collectors.joining(
                            "\n\t"
                        )
                    )
                );
                log.error(
                    errMsg
                );
            },
            () -> log.info(
                "JSON validation against schema IS VALID"
            )
        );
        
        return assertions;
    }
    
    /**
     * Choose which JSON node to process for validation
     * <p>
     * Got GitHub audit event logs integrated with Azure that is coming from 
     * Confluent Cloud, we need to extract the first item (index 0) then obtain 
     * the JSON object key {@code value} element (that is the GitHub audit event 
     * logs as per-documented in <a href="https://docs.github.com/en/enterprise-cloud@latest/admin/monitoring-activity-in-your-enterprise/reviewing-audit-logs-for-your-enterprise/audit-log-events-for-your-enterprise">Audit log events for your enterprise</a> 
     * and/or <a href="https://docs.github.com/en/enterprise-cloud@latest/authentication/keeping-your-account-and-data-secure/security-log-events">Security log events</a>)
     * </p>
     * @param jsonNode                          JSON node to extract the GitHub 
     *                                          audit event details (that is 
     *                                          usually at index {@code 0})
     * @return                                  {@link JsonNode} of the GitHub 
     *                                          audit event details, never 
     *                                          {@code null}
     * @throws UnprocessableEntityException     If unable to extract the GitHub 
     *                                          audit event details from 
     *                                          {@code jsonNode}
     * @since 1.0.0
     */
    @Nonnull
    protected JsonNode processNode(@Nonnull
                                   final JsonNode jsonNode)
    {
        final String githubAuditEventNodeName = "value";
        return Optional.of(
            jsonNode
        ).filter(
            JsonNode::isArray
        ).map(
            jsonNodeToProcess -> Optional.of(
                jsonNodeToProcess.has(
                    0
                )
            ).filter(
                jsonNodeHasIdx0ToProcess -> jsonNodeHasIdx0ToProcess==true
            ).map(
                jsonNodeHasIdx0ToProcess -> Optional.ofNullable(
                    jsonNode.get(
                        0
                    )
                ).filter(
                    // making sure it is a node (object)
                    JsonNode::isObject
                ).map(
                    jsonNodeAtIdx0ToProcess -> Optional.of(
                        jsonNodeAtIdx0ToProcess.hasNonNull(
                            githubAuditEventNodeName
                        )
                    ).filter(
                        jsonNodeAtIdx0HasNodeNamedToProcess -> jsonNodeAtIdx0HasNodeNamedToProcess
                    ).map(
                        jsonNodeAtIdx0HasNodeNamedToProcess -> Optional.of(
                            jsonNodeAtIdx0ToProcess.get(
                                githubAuditEventNodeName
                            )
                        ).filter(
                            JsonNode::isObject
                        ).orElseThrow(
                            () -> new UnprocessableEntityException(
                                String.format(
                                    "JSON node array ELEMENT AT INDEX 0 NODE/ELEMENT NAMED '%s' IS NOT AN OBJECT NODE ; expecting JSON node array type at element index 0 having node/element named as object node for JSON string%n\t%s",
                                    githubAuditEventNodeName,
                                    jsonNodeToProcess.toString()
                                )
                            )
                        )
                    ).orElseThrow(
                        () -> new UnprocessableEntityException(
                            String.format(
                                "JSON node array ELEMENT AT INDEX 0 IS MISSING NODE/ELEMENT NAMED '%s' ; expecting JSON node array type at element index 0 having node/element named for JSON string%n\t%s",
                                githubAuditEventNodeName,
                                jsonNodeToProcess.toString()
                            )
                        )
                    )
                ).orElseThrow(
                    () -> new UnprocessableEntityException(
                        String.format(
                            "JSON node array ELEMENT AT INDEX 0 IS NOT AN OBJECT NODE ; expecting JSON node array type at element index 0 is object node for JSON string%n\t%s",
                            jsonNodeToProcess.toString()
                        )
                    )
                )
            ).orElseThrow(
                () -> new UnprocessableEntityException(
                    String.format(
                        "JSON node array ELEMENT AT INDEX 0 IS MISSING ; expecting JSON node array type with element index 0 to obtain GitHub Audit Event object for JSON string%n\t%s",
                        jsonNodeToProcess.toString()
                    )
                )
            )
        ).orElseThrow(
            () -> new UnprocessableEntityException(
                String.format(
                    "JSON node IS NOT AN ARRAY ; expecting JSON node array type for JSON string%n\t%s",
                    jsonNode.toString()
                )
            )
        );
    }
}
