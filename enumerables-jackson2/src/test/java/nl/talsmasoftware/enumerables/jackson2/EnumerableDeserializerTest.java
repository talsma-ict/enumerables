/*
 * Copyright 2016-2017 Talsma ICT
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

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

    @Test(expected = NullPointerException.class)
    public void testDeserialize_nullString() throws IOException {
        mapper.readValue((String) null, BigCo.class);
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
    public void testDeserialize_EnumerableClass() throws IOException {
        String json = "{ \"value\" : \"IBM\", \"description\" : \"International Business Machines\" }";
        Enumerable deserialized = mapper.readValue(json, Enumerable.class);
        assertThat(deserialized, is(notNullValue()));
        assertThat(deserialized.getValue(), is(equalTo("IBM")));
        assertThat(deserialized, is(instanceOf(EnumerableDeserializer.UnknownEnumerable.class)));
    }

}
