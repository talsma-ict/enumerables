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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.jackson2.PlainTestObject.BigCo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EnumerableDeserializerTest {

    ObjectMapper mapper;

    static String jsonString(String content) {
        return '"' + content.replaceAll("[\"]", "\\" + '"') + '"';
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule bigCoModule = new SimpleModule("BigCo module", Version.unknownVersion());
        JsonDeserializer<? extends Enumerable> deserializer = new EnumerableDeserializer();
        bigCoModule.addDeserializer(Enumerable.class, deserializer);
        bigCoModule.addDeserializer(BigCo.class, (JsonDeserializer<BigCo>) deserializer);
        mapper.registerModule(bigCoModule);
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
        BigCo bigCoEmpty = mapper.readValue("\"\"", BigCo.class);
        assertThat(bigCoEmpty)
                .isNotNull()
                .hasToString("BigCo{value=}");
        assertThat(bigCoEmpty.getValue()).isEmpty();
    }

    @Test
    void testDeserialize_stringValue() throws IOException {
        assertThat(mapper.readValue(jsonString(BigCo.ORACLE.getValue()), BigCo.class)).isSameAs(BigCo.ORACLE);
        assertThat(mapper.readValue(jsonString(BigCo.IBM.getValue()), BigCo.class)).isSameAs(BigCo.IBM);
        assertThat(mapper.readValue(jsonString(BigCo.MICROSOFT.getValue()), BigCo.class)).isSameAs(BigCo.MICROSOFT);
        assertThat(mapper.readValue(jsonString(BigCo.APPLE.getValue()), BigCo.class)).isSameAs(BigCo.APPLE);
        assertThat(mapper.readValue(jsonString("VMWare"), BigCo.class))
                .isEqualTo(Enumerable.parse(BigCo.class, "VMWare"));
    }

    @Test
    void testDeserialize_asFieldInJsonObject() throws IOException {
        String json = "{ \"bigCo\" : \"Microsoft\" }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);
        assertThat(actual).isEqualTo(new PlainTestObject(BigCo.MICROSOFT));
        assertThat(actual.getBigCo()).isSameAs(BigCo.MICROSOFT);
    }

    @Test
    void testDeserialize_fromObjectRepresentation() throws IOException {
        String json = "{ \"bigCo\" : { \"value\" : \"IBM\", \"description\" : \"International Business Machines\" } }";
        PlainTestObject actual = mapper.readValue(json, PlainTestObject.class);
        assertThat(actual).isEqualTo(new PlainTestObject(BigCo.IBM));
        assertThat(actual.getBigCo()).isSameAs(BigCo.IBM);
    }

    @Test
    void testDeserialize_fromEmptyObjectRepresentation() {
        String json = "{ \"bigCo\" : { } }";
        assertThatThrownBy(() -> mapper.readValue(json, PlainTestObject.class))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    void testDeserialize_unknownObjectRepresentation() {
        String json = "{ \"bigCo\" : { \"val\" : \"IBM\", \"description\" : \"Ibm\" } }";
        assertThatThrownBy(() -> mapper.readValue(json, PlainTestObject.class))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    void testDeserialize_irrelevantOtherFields() throws IOException {
        String json = "{ \"bigCo\" : { \"name\" : \"Company name\", " +
                "\"childObject\" : {\"type\" : \"dummy\" }, " +
                "\"emptyArray\" : [], " +
                "\"someNumber\" : 12, " +
                "\"value\" : \"Apple\"" +
                " } }";
        assertThat(mapper.readValue(json, PlainTestObject.class)).isEqualTo(new PlainTestObject(BigCo.APPLE));
    }

    @Test
    void testDeserialize_EnumerableClass() throws IOException {
        String json = "{ \"value\" : \"IBM\", \"description\" : \"International Business Machines\" }";
        Enumerable deserialized = mapper.readValue(json, Enumerable.class);
        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getValue()).isEqualTo("IBM");
        assertThat(deserialized).isInstanceOf(EnumerableDeserializer.UnknownEnumerable.class);
    }

    @Test
    void testDeserialize_EnumberableField() throws IOException {
        String json = "{\"member\" : \"Value\" }";
        ContainsEnumerable container = mapper.readValue(json, ContainsEnumerable.class);
        assertThat(container).isNotNull();
        assertThat(container.member)
                .isNotNull()
                .isInstanceOf(EnumerableDeserializer.UnknownEnumerable.class);
        assertThat(container.member.getValue()).isEqualTo("Value");
    }

    @Test
    void testGetType_fromJsonParser() throws IOException {
        JsonParser jp = mock(JsonParser.class);
        when(jp.getTypeId()).thenReturn(PlainTestObject.BigCo.class);

        Class<? extends Enumerable> type = new EnumerableDeserializer().getType(jp);
        assertThat(type).isEqualTo(PlainTestObject.BigCo.class);

        verify(jp).getTypeId();
    }

    @Test
    void testGetType_fromJsonParser_nonEnumerable() throws IOException {
        JsonParser jp = mock(JsonParser.class);
        when(jp.getTypeId()).thenReturn(String.class);

        Class<? extends Enumerable> type = new EnumerableDeserializer().getType(jp);
        assertThat(type).isEqualTo(EnumerableDeserializer.UnknownEnumerable.class);

        verify(jp).getTypeId();
    }

    static class ContainsEnumerable {
        public Enumerable member;
    }
}
