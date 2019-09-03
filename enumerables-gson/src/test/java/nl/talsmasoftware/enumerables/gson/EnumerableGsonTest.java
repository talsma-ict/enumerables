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
package nl.talsmasoftware.enumerables.gson;

import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static nl.talsmasoftware.enumerables.gson.GsonEnumerables.createGsonBuilder;
import static nl.talsmasoftware.enumerables.gson.GsonEnumerables.defaultGsonBuilder;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableGsonTest {

    static final String ASTON_MARTIN_OBJECT_JSON = "{\"brand\": {\"value\": \"Aston martin\"}}";
    static final String ASTON_MARTIN_STRING_JSON = "{\"brand\": \"Aston martin\"}";
    static final String EMPTY_BRAND_JSON = "{\"brand\": {}}";
    static final String NO_BRAND_JSON = "{\"brand\": null}";

    Car astonMartin = new Car(Car.Brand.ASTON_MARTIN);

    @Test
    public void testSerialization() throws IOException, JSONException {
        String json = defaultGsonBuilder().create().toJson(astonMartin);
        JSONAssert.assertEquals(ASTON_MARTIN_STRING_JSON, json, true);
    }

    @Test
    public void testSerialization_null() throws IOException, JSONException {
        String json = createGsonBuilder(AS_STRING).create().toJson(new Car());
        JSONAssert.assertEquals("{}", json, true);
    }

    @Test
    public void testSerialization_jsonObject() throws IOException, JSONException {
        String json = createGsonBuilder(AS_OBJECT).create().toJson(astonMartin);
        JSONAssert.assertEquals(ASTON_MARTIN_OBJECT_JSON, json, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerialization_jsonObject_exception() throws IOException, JSONException {
        String json = createGsonBuilder(AS_OBJECT.except(Car.Brand.class)).create().toJson(astonMartin);
        JSONAssert.assertEquals(ASTON_MARTIN_STRING_JSON, json, true);
    }

    @Test
    public void testDeserialization() throws IOException {
        Car parsed = defaultGsonBuilder().create().fromJson(ASTON_MARTIN_STRING_JSON, Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
        parsed = createGsonBuilder(AS_STRING).create().fromJson(ASTON_MARTIN_STRING_JSON, Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
        parsed = createGsonBuilder(AS_OBJECT).create().fromJson(ASTON_MARTIN_STRING_JSON, Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

    @Test
    public void testDeserialization_null() throws IOException {
        Car parsed = createGsonBuilder(AS_STRING).create().fromJson(NO_BRAND_JSON, Car.class);
        assertThat(parsed, is(equalTo(new Car())));
    }

    @Test
    public void testDeserialization_jsonObject() throws IOException {
        Car parsed = createGsonBuilder(AS_STRING).create().fromJson(ASTON_MARTIN_OBJECT_JSON, Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
        parsed = createGsonBuilder(AS_OBJECT).create().fromJson(ASTON_MARTIN_OBJECT_JSON, Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

    @Test
    public void testDeserialization_jsonObject_valueNull() throws IOException {
        Car parsed = createGsonBuilder(AS_OBJECT).create().fromJson("{\"brand\": {\"value\": null}}", Car.class);
        assertThat(parsed, is(notNullValue()));
        assertThat(parsed.brand, is(nullValue()));
    }

    @Test(expected = JsonSyntaxException.class)
    public void testDeserialization_array() throws IOException {
        defaultGsonBuilder().create().fromJson("{\"brand\": []}", Car.class);
        fail("Json syntax exception expected.");
    }

    @Test
    public void testDeserialization_irrelevantFields() throws IOException {
        Car parsed = defaultGsonBuilder().create().fromJson("{\"brand\": {\"value\": \"Aston martin\", \"type\": \"Sports coupe\"}}", Car.class);
        assertThat(parsed, is(equalTo(astonMartin)));
    }

    @Test
    public void testDeserialization_asBoolean() throws IOException {
        Car parsed = defaultGsonBuilder().create().fromJson("{\"brand\": true}", Car.class);
        assertThat(parsed, is(equalTo(new Car("true"))));

        parsed = defaultGsonBuilder().create().fromJson("{\"brand\": {\"value\": true}}", Car.class);
        assertThat(parsed, is(equalTo(new Car("true"))));
    }

    @Test
    public void testDeserialization_emptyBrandObject() {
        try {
            defaultGsonBuilder().create().fromJson(EMPTY_BRAND_JSON, Car.class);
            fail("Exception expected.");
        } catch (JsonSyntaxException expected) {
            assertThat(expected.getMessage(), containsString("Attribute \"value\" is required to parse an Enumerable JSON object."));
        }
    }

}