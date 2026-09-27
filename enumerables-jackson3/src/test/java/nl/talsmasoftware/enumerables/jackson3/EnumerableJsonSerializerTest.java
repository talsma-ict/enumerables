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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.json.JsonMapper;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EnumerableJsonSerializerTest {

    static final JsonMapper MAPPER = JsonMapper.builder().findAndAddModules().build();

    @Test
    @DisplayName("Json serialization: null becomes null.")
    void testSerialize_null() {
        String result = MAPPER.writeValueAsString(new PlainTestObject(null));

        assertThatJson(result)
                .isObject()
                .containsEntry("bigCo", null);
    }

    @Test
    @DisplayName("Json serialization: constants are serialized using their string value.")
    void testSerialize_values() {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            String result = MAPPER.writeValueAsString(new PlainTestObject(bigCo));
            assertThatJson(result)
                    .isObject()
                    .containsEntry("bigCo", bigCo.getValue());
        }
    }

    @Test
    @DisplayName("Json serialization: null serializable values can be serialized (handled by Jackson).")
    void testSerialize_null_serializable() {
        String result = MAPPER.writeValueAsString(new PlainTestObject(null));
        assertThatJson(result)
                .isObject()
                .containsEntry("bigCo", null);
    }

    @Test
    @DisplayName("Json serialization: null value can be serialized (by serializer)")
    void testSerialize_valueNull() {
        // given
        EnumerableSerializer subject = new EnumerableSerializer();
        JsonGenerator jsonGeneratorMock = mock(JsonGenerator.class);
        SerializationContext serializationContextMock = mock(SerializationContext.class);

        // when
        subject.serialize(null, jsonGeneratorMock, serializationContextMock);

        // then
        verify(jsonGeneratorMock).writeNull();
    }
}
