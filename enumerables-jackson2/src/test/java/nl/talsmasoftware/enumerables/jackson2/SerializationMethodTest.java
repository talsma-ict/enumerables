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
package nl.talsmasoftware.enumerables.jackson2;

import nl.talsmasoftware.enumerables.Enumerable;
import org.junit.Test;

import static java.util.Arrays.asList;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_OBJECT;
import static nl.talsmasoftware.enumerables.jackson2.SerializationMethod.AS_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

/**
 * @author Sjoerd Talsma
 */
public class SerializationMethodTest {

    @Test
    public void testIsObjectSerializationByDefault() {
        assertThat(AS_STRING.isObjectSerializationByDefault(), is(false));
        assertThat(AS_OBJECT.isObjectSerializationByDefault(), is(true));
    }

    @Test
    public void testStandardBehaviour() {
        assertThat(AS_STRING.serializeAsObject(Enumerable1.class), is(false));
        assertThat(AS_STRING.serializeAsObject(Enumerable2.class), is(false));
        assertThat(AS_STRING.serializeAsObject(Enumerable3.class), is(false));

        assertThat(AS_OBJECT.serializeAsObject(Enumerable1.class), is(true));
        assertThat(AS_OBJECT.serializeAsObject(Enumerable2.class), is(true));
        assertThat(AS_OBJECT.serializeAsObject(Enumerable3.class), is(true));
    }

    @Test
    public void testSerializeAsObjectNull() {
        assertThat(AS_STRING.serializeAsObject(null), is(false));
        assertThat(AS_OBJECT.serializeAsObject(null), is(true));
    }

    @Test
    @SuppressWarnings({"unchecked", "deprecation"})
    public void testAsStringExceptBehaviour() {
        SerializationMethod serializationMethod = AS_STRING;
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(false));

        serializationMethod = AS_STRING.except(Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(true));

        serializationMethod = AS_STRING.except(Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(true));

        serializationMethod = AS_STRING.except(Enumerable1.class, Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Numbers.class), is(false));
    }

    @Test
    @SuppressWarnings({"unchecked", "deprecation"})
    public void testAsObjectExceptBehaviour() {
        SerializationMethod serializationMethod = AS_OBJECT;
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(true));

        serializationMethod = AS_OBJECT.except(Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(false));

        serializationMethod = AS_OBJECT.except(Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(true));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(false));

        serializationMethod = AS_OBJECT.except(Enumerable1.class, Enumerable2.class, Enumerable3.class);
        assertThat(serializationMethod.serializeAsObject(Enumerable1.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable2.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Enumerable3.class), is(false));
        assertThat(serializationMethod.serializeAsObject(Numbers.class), is(true));
    }

    @Test
    public void testExceptNull() {
        assertThat(AS_STRING.except((Class<? extends Enumerable>) null), is(sameInstance(AS_STRING)));
    }

    @Test
    public void testHashCode() {
        assertThat(AS_STRING.hashCode(), is(AS_STRING.hashCode()));
        assertThat(AS_STRING.hashCode(), is(not(AS_OBJECT.hashCode()))); // Stronger than hashcode contract
        assertThat(AS_STRING.hashCode(), is(AS_STRING.except((Class<? extends Enumerable>) null).hashCode()));

        assertThat(AS_OBJECT.except(Enumerable2.class).hashCode(), is(AS_OBJECT.except(Enumerable2.class).hashCode()));
    }

    @Test
    public void testEquals() {
        assertThat(AS_STRING, is(equalTo(AS_STRING)));
        assertThat(AS_STRING, is(not(equalTo(AS_STRING.except(Enumerable1.class)))));
        assertThat(AS_STRING, is(not(equalTo(AS_OBJECT))));

        assertThat(AS_OBJECT.except(Enumerable2.class), is(equalTo(AS_OBJECT.except(Enumerable2.class))));
    }

    @Test
    public void testToString() {
        assertThat(AS_STRING, hasToString("As string"));
        assertThat(AS_OBJECT, hasToString("As object"));
        assertThat(AS_STRING.except(Enumerable3.class),
                hasToString("As string, except [SerializationMethodTest$Enumerable3]"));
        assertThat(AS_OBJECT.except(asList(Enumerable1.class, Enumerable3.class)), hasToString(
                "As object, except [SerializationMethodTest$Enumerable1, SerializationMethodTest$Enumerable3]"));
    }

    private static final class Enumerable1 extends Enumerable {
        private Enumerable1(String value) {
            super(value);
        }
    }

    private static final class Enumerable2 extends Enumerable {
        private Enumerable2(String value) {
            super(value);
        }
    }

    private static final class Enumerable3 extends Enumerable {
        private Enumerable3(String value) {
            super(value);
        }
    }

}
