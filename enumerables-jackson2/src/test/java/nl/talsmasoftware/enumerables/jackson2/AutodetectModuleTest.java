/*
 * Copyright 2016-2020 Talsma ICT
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
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class AutodetectModuleTest {

    private final ObjectMapper baseMapper = new ObjectMapper();
    private final PlainTestObject subject = new PlainTestObject(PlainTestObject.BigCo.APPLE);

    @Test
    public void testDefaultObjectSerialization() throws JsonProcessingException, JSONException {
        ObjectMapper mapper = baseMapper;
        String expectedJson = "{\"bigCo\": {\"value\": \"Apple\"}}";
        String actualJson = mapper.writeValueAsString(subject);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailedDefaultDeserialization() throws IOException {
        ObjectMapper mapper = baseMapper;
        String json = "{\"bigCo\": {\"value\": \"Apple\"}}";
        mapper.readValue(json, PlainTestObject.class);
        fail("Missing deserializer exception expected.");
    }

    @Test
    public void testAutodetectedSerialization() throws JsonProcessingException, JSONException {
        ObjectMapper mapper = baseMapper.copy().findAndRegisterModules();
        String expectedJson = "{\"bigCo\": \"Apple\"}";
        String actualJson = mapper.writeValueAsString(subject);
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void testAutodetectedDeserialization() throws IOException {
        ObjectMapper mapper = baseMapper.copy().findAndRegisterModules();
        String json1 = "{\"bigCo\": \"Apple\"}",
                json2 = "{\"bigCo\": {\"value\": \"Apple\"}}";

        assertThat(mapper.readValue(json1, PlainTestObject.class), is(equalTo(subject)));
        assertThat(mapper.readValue(json2, PlainTestObject.class), is(equalTo(subject)));
    }

    @Test
    public void testAutodetectedSerializationAsObject() throws JsonProcessingException, JSONException {
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
    public void testAutodetectedAsObjectFromExplicitWriter() throws JsonProcessingException, JSONException {
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
