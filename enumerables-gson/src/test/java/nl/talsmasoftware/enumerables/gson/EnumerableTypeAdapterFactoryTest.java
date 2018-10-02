/*
 * Copyright 2016-2018 Talsma ICT
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

import java.util.HashSet;
import java.util.Set;

import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableTypeAdapterFactoryTest {

    @Test
    public void testHashcodeEquals() {
        EnumerableTypeAdapterFactory factory = new EnumerableTypeAdapterFactory(null);
        Set<EnumerableTypeAdapterFactory> set = new HashSet<EnumerableTypeAdapterFactory>();
        assertThat(set.add(factory), is(true));
        assertThat(set.add(factory), is(false));
        assertThat(set.add(new EnumerableTypeAdapterFactory(AS_STRING)), is(false));
        assertThat(set.add(new EnumerableTypeAdapterFactory(AS_OBJECT)), is(true));
        assertThat(set.add(new EnumerableTypeAdapterFactory(AS_STRING.except(Car.Brand.class))), is(true));
        assertThat(set, hasSize(3));
    }

    @Test
    public void testToString() {
        assertThat(new EnumerableTypeAdapterFactory(null),
                hasToString(equalTo("EnumerableTypeAdapterFactory{As string}")));
        assertThat(new EnumerableTypeAdapterFactory(AS_STRING),
                hasToString(equalTo("EnumerableTypeAdapterFactory{As string}")));
        assertThat(new EnumerableTypeAdapterFactory(AS_OBJECT),
                hasToString(equalTo("EnumerableTypeAdapterFactory{As object}")));
        assertThat(new EnumerableTypeAdapterFactory(AS_OBJECT.except(Car.Brand.class)),
                hasToString(equalTo("EnumerableTypeAdapterFactory{As object, except [Car$Brand]}")));
    }
}
