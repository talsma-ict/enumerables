/*
 * Copyright 2016-2025 Talsma ICT
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static nl.talsmasoftware.enumerables.gson.GsonEnumerables.createGsonBuilder;
import static nl.talsmasoftware.enumerables.gson.GsonEnumerables.defaultGsonBuilder;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Sjoerd Talsma
 */
class EnumerableGsonTest {

    static final String ASTON_MARTIN_OBJECT_JSON = "{\"brand\": {\"value\": \"Aston martin\"}}";
    static final String ASTON_MARTIN_STRING_JSON = "{\"brand\": \"Aston martin\"}";
    static final String EMPTY_BRAND_JSON = "{\"brand\": {}}";
    static final String NO_BRAND_JSON = "{\"brand\": null}";

    Car astonMartin = new Car(Car.Brand.ASTON_MARTIN);

    @Test
    void testSerialization() throws JSONException {
        String json = defaultGsonBuilder().create().toJson(astonMartin);
        JSONAssert.assertEquals(ASTON_MARTIN_STRING_JSON, json, true);
    }

    @Test
    void testSerialization_null() throws JSONException {
        String json = createGsonBuilder(AS_STRING).create().toJson(new Car());
        JSONAssert.assertEquals("{}", json, true);
    }

    @Test
    void testSerialization_jsonObject() throws JSONException {
        String json = createGsonBuilder(AS_OBJECT).create().toJson(astonMartin);
        JSONAssert.assertEquals(ASTON_MARTIN_OBJECT_JSON, json, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSerialization_jsonObject_exception() throws JSONException {
        String json = createGsonBuilder(AS_OBJECT.except(Car.Brand.class)).create().toJson(astonMartin);
        JSONAssert.assertEquals(ASTON_MARTIN_STRING_JSON, json, true);
    }

    @Test
    void testDeserialization() {
        Car parsed = defaultGsonBuilder().create().fromJson(ASTON_MARTIN_STRING_JSON, Car.class);
        assertThat(parsed).isEqualTo(astonMartin);
        parsed = createGsonBuilder(AS_STRING).create().fromJson(ASTON_MARTIN_STRING_JSON, Car.class);
        assertThat(parsed).isEqualTo(astonMartin);
        parsed = createGsonBuilder(AS_OBJECT).create().fromJson(ASTON_MARTIN_STRING_JSON, Car.class);
        assertThat(parsed).isEqualTo(astonMartin);
    }

    @Test
    void testDeserialization_null() {
        Car parsed = createGsonBuilder(AS_STRING).create().fromJson(NO_BRAND_JSON, Car.class);
        assertThat(parsed).isEqualTo(new Car());
    }

    @Test
    void testDeserialization_jsonObject() {
        Car parsed = createGsonBuilder(AS_STRING).create().fromJson(ASTON_MARTIN_OBJECT_JSON, Car.class);
        assertThat(parsed).isEqualTo(astonMartin);
        parsed = createGsonBuilder(AS_OBJECT).create().fromJson(ASTON_MARTIN_OBJECT_JSON, Car.class);
        assertThat(parsed).isEqualTo(astonMartin);
    }

    @Test
    void testDeserialization_jsonObject_valueNull() {
        Car parsed = createGsonBuilder(AS_OBJECT).create().fromJson("{\"brand\": {\"value\": null}}", Car.class);
        assertThat(parsed).isNotNull();
        assertThat(parsed.brand).isNull();
    }

    @Test
    void testDeserialization_array() {
        Gson gsonBuilder = defaultGsonBuilder().create();
        assertThatThrownBy(() -> gsonBuilder.fromJson("{\"brand\": []}", Car.class))
                .isInstanceOf(JsonSyntaxException.class);
    }

    @Test
    void testDeserialization_irrelevantFields() {
        Car parsed = defaultGsonBuilder().create().fromJson("{\"brand\": {\"value\": \"Aston martin\", \"type\": \"Sports coupe\"}}", Car.class);
        assertThat(parsed).isEqualTo(astonMartin);
    }

    @Test
    void testDeserialization_asBoolean() {
        Car parsed = defaultGsonBuilder().create().fromJson("{\"brand\": true}", Car.class);
        assertThat(parsed).isEqualTo(new Car("true"));

        parsed = defaultGsonBuilder().create().fromJson("{\"brand\": {\"value\": true}}", Car.class);
        assertThat(parsed).isEqualTo(new Car("true"));
    }

    @Test
    void testDeserialization_emptyBrandObject() {
        Gson gsonBuilder = defaultGsonBuilder().create();
        assertThatThrownBy(() -> gsonBuilder.fromJson(EMPTY_BRAND_JSON, Car.class))
                .isInstanceOf(JsonSyntaxException.class)
                .hasMessageContaining("Attribute \"value\" is required to parse an Enumerable JSON object.");
    }

}
