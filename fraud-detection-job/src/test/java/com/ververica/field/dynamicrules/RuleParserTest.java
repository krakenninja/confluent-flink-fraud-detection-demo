/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ververica.field.dynamicrules;

import com.ververica.field.dynamicrules.Rule.AggregatorFunctionType;
import com.ververica.field.dynamicrules.Rule.LimitOperatorType;
import com.ververica.field.dynamicrules.Rule.RuleState;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class RuleParserTest
{
    @SafeVarargs
    public static <T> List<T> lst(final T... ts)
    {
        return Arrays.asList(
            ts
        );
    }

    @Test
    void testRuleParsedPlain() 
         throws Exception
    {
        String ruleString1 = "1,(active),(taxiId&driverId),,(totalFare),(sum),(>),(5),(20)";

        RuleParser ruleParser = new RuleParser();
        Rule rule1 = ruleParser.fromString(ruleString1);

        assertEquals(
            1, 
            (int)rule1.getRuleId(),
            "ID incorrect"
        );
        assertEquals(
            RuleState.ACTIVE, 
            rule1.getRuleState(),
            "Rule state incorrect"
        );
        assertEquals(
            lst("taxiId", "driverId"), 
            rule1.getGroupingKeyNames(),
            "Key names incorrect"
        );
        assertEquals(
            lst(), 
            rule1.getUnique(),
            "Unique names incorrect"
        );
        assertEquals(
            "totalFare", 
            rule1.getAggregateFieldName(),
            "Cumulative key incorrect"
        );
        assertEquals(
            AggregatorFunctionType.SUM,
            rule1.getAggregatorFunctionType(),
            "Aggregator function incorrect"
        );
        assertEquals(
            LimitOperatorType.GREATER, 
            rule1.getLimitOperatorType(),
            "Limit operator incorrect"
        );
        assertEquals(
            BigDecimal.valueOf(5), 
            rule1.getLimit(),
            "Limit incorrect"
        );
        assertEquals(
            20, 
            (int) rule1.getWindowMinutes(),
            "Window incorrect"
        );
    }

    @Test
    public void testRuleParsedJson() 
           throws Exception
    {
        final String ruleString1 = "{\n" 
            + "  \"ruleId\": 1,\n"
            + "  \"ruleState\": \"ACTIVE\",\n"
            + "  \"groupingKeyNames\": [\"taxiId\", \"driverId\"],\n"
            + "  \"unique\": [],\n"
            + "  \"aggregateFieldName\": \"totalFare\",\n"
            + "  \"aggregatorFunctionType\": \"SUM\",\n"
            + "  \"limitOperatorType\": \"GREATER\",\n"
            + "  \"limit\": 50,\n"
            + "  \"windowMinutes\": 20\n"
            + "}"
        ;

        RuleParser ruleParser = new RuleParser();
        Rule rule1 = ruleParser.fromString(
            ruleString1
        );

        assertEquals(
            1, 
            (int) rule1.getRuleId(),
            "ID incorrect"
        );
        assertEquals(
            RuleState.ACTIVE, 
            rule1.getRuleState(),
            "Rule state incorrect"
        );
        assertEquals(
            lst("taxiId", "driverId"), 
            rule1.getGroupingKeyNames(),
            "Key names incorrect"
        );
        assertEquals(
            lst(), 
            rule1.getUnique(),
            "Unique names incorrect"
        );
        assertEquals(
            "totalFare", 
            rule1.getAggregateFieldName(),
            "Cumulative key incorrect"
        );
        assertEquals(
            AggregatorFunctionType.SUM,
            rule1.getAggregatorFunctionType(),
            "Aggregator function incorrect"
        );
        assertEquals(
            LimitOperatorType.GREATER, 
            rule1.getLimitOperatorType(),
            "Limit operator incorrect"
        );
        assertEquals(
            BigDecimal.valueOf(50), 
            rule1.getLimit(),
            "Limit incorrect"
        );
        assertEquals(
            20, 
            (int) rule1.getWindowMinutes(),
            "Window incorrect"
        );
    }
}
