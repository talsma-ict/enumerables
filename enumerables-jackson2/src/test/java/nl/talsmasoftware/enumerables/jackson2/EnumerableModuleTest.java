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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.jackson2.PlainTestObject.BigCo;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static nl.talsmasoftware.enumerables.jackson2.EnumerableDeserializerTest.jsonString;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumerableModuleTest {

    ObjectMapper mapper, mapperAsObject, mapperWithException;
    EnumerableModule module;

    private final ThreadLocal<Locale> defaultLocale = new ThreadLocal<>();

    @BeforeEach
    void setUpLocale() {
        defaultLocale.set(Locale.getDefault());
        Locale.setDefault(Locale.UK);
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(defaultLocale.get());
        defaultLocale.remove();
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @BeforeEach
    void setUpMappers() {
        module = new EnumerableModule();
        mapper = createMapper().registerModule(module);
        mapperAsObject = createMapper().registerModule(new EnumerableModule(AS_OBJECT));
        mapperWithException = createMapper().registerModule(new EnumerableModule(AS_OBJECT.except(BigCo.class)));
    }

    @Test
    void testSerialize_null() throws IOException {
        assertThat(mapper.writeValueAsString(null)).isEqualTo("null");
    }

    @Test
    void testSerialize_emptyString() throws IOException {
        assertThat(mapper.writeValueAsString(Enumerable.parse(BigCo.class, ""))).isEqualTo(jsonString(""));
    }

    @Test
    void testSerialize_stringValue() throws IOException {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            assertThat(mapper.writeValueAsString(bigCo)).isEqualTo(jsonString(bigCo.getValue()));
        }
        assertThat(mapper.writeValueAsString(Enumerable.parse(BigCo.class, "VMWare")))
                .isEqualTo(jsonString("VMWare"));
    }

    @Test
    void testSerialize_asObject() throws IOException, JSONException {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            String expected = String.format("{ \"value\" : \"%s\" }", bigCo.getValue());
            String actual = mapperAsObject.writeValueAsString(bigCo);
            JSONAssert.assertEquals(expected, actual, true);
        }
        String expected = "{ \"value\" : \"VMWare\" }";
        String actual = mapperAsObject.writeValueAsString(Enumerable.parse(BigCo.class, "VMWare"));
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testSerialize_usingContainerObject() throws IOException, JSONException {
        PlainTestObject testObject = new PlainTestObject();
        testObject.setBigCo(BigCo.MICROSOFT);

        // Serialization as string.
        String expected = "{ \"bigCo\" : \"Microsoft\" }";
        String actual = mapper.writeValueAsString(testObject);
        JSONAssert.assertEquals(expected, actual, true);

        // Serialization as object.
        expected = "{ \"bigCo\" :  { \"value\" : \"Microsoft\" } }";
        actual = mapperAsObject.writeValueAsString(testObject);
        JSONAssert.assertEquals(expected, actual, true);

        // Serialization as exception to object (i.e., String again)
        expected = "{ \"bigCo\" : \"Microsoft\" }";
        actual = mapperWithException.writeValueAsString(testObject);
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    void testDeserialize_nullString() {
        assertThatThrownBy(() -> mapper.readValue((String) null, BigCo.class))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class);
    }

    @Test
    void testDeserialize_jsonNullValue() throws IOException {
        assertThat(mapper.readValue("null", BigCo.class)).isNull();
    }

    @Test
    void testDeserialize_emptyString() throws IOException {
        BigCo emptyBigCo = mapper.readValue("\"\"", BigCo.class);
        assertThat(emptyBigCo).isNotNull().hasToString("BigCo{value=}");
        assertThat(emptyBigCo.getValue()).isEmpty();
    }

    @Test
    void testDeserialize_StringValue() throws IOException {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            BigCo actual = mapper.readValue(jsonString(bigCo.getValue()), BigCo.class);
            assertThat(actual).isSameAs(bigCo);
        }
        assertThat(mapper.readValue(jsonString("VMWare"), BigCo.class))
                .isEqualTo(Enumerable.parse(BigCo.class, "VMWare"));
    }

    @Test
    void testDeserialize_wrapperObject() throws IOException {
        String json = "{ \"bigCo\" : \"Microsoft\" }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);
        assertThat(actual).isEqualTo(new PlainTestObject(BigCo.MICROSOFT));
        assertThat(actual.getBigCo()).isSameAs(BigCo.MICROSOFT);

        actual = mapperAsObject.readValue(json, PlainTestObject.class);
        assertThat(actual).isEqualTo(new PlainTestObject(BigCo.MICROSOFT));
        assertThat(actual.getBigCo()).isSameAs(BigCo.MICROSOFT);
    }

    @Test
    void testDeserialize_objectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { \"value\" : \"IBM\" } }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);

        assertThat(actual).isEqualTo(new PlainTestObject(BigCo.IBM));
        assertThat(actual.getBigCo()).isSameAs(BigCo.IBM);
    }

    @Test
    void testDeserialize_objectRepresentation_emptyObject() {
        String json = "{ \"bigCo\" : { } }";
        assertThatThrownBy(() -> mapper.readValue(json, PlainTestObject.class))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    void testDeserialize_unknownObjectRepresentation() {
        String json = "{ \"bigCo\" : { \"val\" : \"IBM\" } }";
        assertThatThrownBy(() -> mapper.readValue(json, PlainTestObject.class))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    void testToString() {
        assertThat(module).hasToString("EnumerableModule");
    }

    @Test
    void testDeserialize_wrapperObjectFrom_treeAsTokens() throws IOException {
        String json = "{ \"bigCo\" : \"Microsoft\" }";

        final MappingJsonFactory jsonFactory = new MappingJsonFactory(mapper);
        final JsonParser jp = jsonFactory.createParser(json);

        ObjectNode objectNode = mapper.readTree(jp);
        PlainTestObject actual = mapper.readValue(mapper.treeAsTokens(objectNode), PlainTestObject.class);
        assertThat(actual).isEqualTo(new PlainTestObject(BigCo.MICROSOFT));
    }

    @Test
    void testHashcode_equals() {
        Set<EnumerableModule> set = new HashSet<>();
        assertThat(set.add(module)).isTrue();
        assertThat(set.add(module)).isFalse();
        assertThat(set.add(new EnumerableModule(AS_STRING))).isFalse();
        assertThat(set.add(new EnumerableModule(AS_OBJECT))).isFalse();
        assertThat(set.add(new EnumerableModule(AS_STRING.except(BigCo.class)))).isFalse();
        assertThat(set).hasSize(1);
    }
}
