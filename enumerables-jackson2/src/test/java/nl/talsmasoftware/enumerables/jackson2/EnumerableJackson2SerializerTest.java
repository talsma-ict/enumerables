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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.jackson2.PlainTestObject.BigCo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static nl.talsmasoftware.enumerables.jackson2.EnumerableJackson2DeserializerTest.jsonString;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_OBJECT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class EnumerableJackson2SerializerTest {

    ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule bigCoModule = new SimpleModule("BigCo module", Version.unknownVersion());
        bigCoModule.addSerializer(BigCo.class, new EnumerableJackson2Serializer());
        mapper.registerModule(bigCoModule);
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
    public void testSerialize_asString() throws IOException {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            String expected = jsonString(bigCo.getValue());
            String actual = mapper.writeValueAsString(bigCo);
            assertThat(actual, is(equalTo(expected)));
        }
        String actual = mapper.writeValueAsString(Enumerable.parse(BigCo.class, "VMWare"));
        assertThat(actual, is(equalTo(jsonString("VMWare"))));
    }

    @Test
    public void testSerialize_asJsonObject() throws IOException {
        SimpleModule bigCoModule = new SimpleModule("BigCo module", Version.unknownVersion());
        bigCoModule.addSerializer(BigCo.class, new EnumerableJackson2Serializer(AS_OBJECT));
        mapper.registerModule(bigCoModule);

        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            String expected = "{\"value\":" + jsonString(bigCo.getValue()) + "}";
            String actual = mapper.writeValueAsString(bigCo);
            assertThat(actual, is(equalTo(expected)));
        }
        String expected = "{\"value\":" + jsonString("VMWare") + "}";
        String actual = mapper.writeValueAsString(Enumerable.parse(BigCo.class, "VMWare"));
        assertThat(actual, is(equalTo(expected)));
    }

}
