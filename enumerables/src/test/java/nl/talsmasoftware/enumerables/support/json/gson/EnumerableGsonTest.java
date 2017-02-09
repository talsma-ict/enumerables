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

package nl.talsmasoftware.enumerables.support.json.gson;

import com.google.gson.JsonSyntaxException;
import nl.talsmasoftware.enumerables.CarBrand;
import nl.talsmasoftware.enumerables.support.Car;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static nl.talsmasoftware.enumerables.CarBrand.ASTON_MARTIN;
import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.JSON_OBJECT;
import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;
import static nl.talsmasoftware.enumerables.support.json.gson.EnumerableGsonBuilderFactory.createGsonBuilder;
import static nl.talsmasoftware.enumerables.support.json.gson.EnumerableGsonBuilderFactory.defaultGsonBuilder;
import static nl.talsmasoftware.testing.Fixtures.fixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableGsonTest {

    Car astonMartin = new Car(ASTON_MARTIN);

    @Test
    public void testSerialization() throws IOException, JSONException {
        String expectedJson = fixture("../aston_martin_string.json");
        String json = defaultGsonBuilder().create().toJson(astonMartin);
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    public void testSerialization_null() throws IOException, JSONException {
        String expectedJson = fixture("../car_without_brand.json");
        String json = createGsonBuilder(PLAIN_STRING).create().toJson(new Car());
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    public void testSerialization_jsonObject() throws IOException, JSONException {
        String expectedJson = fixture("../aston_martin_object.json");
        String json = createGsonBuilder(JSON_OBJECT).create().toJson(astonMartin);
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerialization_jsonObject_exception() throws IOException, JSONException {
        String expectedJson = fixture("../aston_martin_string.json");
        String json = createGsonBuilder(JSON_OBJECT.except(CarBrand.class)).create().toJson(astonMartin);
        JSONAssert.assertEquals(expectedJson, json, true);
    }

    @Test
    public void testDeserialization() throws IOException {
        Car parsed = defaultGsonBuilder().create().fromJson(fixture("../aston_martin_string.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
        parsed = createGsonBuilder(PLAIN_STRING).create().fromJson(fixture("../aston_martin_string.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
        parsed = createGsonBuilder(JSON_OBJECT).create().fromJson(fixture("../aston_martin_string.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

    @Test
    public void testDeserialization_null() throws IOException {
        Car parsed = createGsonBuilder(PLAIN_STRING).create().fromJson(fixture("../car_without_brand.json"), Car.class);
        assertThat(parsed, is(equalTo(new Car())));
    }

    @Test
    public void testDeserialization_jsonObject() throws IOException {
        Car parsed = createGsonBuilder(PLAIN_STRING).create().fromJson(fixture("../aston_martin_object.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
        parsed = createGsonBuilder(JSON_OBJECT).create().fromJson(fixture("../aston_martin_object.json"), Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

    @Test
    public void testDeserialization_emptyBrandObject() {
        try {
            defaultGsonBuilder().create().fromJson(fixture("../car_with_empty_brand.json"), Car.class);
            fail("Exception expected.");
        } catch (JsonSyntaxException expected) {
            assertThat(expected.getMessage(), containsString("Attribute \"value\" is required to parse an Enumerable JSON object."));
        }
    }

}
