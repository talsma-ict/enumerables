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
package nl.talsmasoftware.enumerables.gson;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

/**
 * @author Sjoerd Talsma
 */
public class GsonEnumerablesTest {

    @Test
    public void testConfigureNulls() {
        assertThat(GsonEnumerables.configureGsonBuilder(null, null), is(nullValue()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedConstructor() throws Throwable {
        Constructor<GsonEnumerables> constructor = GsonEnumerables.class.getDeclaredConstructor();
        try {
            assertThat(constructor.isAccessible(), is(false));
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Exception expected.");
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        } finally {
            constructor.setAccessible(false);
        }
    }

    @Test
    public void testDefaultGsonBuilder() {
        assertThat(GsonEnumerables.defaultGsonBuilder(), is(notNullValue()));
        assertThat(GsonEnumerables.defaultGsonBuilder().create(), is(notNullValue()));
        assertThat(GsonEnumerables.defaultGsonBuilder().create().toJson(Car.Brand.AUDI), is(equalTo("\"Audi\"")));
    }

    @Test
    public void testCreateGsonBuilder() {
        // default serialization
        assertThat(GsonEnumerables.createGsonBuilder(null), is(notNullValue()));
        assertThat(GsonEnumerables.createGsonBuilder(null).create(), is(notNullValue()));
        assertThat(GsonEnumerables.createGsonBuilder(null).create().toJson(Car.Brand.AUDI),
                is(equalTo("\"Audi\"")));

        // String serialization == default
        assertThat(GsonEnumerables.createGsonBuilder(AS_STRING).create().toJson(Car.Brand.AUDI),
                is(equalTo("\"Audi\"")));

        // Object serialization
        assertThat(GsonEnumerables.createGsonBuilder(AS_OBJECT).create().toJson(Car.Brand.AUDI),
                is(equalTo("{\"value\":\"Audi\"}")));

        // Object serialization with exception
        assertThat(GsonEnumerables.createGsonBuilder(AS_OBJECT.except(Car.Brand.class)).create().toJson(Car.Brand.AUDI),
                is(equalTo("\"Audi\"")));
    }
}
