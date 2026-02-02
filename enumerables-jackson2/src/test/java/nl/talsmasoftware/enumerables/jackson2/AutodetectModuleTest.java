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
package nl.talsmasoftware.enumerables.jackson2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutodetectModuleTest {

    private final ObjectMapper baseMapper = new ObjectMapper();
    private final PlainTestObject subject = new PlainTestObject(PlainTestObject.BigCo.APPLE);

    @Test
    void testDefaultObjectSerialization() throws JsonProcessingException, JSONException {
        ObjectMapper mapper = baseMapper;
        String expectedJson = "{\"bigCo\": {\"value\": \"Apple\"}}";
        String actualJson = mapper.writeValueAsString(subject);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void testFailedDefaultDeserialization() {
        ObjectMapper mapper = baseMapper;
        String json = "{\"bigCo\": {\"value\": \"Apple\"}}";
        assertThatThrownBy(() -> mapper.readValue(json, PlainTestObject.class))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    void testAutodetectedSerialization() throws JsonProcessingException, JSONException {
        ObjectMapper mapper = baseMapper.copy().findAndRegisterModules();
        String expectedJson = "{\"bigCo\": \"Apple\"}";
        String actualJson = mapper.writeValueAsString(subject);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void testAutodetectedDeserialization() throws IOException {
        ObjectMapper mapper = baseMapper.copy().findAndRegisterModules();
        String json1 = "{\"bigCo\": \"Apple\"}",
                json2 = "{\"bigCo\": {\"value\": \"Apple\"}}";

        assertThat(mapper.readValue(json1, PlainTestObject.class)).isEqualTo(subject);
        assertThat(mapper.readValue(json2, PlainTestObject.class)).isEqualTo(subject);
    }

    @Test
    void testAutodetectedSerializationAsObject() throws JsonProcessingException, JSONException {
        ObjectMapper mapper = baseMapper.copy().findAndRegisterModules();

        // Reconfigure the mapper to serialize only BigCo types as JSON object. All other Enumerables as Strings.
        SerializationMethod serializationMethod = SerializationMethod.AS_STRING.except(PlainTestObject.BigCo.class);
        mapper = mapper.setConfig(mapper.getSerializationConfig()
                .with(ContextAttributes.getEmpty().withSharedAttribute(SerializationMethod.class.getName(), serializationMethod)));

        String expectedJson = "{\"bigCo\": {\"value\": \"Apple\"}}";
        String actualJson = mapper.writeValueAsString(subject);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void testAutodetectedAsObjectFromExplicitWriter() throws JsonProcessingException, JSONException {
        // Create a writer that serializes only BigCo types to JSON objects. All other Enumerables as strings.
        ObjectWriter writer = baseMapper.copy().findAndRegisterModules()
                .writer(ContextAttributes.getEmpty().withSharedAttribute(
                        SerializationMethod.class.getName(),
                        SerializationMethod.AS_STRING.except(PlainTestObject.BigCo.class)));

        String expectedJson = "{\"bigCo\": {\"value\": \"Apple\"}}";
        String actualJson = writer.writeValueAsString(subject);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
