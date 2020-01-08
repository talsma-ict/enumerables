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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.jackson2.PlainTestObject.BigCo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnumerableDeserializerTest {

    ObjectMapper mapper;

    static String jsonString(String content) {
        return '"' + content.replaceAll("[\"]", "\\" + '"') + '"';
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        mapper = new ObjectMapper();
        SimpleModule bigCoModule = new SimpleModule("BigCo module", Version.unknownVersion());
        JsonDeserializer<? extends Enumerable> deserializer = new EnumerableDeserializer();
        bigCoModule.addDeserializer(Enumerable.class, deserializer);
        bigCoModule.addDeserializer(BigCo.class, (JsonDeserializer<BigCo>) deserializer);
        mapper.registerModule(bigCoModule);
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
        assertThat(mapper.readValue("null", BigCo.class), is(nullValue()));
    }

    @Test
    public void testDeserialize_emptyString() throws IOException {
        BigCo bigCoLeeg = mapper.readValue("\"\"", BigCo.class);
        assertThat(bigCoLeeg, is(notNullValue()));
        assertThat(bigCoLeeg.getValue(), is(equalTo("")));
        assertThat(bigCoLeeg, hasToString("BigCo{value=}"));
    }

    @Test
    public void testDeserialize_stringValue() throws IOException {
        assertThat(mapper.readValue(jsonString(BigCo.ORACLE.getValue()), BigCo.class), is(sameInstance(BigCo.ORACLE)));
        assertThat(mapper.readValue(jsonString(BigCo.IBM.getValue()), BigCo.class), is(sameInstance(BigCo.IBM)));
        assertThat(mapper.readValue(jsonString(BigCo.MICROSOFT.getValue()), BigCo.class), is(sameInstance(BigCo.MICROSOFT)));
        assertThat(mapper.readValue(jsonString(BigCo.APPLE.getValue()), BigCo.class), is(sameInstance(BigCo.APPLE)));
        assertThat(mapper.readValue(jsonString("VMWare"), BigCo.class), is(equalTo(Enumerable.parse(BigCo.class, "VMWare"))));
    }

    @Test
    public void testDeserialize_asFieldInJsonObject() throws IOException {
        String json = "{ \"bigCo\" : \"Microsoft\" }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);
        assertThat(actual, is(equalTo(new PlainTestObject(BigCo.MICROSOFT))));
        assertThat(actual.getBigCo(), is(sameInstance(BigCo.MICROSOFT)));
    }

    @Test
    public void testDeserialize_fromObjectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { \"value\" : \"IBM\", \"description\" : \"International Business Machines\" } }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);
        assertThat(actual, is(equalTo(new PlainTestObject(BigCo.IBM))));
        assertThat(actual.getBigCo(), is(sameInstance(BigCo.IBM)));
    }

    @Test(expected = JsonMappingException.class)
    public void testDeserialize_fromEmptyObjectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { } }";
        mapper.readValue(json, PlainTestObject.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testDeserialize_unknownObjectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { \"val\" : \"IBM\", \"description\" : \"Ibm\" } }";
        mapper.readValue(json, PlainTestObject.class);
    }

    @Test
    public void testDeserialize_irrelevantOtherFields() throws IOException {
        String json = "{ \"bigCo\" : { \"name\" : \"Company name\", " +
                "\"childObject\" : {\"type\" : \"dummy\" }, " +
                "\"emptyArray\" : [], " +
                "\"someNumber\" : 12, " +
                "\"value\" : \"Apple\"" +
                " } }";
        assertThat(mapper.readValue(json, PlainTestObject.class), is(equalTo(new PlainTestObject(BigCo.APPLE))));
    }

    @Test
    public void testDeserialize_EnumerableClass() throws IOException {
        String json = "{ \"value\" : \"IBM\", \"description\" : \"International Business Machines\" }";
        Enumerable deserialized = mapper.readValue(json, Enumerable.class);
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getValue(), is(equalTo("IBM")));
        assertThat(deserialized, is(instanceOf(EnumerableDeserializer.UnknownEnumerable.class)));
    }

    @Test
    public void testDeserialize_EnumberableField() throws IOException {
        String json = "{\"member\" : \"Value\" }";
        ContainsEnumerable container = mapper.readValue(json, ContainsEnumerable.class);
        assertThat(container, is(notNullValue()));
        assertThat(container.member, is(notNullValue()));
        assertThat(container.member, is(instanceOf(EnumerableDeserializer.UnknownEnumerable.class)));
        assertThat(container.member.getValue(), is("Value"));
    }

    @Test
    public void testGetType_fromJsonParser() throws IOException {
        JsonParser jp = mock(JsonParser.class);
        when(jp.getTypeId()).thenReturn(PlainTestObject.BigCo.class);

        Class<? extends Enumerable> type = new EnumerableDeserializer().getType(jp);
        assertThat(type, equalTo((Class) PlainTestObject.BigCo.class));

        verify(jp).getTypeId();
    }

    @Test
    public void testGetType_fromJsonParser_nonEnumerable() throws IOException {
        JsonParser jp = mock(JsonParser.class);
        when(jp.getTypeId()).thenReturn(String.class);

        Class<? extends Enumerable> type = new EnumerableDeserializer().getType(jp);
        assertThat(type, equalTo((Class) EnumerableDeserializer.UnknownEnumerable.class));

        verify(jp).getTypeId();
    }

    static class ContainsEnumerable {
        public Enumerable member;
    }
}
