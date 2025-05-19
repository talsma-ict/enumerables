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

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.gson.SerializationMethod.AS_STRING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sjoerd Talsma
 */
class EnumerableTypeAdapterFactoryTest {

    @Test
    void testHashcodeEquals() {
        EnumerableTypeAdapterFactory factory = new EnumerableTypeAdapterFactory(null);
        Set<EnumerableTypeAdapterFactory> set = new HashSet<EnumerableTypeAdapterFactory>();
        assertThat(set.add(factory)).isTrue();
        assertThat(set.add(factory)).isFalse();
        assertThat(set.add(new EnumerableTypeAdapterFactory(AS_STRING))).isFalse();
        assertThat(set.add(new EnumerableTypeAdapterFactory(AS_OBJECT))).isTrue();
        assertThat(set.add(new EnumerableTypeAdapterFactory(AS_STRING.except(Car.Brand.class)))).isTrue();
        assertThat(set).hasSize(3);
    }

    @Test
    void testToString() {
        assertThat(new EnumerableTypeAdapterFactory(null))
                .hasToString("EnumerableTypeAdapterFactory{As string}");
        assertThat(new EnumerableTypeAdapterFactory(AS_STRING))
                .hasToString("EnumerableTypeAdapterFactory{As string}");
        assertThat(new EnumerableTypeAdapterFactory(AS_OBJECT))
                .hasToString("EnumerableTypeAdapterFactory{As object}");
        assertThat(new EnumerableTypeAdapterFactory(AS_OBJECT.except(Car.Brand.class)))
                .hasToString("EnumerableTypeAdapterFactory{As object, except [Car$Brand]}");
    }
}
