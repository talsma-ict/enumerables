/*
 * Copyright 2016-2019 Talsma ICT
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
import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static nl.talsmasoftware.enumerables.jackson2.EnumerableDeserializerTest.jsonString;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

public class EnumerableModuleTest {

    ObjectMapper mapper, mapperAsObject, mapperWithException;
    EnumerableModule module;

    private final ThreadLocal<Locale> defaultLocale = new ThreadLocal<Locale>();

    @Before
    public void setUpLocale() {
        defaultLocale.set(Locale.getDefault());
        Locale.setDefault(Locale.UK);
    }

    @After
    public void restoreLocale() {
        Locale.setDefault(defaultLocale.get());
        defaultLocale.remove();
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Before
    public void setUpMappers() {
        module = new EnumerableModule();
        mapper = createMapper().registerModule(module);
        mapperAsObject = createMapper().registerModule(new EnumerableModule(AS_OBJECT));
        mapperWithException = createMapper().registerModule(new EnumerableModule(AS_OBJECT.except(BigCo.class)));
    }

    @Test
    public void testSerialize_null() throws IOException {
        assertThat(mapper.writeValueAsString(null), is(equalTo("null")));
    }

    @Test
    public void testSerialize_emptyString() throws IOException {
        assertThat(mapper.writeValueAsString(Enumerable.parse(BigCo.class, "")), is(equalTo(jsonString(""))));
    }

    @Test
    public void testSerialize_stringValue() throws IOException {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            assertThat(mapper.writeValueAsString(bigCo), is(equalTo(jsonString(bigCo.getValue()))));
        }
        assertThat(mapper.writeValueAsString(Enumerable.parse(BigCo.class, "VMWare")),
                is(equalTo(jsonString("VMWare"))));
    }

    @Test
    public void testSerialize_asObject() throws IOException, JSONException {
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
    public void testSerialize_usingContainerObject() throws IOException, JSONException {
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

        // Serialization as exception to object (i.e. String again)
        expected = "{ \"bigCo\" : \"Microsoft\" }";
        actual = mapperWithException.writeValueAsString(testObject);
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void testDeserialize_nullString() throws IOException {
        try {
            mapper.readValue((String) null, BigCo.class);
            throw new AssertionError("Exception expected");
        } catch (RuntimeException expected) {
            assertThat(expected, anyOf(
                    instanceOf(NullPointerException.class),
                    instanceOf(IllegalArgumentException.class)));
        }
    }

    @Test
    public void testDeserialize_jsonNullValue() throws IOException {
        assertThat(mapper.readValue("null", BigCo.class), nullValue());
    }

    @Test
    public void testDeserialize_emptyString() throws IOException {
        BigCo emptyBigCo = mapper.readValue("\"\"", BigCo.class);
        MatcherAssert.assertThat(emptyBigCo, notNullValue());
        MatcherAssert.assertThat(emptyBigCo.getValue(), is(equalTo("")));
        MatcherAssert.assertThat(emptyBigCo.toString(), is(equalTo("BigCo{value=}")));
    }

    @Test
    public void testDeserialize_StringValue() throws IOException {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            BigCo actual = mapper.readValue(jsonString(bigCo.getValue()), BigCo.class);
            assertThat(actual, is(sameInstance(bigCo)));
        }
        assertThat(mapper.readValue(jsonString("VMWare"), BigCo.class),
                is(equalTo(Enumerable.parse(BigCo.class, "VMWare"))));
    }

    @Test
    public void testDeserialize_wrapperObject() throws IOException {
        String json = "{ \"bigCo\" : \"Microsoft\" }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);
        assertThat(actual, is(equalTo(new PlainTestObject(BigCo.MICROSOFT))));
        assertThat(actual.getBigCo(), is(sameInstance(BigCo.MICROSOFT)));

        actual = mapperAsObject.readValue(json, PlainTestObject.class);
        assertThat(actual, is(equalTo(new PlainTestObject(BigCo.MICROSOFT))));
        assertThat(actual.getBigCo(), is(sameInstance(BigCo.MICROSOFT)));
    }

    @Test
    public void testDeserialize_objectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { \"value\" : \"IBM\" } }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);

        assertThat(actual, is(equalTo(new PlainTestObject(BigCo.IBM))));
        assertThat(actual.getBigCo(), is(sameInstance(BigCo.IBM)));
    }

    @Test(expected = JsonMappingException.class)
    public void testDeserialize_objectRepresentation_emptyObject() throws IOException {
        String json = "{ \"bigCo\" : { } }";
        mapper.readValue(json, PlainTestObject.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testDeserialize_unknownObjectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { \"val\" : \"IBM\" } }";
        mapper.readValue(json, PlainTestObject.class);
    }

    @Test
    public void testToString() {
        assertThat(module, hasToString("EnumerableModule"));
    }

    @Test
    public void testDeserialize_wrapperObjectFrom_treeAsTokens() throws IOException {
        String json = "{ \"bigCo\" : \"Microsoft\" }";

        final MappingJsonFactory jsonFactory = new MappingJsonFactory(mapper);
        final JsonParser jp = jsonFactory.createParser(json);

        ObjectNode objectNode = mapper.readTree(jp);
        PlainTestObject actual = mapper.readValue(mapper.treeAsTokens(objectNode), PlainTestObject.class);
        assertThat(actual, is(equalTo(new PlainTestObject(BigCo.MICROSOFT))));
    }

    @Test
    public void testHashcode_equals() {
        Set<EnumerableModule> set = new HashSet<EnumerableModule>();
        assertThat(set.add(module), is(true));
        assertThat(set.add(module), is(false));
        assertThat(set.add(new EnumerableModule(AS_STRING)), is(false));
        assertThat(set.add(new EnumerableModule(AS_OBJECT)), is(false));
        assertThat(set.add(new EnumerableModule(AS_STRING.except(BigCo.class))), is(false));
        assertThat(set, hasSize(1));
    }
}
