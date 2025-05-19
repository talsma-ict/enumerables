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
package nl.talsmasoftware.enumerables.jackson2;

import nl.talsmasoftware.enumerables.Enumerable;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sjoerd Talsma
 */
class SerializationMethodTest {

    @Test
    void testIsObjectSerializationByDefault() {
        assertThat(AS_STRING.isObjectSerializationByDefault()).isFalse();
        assertThat(AS_OBJECT.isObjectSerializationByDefault()).isTrue();
    }

    @Test
    void testStandardBehaviour() {
        assertThat(AS_STRING.serializeAsObject(Enumerable1.class)).isFalse();
        assertThat(AS_STRING.serializeAsObject(Enumerable2.class)).isFalse();
        assertThat(AS_STRING.serializeAsObject(Enumerable3.class)).isFalse();

        assertThat(AS_OBJECT.serializeAsObject(Enumerable1.class)).isTrue();
        assertThat(AS_OBJECT.serializeAsObject(Enumerable2.class)).isTrue();
        assertThat(AS_OBJECT.serializeAsObject(Enumerable3.class)).isTrue();
    }

    @Test
    void testSerializeAsObjectNull() {
        assertThat(AS_STRING.serializeAsObject(null)).isFalse();
        assertThat(AS_OBJECT.serializeAsObject(null)).isTrue();
    }

    @Test
    @SuppressWarnings({"unchecked", "deprecation"})
    void testAsStringExceptBehaviour() {
        SerializationMethod serializationMethod = AS_STRING;
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isFalse();

        serializationMethod = AS_STRING.except(Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isTrue();

        serializationMethod = AS_STRING.except(Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isTrue();

        serializationMethod = AS_STRING.except(Enumerable1.class, Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Numbers.class)).isFalse();
    }

    @Test
    @SuppressWarnings({"unchecked", "deprecation"})
    void testAsObjectExceptBehaviour() {
        SerializationMethod serializationMethod = AS_OBJECT;
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isTrue();

        serializationMethod = AS_OBJECT.except(Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isFalse();

        serializationMethod = AS_OBJECT.except(Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isTrue();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isFalse();

        serializationMethod = AS_OBJECT.except(Enumerable1.class, Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class)).isFalse();
        assertThat(serializationMethod.serializeAsObject(Numbers.class)).isTrue();
    }

    @Test
    void testExceptNull() {
        assertThat(AS_STRING.except((Class<? extends Enumerable>) null)).isSameAs(AS_STRING);
    }

    @Test
    void testHashCode() {
        assertThat(AS_STRING)
                .hasSameHashCodeAs(AS_STRING)
                .doesNotHaveSameHashCodeAs(AS_OBJECT) // Stronger than hashcode contract
                .hasSameHashCodeAs(AS_STRING.except((Class<? extends Enumerable>) null));

        assertThat(AS_OBJECT.except(Enumerable2.class)).hasSameHashCodeAs(AS_OBJECT.except(Enumerable2.class));
    }

    @Test
    void testEquals() {
        assertThat(AS_STRING)
                .isEqualTo(AS_STRING)
                .isNotEqualTo(AS_STRING.except(Enumerable1.class))
                .isNotEqualTo(AS_OBJECT);

        assertThat(AS_OBJECT.except(Enumerable2.class)).isEqualTo(AS_OBJECT.except(Enumerable2.class));
    }

    @Test
    void testToString() {
        assertThat(AS_STRING).hasToString("As string");
        assertThat(AS_OBJECT).hasToString("As object");
        assertThat(AS_STRING.except(Enumerable3.class))
                .hasToString("As string, except [SerializationMethodTest$Enumerable3]");
        assertThat(AS_OBJECT.except(asList(Enumerable1.class, Enumerable3.class))).hasToString(
                "As object, except [SerializationMethodTest$Enumerable1, SerializationMethodTest$Enumerable3]");
    }

    static final class Enumerable1 extends Enumerable {
        Enumerable1(String value) {
            super(value);
        }
    }

    static final class Enumerable2 extends Enumerable {
        Enumerable2(String value) {
            super(value);
        }
    }

    static final class Enumerable3 extends Enumerable {
        Enumerable3(String value) {
            super(value);
        }
    }

}
