/*
 * Copyright 2016-2026 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.talsmasoftware.enumerables.jackson3;

import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.jackson3.model.BigCo;
import nl.talsmasoftware.enumerables.jackson3.model.PlainTestObject;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class EnumerableJsonDeserializerTest {

    static final JsonMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    @Test
    void testDeserialize() {
        String json = "{\"bigCo\":\"Open AI\"}";

        PlainTestObject result = MAPPER.readValue(json, PlainTestObject.class);
        assertThat(result).isNotNull();
        assertThat(result.getBigCo())
                .isInstanceOf(BigCo.class)
                .isEqualTo(Enumerable.parse(BigCo.class, "Open AI"));
    }

}
