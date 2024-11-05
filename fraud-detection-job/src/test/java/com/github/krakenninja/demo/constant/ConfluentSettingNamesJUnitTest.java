package com.github.krakenninja.demo.constant;

import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit test for {@link com.github.krakenninja.demo.constant.ConfluentSettingNames}
 * @author Christopher CKW
 */
@Slf4j
public class ConfluentSettingNamesJUnitTest
{
    @BeforeAll
    public static void setUpClass() {}
    
    @AfterAll
    public static void tearDownClass() {}
    
    @BeforeEach
    public void setUp() {}
    
    @AfterEach
    public void tearDown() {}

    @Test
    public void getSettings_Expect_OK()
    {
        try (MockedStatic<ConfluentSettingNames> confluentSettingNamesMockedStatic = Mockito.mockStatic(
            ConfluentSettingNames.class
        ))
        {
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_CLOUD_PROVIDER
                )
            ).thenReturn(
                Optional.of(
                    "aws"
                )
            );
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_CLOUD_REGION
                )
            ).thenReturn(
                Optional.of(
                    "us-east-1"
                )
            );
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_FLINK_API_KEY
                )
            ).thenReturn(
                Optional.of(
                    "Flink-Api-Key"
                )
            );
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_FLINK_API_SECRET
                )
            ).thenReturn(
                Optional.of(
                    "Flink-Api-Secret"
                )
            );
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_ORG_ID
                )
            ).thenReturn(
                Optional.of(
                    "Org-ID"
                )
            );
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_ENV_ID
                )
            ).thenReturn(
                Optional.of(
                    "Env-ID"
                )
            );
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getEnv(
                    ConfluentSettingNames.SYSENV_COMPUTE_POOL_ID
                )
            ).thenReturn(
                Optional.of(
                    "Compute-Pool-ID"
                )
            );
            confluentSettingNamesMockedStatic.when(
                ConfluentSettingNames::values
            ).thenCallRealMethod();
            confluentSettingNamesMockedStatic.when(
                ConfluentSettingNames::getSettings
            ).thenCallRealMethod();
            confluentSettingNamesMockedStatic.when(
                () -> ConfluentSettingNames.getSetting(
                    any(
                        ConfluentSettingNames.class
                    )
                )
            ).thenCallRealMethod();
            
            final String[] argSettings = ConfluentSettingNames.getSettings();
            assertNotNull(
                argSettings
            );
            assertTrue(
                argSettings.length>0
            );
            log.info(
                "Argument Settings --- \n\t{}",
                Arrays.toString(
                    argSettings
                )
            );
        }
    }
}
