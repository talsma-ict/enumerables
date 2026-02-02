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
package nl.talsmasoftware.enumerables.gson;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Sjoerd Talsma
 */
class GsonEnumerablesTest {

    @Test
    void testConfigureNulls() {
        assertThat(GsonEnumerables.configureGsonBuilder(null, null)).isNull();
    }

    @Test
    void testUnsupportedConstructor() throws Throwable {
        Constructor<GsonEnumerables> constructor = GsonEnumerables.class.getDeclaredConstructor();
        try {
            assertThat(constructor.isAccessible()).isFalse();
            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance)
                    .isInstanceOf(InvocationTargetException.class)
                    .cause()
                    .isInstanceOf(UnsupportedOperationException.class);
        } finally {
            constructor.setAccessible(false);
        }
    }

    @Test
    void testDefaultGsonBuilder() {
        GsonBuilder builder = GsonEnumerables.defaultGsonBuilder();
        assertThat(builder).isNotNull();
        assertThat(builder.create()).isNotNull();
        assertThat(builder.create().toJson(Car.Brand.AUDI)).isEqualTo("\"Audi\"");
    }

    @Test
    void testCreateGsonBuilder() {
        // default serialization
        assertThat(GsonEnumerables.createGsonBuilder(null)).isNotNull();
        assertThat(GsonEnumerables.createGsonBuilder(null).create()).isNotNull();
        assertThat(GsonEnumerables.createGsonBuilder(null).create().toJson(Car.Brand.AUDI))
                .isEqualTo("\"Audi\"");

        // String serialization == default
        assertThat(GsonEnumerables.createGsonBuilder(AS_STRING).create().toJson(Car.Brand.AUDI))
                .isEqualTo("\"Audi\"");

        // Object serialization
        assertThat(GsonEnumerables.createGsonBuilder(AS_OBJECT).create().toJson(Car.Brand.AUDI))
                .isEqualTo("{\"value\":\"Audi\"}");

        // Object serialization with exception
        assertThat(GsonEnumerables.createGsonBuilder(AS_OBJECT.except(Car.Brand.class)).create().toJson(Car.Brand.AUDI))
                .isEqualTo("\"Audi\"");
    }
}
