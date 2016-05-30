/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nl.talsmasoftware.enumerables.support.json.jackson1;

import nl.talsmasoftware.enumerables.CarBrand;
import nl.talsmasoftware.enumerables.support.Car;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static nl.talsmasoftware.enumerables.CarBrand.ASTON_MARTIN;
import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.JSON_OBJECT;
import static nl.talsmasoftware.testing.Fixtures.fixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableModuleTest {

    Car astonMartin = new Car(ASTON_MARTIN);

    static ObjectMapper mapperWith(Module module) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }

    @Test
    public void testSerialization() throws IOException, JSONException {
        String expectedJson = fixture("../aston_martin_string.json");
        String json = mapperWith(new EnumerableModule()).writeValueAsString(astonMartin);
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    public void testSerialization_jsonObject() throws IOException, JSONException {
        String expectedJson = fixture("../aston_martin_object.json");
        String json = mapperWith(new EnumerableModule(JSON_OBJECT)).writeValueAsString(astonMartin);
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerialization_jsonObject_exception() throws IOException, JSONException {
        String expectedJson = fixture("../aston_martin_string.json");
        String json = mapperWith(new EnumerableModule(JSON_OBJECT.except(CarBrand.class)))
                .writeValueAsString(astonMartin);
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    public void testDeserialization() throws IOException {
        Car parsed = mapperWith(new EnumerableModule()).readValue(fixture("../aston_martin_string.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

    @Test
    @Ignore // todo fix this test
    public void testDeserialization_jsonObject() throws IOException {
        Car parsed = mapperWith(new EnumerableModule()).readValue(fixture("../aston_martin_object.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

}
