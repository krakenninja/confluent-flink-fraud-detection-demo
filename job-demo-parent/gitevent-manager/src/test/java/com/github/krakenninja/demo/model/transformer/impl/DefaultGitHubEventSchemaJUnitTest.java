package com.github.krakenninja.demo.model.transformer.impl;

import com.github.krakenninja.demo.exceptions.InternalException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit Test for {@link com.github.krakenninja.demo.models.validator.GitHubEventSchemaHelper}
 * <p>
 * Then you can run this integration test using command below : 
 * <pre>
 *   cd job-demo-parent
 *   mvn clean install
 *   mvn test -f gitevent-manager/pom.xml -Dtest=com.github.krakenninja.demo.models.validator.GitHubEventSchemaValidatorJUnitTest
 * </pre>
 * </p>
 * @since 1.0.0
 * @author Christopher CKW
 */
@Slf4j
public class DefaultGitHubEventSchemaJUnitTest
{
    private DefaultGitHubEventSchema gitHubEventSchemaValidator;
    
    private final List<String> gitHubEventJsonSuccessResources = new ArrayList<>();
    
    private final List<String> gitHubEventJsonFailureResources = new ArrayList<>();
    
    @BeforeAll
    public static void setUpClass() {}
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp()
    {
        gitHubEventSchemaValidator = new DefaultGitHubEventSchema();
        
        int gitHubEventJsonSuccessNumResource = 1;
        while(true)
        {
            final String gitHubEventJsonSuccessResource = String.format(
                "%s/sample/%s", 
                DefaultGitHubEventSchema.SCHEMA_RESOURCE_ROOT_JSON,
                String.format(
                    "git-event-success-%s.json",
                    String.format(
                        "%02d", 
                        gitHubEventJsonSuccessNumResource
                    )
                )
            );
            
            if(Objects.nonNull(
                Thread.currentThread().getContextClassLoader().getResource(
                    gitHubEventJsonSuccessResource
                )
            ))
            {
                gitHubEventJsonSuccessResources.add(
                    gitHubEventJsonSuccessResource
                );
                
                gitHubEventJsonSuccessNumResource++;
                
                continue;
            }
            
            break;
        }
        
        int gitHubEventJsonFailureNumResource = 1;
        while(true)
        {
            final String gitHubEventJsonFailureResource = String.format(
                "%s/sample/%s", 
                DefaultGitHubEventSchema.SCHEMA_RESOURCE_ROOT_JSON,
                String.format(
                    "git-event-failure-%s.json",
                    String.format(
                        "%02d", 
                        gitHubEventJsonFailureNumResource
                    )
                )
            );
            
            if(Objects.nonNull(
                Thread.currentThread().getContextClassLoader().getResource(
                    gitHubEventJsonFailureResource
                )
            ))
            {
                gitHubEventJsonFailureResources.add(
                    gitHubEventJsonFailureResource
                );
                
                gitHubEventJsonFailureNumResource++;
                
                continue;
            }
            
            break;
        }
    }
    
    @AfterEach
    public void tearDown() {}

    @Test
    void validate_UsingStream_ExpectOK()
    {
        final InputStream[] gitHubEventJsonStreams = getGitHubEventJsonSuccessStreams();
        
        IntStream.range(
            0, 
            gitHubEventJsonStreams.length
        ).forEach(
            indexToProcess -> {
                try
                {
                    final boolean validateResult = gitHubEventSchemaValidator.validate(
                        gitHubEventJsonStreams[indexToProcess]
                    );

                    assertTrue(
                        validateResult
                    );
                }
                finally
                {
                    try
                    {
                        gitHubEventJsonStreams[indexToProcess].close();
                    }
                    catch(Exception e)
                    {
                        log.warn(
                            "Close resource stream at index '{}' ENCOUNTERED FAILURE ; {}",
                            indexToProcess,
                            e.getMessage(),
                            e
                        );
                    }
                }
            }
        );
    }

    @Test
    void validate_UsingString_ExpectOK()
    {
        final String[] gitHubEventJsons = getGitHubEventSuccessJsons();
        
        IntStream.range(
            0, 
            gitHubEventJsons.length
        ).forEach(
            indexToProcess -> {
                final boolean validateResult = gitHubEventSchemaValidator.validate(
                    gitHubEventJsons[indexToProcess]
                );
                
                assertTrue(
                    validateResult
                );
            }
        );
    }
    
    @Test
    void validate_UsingStream_ExpectFail()
    {
        final InputStream[] gitHubEventJsonStreams = getGitHubEventJsonFailureStreams();
        
        IntStream.range(
            0, 
            gitHubEventJsonStreams.length
        ).forEach(
            indexToProcess -> {
                try
                {
                    final boolean validateResult = gitHubEventSchemaValidator.validate(
                        gitHubEventJsonStreams[indexToProcess]
                    );

                    assertFalse(
                        validateResult
                    );
                }
                finally
                {
                    try
                    {
                        gitHubEventJsonStreams[indexToProcess].close();
                    }
                    catch(Exception e)
                    {
                        log.warn(
                            "Close resource stream at index '{}' ENCOUNTERED FAILURE ; {}",
                            indexToProcess,
                            e.getMessage(),
                            e
                        );
                    }
                }
            }
        );
    }

    @Test
    void validate_UsingString_ExpectFail()
    {
        final String[] gitHubEventJsons = getGitHubEventFailureJsons();
        
        IntStream.range(
            0, 
            gitHubEventJsons.length
        ).forEach(
            indexToProcess -> {
                final boolean validateResult = gitHubEventSchemaValidator.validate(
                    gitHubEventJsons[indexToProcess]
                );
                
                assertFalse(
                    validateResult
                );
            }
        );
    }
    
    protected String[] getGitHubEventSuccessJsons()
    {
        final AtomicReference<InputStream[]> gitHubEventJsonStreams = new AtomicReference<>();
        
        final AtomicReference<String[]> gitHubEventJsons = new AtomicReference<>();
        try
        {
            gitHubEventJsonStreams.set(
                getGitHubEventJsonSuccessStreams()
            );
            
            gitHubEventJsons.set(
                new String[gitHubEventJsonStreams.get().length]
            );
            
            IntStream.range(
                0, 
                gitHubEventJsons.get().length
            ).forEach(
                indexToProcess -> {
                    try(BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            gitHubEventJsonStreams.get()[indexToProcess]
                        )
                    ))
                    {
                        gitHubEventJsons.get()[indexToProcess] = reader.lines().collect(
                            Collectors.joining(
                                System.lineSeparator()
                            )
                        );
                    }
                    catch(Exception e)
                    {
                        log.error(
                            "Stream to JSON string at index '{}' ENCOUNTERED FAILURE ; {}",
                            indexToProcess,
                            e.getMessage(),
                            e
                        );
                    }
                }
            );
        }
        finally
        {
            Optional.ofNullable(
                gitHubEventJsonStreams
            ).ifPresent(
                gitHubEventJsonStreamsToProcess -> Arrays.stream(
                    gitHubEventJsonStreamsToProcess.get()
                ).forEach(
                    gitHubEventJsonStreamToProcess -> {
                        try
                        {
                            gitHubEventJsonStreamToProcess.close();
                        }
                        catch(Exception e)
                        {
                            log.warn(
                                "Close resource stream ENCOUNTERED FAILURE ; {}",
                                e.getMessage(),
                                e
                            );
                        }
                    }
                )
            );
        }
        return gitHubEventJsons.get();
    }
            
    protected InputStream[] getGitHubEventJsonSuccessStreams()
    {
        final InputStream[] gitHubEventJsonStreams = new InputStream[gitHubEventJsonSuccessResources.size()];
        
        IntStream.range(
            0, 
            gitHubEventJsonStreams.length
        ).forEach(
            indexToProcess -> {
                final String gitHubEventJsonResource = gitHubEventJsonSuccessResources.get(
                    indexToProcess
                );
                
                gitHubEventJsonStreams[indexToProcess] = Optional.ofNullable(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        gitHubEventJsonResource
                    )
                ).orElseThrow(
                    () -> new InternalException(
                        String.format(
                            "Get resource as stream at path '%s' RESOURCE NOT FOUND",
                            gitHubEventJsonResource
                        )
                    )
                );
            }
        ); 
        
        return gitHubEventJsonStreams;
    }
    
    protected String[] getGitHubEventFailureJsons()
    {
        final AtomicReference<InputStream[]> gitHubEventJsonStreams = new AtomicReference<>();
        
        final AtomicReference<String[]> gitHubEventJsons = new AtomicReference<>();
        try
        {
            gitHubEventJsonStreams.set(
                getGitHubEventJsonFailureStreams()
            );
            
            gitHubEventJsons.set(
                new String[gitHubEventJsonStreams.get().length]
            );
            
            IntStream.range(
                0, 
                gitHubEventJsons.get().length
            ).forEach(
                indexToProcess -> {
                    try(BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                            gitHubEventJsonStreams.get()[indexToProcess]
                        )
                    ))
                    {
                        gitHubEventJsons.get()[indexToProcess] = reader.lines().collect(
                            Collectors.joining(
                                System.lineSeparator()
                            )
                        );
                    }
                    catch(Exception e)
                    {
                        log.error(
                            "Stream to JSON string at index '{}' ENCOUNTERED FAILURE ; {}",
                            indexToProcess,
                            e.getMessage(),
                            e
                        );
                    }
                }
            );
        }
        finally
        {
            Optional.ofNullable(
                gitHubEventJsonStreams
            ).ifPresent(
                gitHubEventJsonStreamsToProcess -> Arrays.stream(
                    gitHubEventJsonStreamsToProcess.get()
                ).forEach(
                    gitHubEventJsonStreamToProcess -> {
                        try
                        {
                            gitHubEventJsonStreamToProcess.close();
                        }
                        catch(Exception e)
                        {
                            log.warn(
                                "Close resource stream ENCOUNTERED FAILURE ; {}",
                                e.getMessage(),
                                e
                            );
                        }
                    }
                )
            );
        }
        return gitHubEventJsons.get();
    }
    
    protected InputStream[] getGitHubEventJsonFailureStreams()
    {
        final InputStream[] gitHubEventJsonStreams = new InputStream[gitHubEventJsonFailureResources.size()];
        
        IntStream.range(
            0, 
            gitHubEventJsonStreams.length
        ).forEach(
            indexToProcess -> {
                final String gitHubEventJsonResource = gitHubEventJsonFailureResources.get(
                    indexToProcess
                );
                
                gitHubEventJsonStreams[indexToProcess] = Optional.ofNullable(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        gitHubEventJsonResource
                    )
                ).orElseThrow(
                    () -> new InternalException(
                        String.format(
                            "Get resource as stream at path '%s' RESOURCE NOT FOUND",
                            gitHubEventJsonResource
                        )
                    )
                );
            }
        ); 
        
        return gitHubEventJsonStreams;
    }
}
