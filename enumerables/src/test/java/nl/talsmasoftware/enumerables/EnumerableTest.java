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
package nl.talsmasoftware.enumerables;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Integer.signum;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"unused"})
class EnumerableTest {

    static final class BigCo extends Enumerable {
        private static final long serialVersionUID = 1L;

        public static final BigCo MICROSOFT = new BigCo("Microsoft");
        public static final BigCo APPLE = new BigCo("Apple");
        public static final BigCo IBM = new BigCo("IBM");
        public static final BigCo ORACLE = new BigCo("Oracle");

        // These should NOT be included in values(),
        // because non-public, non-final or non-static,
        // of ander type, so no 'enumerable constants'.
        private static final BigCo NOT_PUBLIC = new BigCo("Not public");
        public static BigCo notFinal = new BigCo("Not final");
        public final BigCo nietStatic = null;
        public static final Enumerable TOO_ABSTRACT = new BigCo("Declaration too abstract");

        private static final BigCo[] VALUES = values(BigCo.class);

        private BigCo(String value) {
            super(value);
        }
    }

    static final class Fruit extends Enumerable {

        private static final long serialVersionUID = 1L;

        public static final Fruit APPLE = new Fruit(BigCo.APPLE.getValue());
        public static final Fruit ORANGE = new Fruit("Orange");

        private static final Fruit[] VALUES = values(Fruit.class);

        private Fruit(String value) {
            super(value);
        }
    }

    static final class Wordpairs extends Enumerable {
        public static final Wordpairs DESSERTS = new Wordpairs("desserts");
        public static final Wordpairs LIVED = new Wordpairs("lived");
        public static final Wordpairs EDIT = new Wordpairs("edit");
        public static final Wordpairs MAPS = new Wordpairs("maps");
        public static final Wordpairs STRAW = new Wordpairs("straw");

        private Wordpairs(String value) {
            super(value);
        }
    }

    @Test
    void testParse_null() {
        assertThat(Enumerable.parse(BigCo.class, null)).isNull();
    }

    @Test
    void testParse_empty() {
        BigCo bigCoEmpty = Enumerable.parse(BigCo.class, "");
        assertThat(bigCoEmpty).isNotNull();
        assertThat(bigCoEmpty.getValue()).isEmpty();
        assertThat(bigCoEmpty).hasToString("BigCo{value=}");
    }

    @Test
    void testConstructorNull() {
        assertThatThrownBy(() -> new Fruit(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value of type Fruit was <null>");
    }

    @Test
    void testParse_withoutEnumerableType() {
        assertThatThrownBy(() -> Enumerable.parse(null, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Enumerable type is <null>");
    }

    @Test
    void testParse_abstractType() {
        assertThatThrownBy(() -> Enumerable.parse(Enumerable.class, "value"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testParse_withExceptionFromFactory() {
        Callable<Enumerable> factory = () -> {
            throw new Exception("CHECKED EXCEPTION!");
        };
        assertThatThrownBy(() -> Enumerable.parse(Enumerable.class, "value", factory))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not create new \"nl.talsmasoftware.enumerables.Enumerable\" object with value \"value\".")
                .cause()
                .hasMessage("CHECKED EXCEPTION!");
    }

    @Test
    void testParse_withFactory() {
        final String jboss = "JBoss";
        final AtomicBoolean called = new AtomicBoolean(false);
        Callable<BigCo> factory = () -> {
            called.set(true);
            return new BigCo(jboss);
        };

        // Parsing of 'constant value'.
        BigCo parsed = Enumerable.parse(BigCo.class, "Microsoft", factory);
        assertThat(called.get()).as("Factory called").isFalse();
        assertThat(parsed).isSameAs(BigCo.MICROSOFT);

        parsed = Enumerable.parse(BigCo.class, jboss, factory);
        assertThat(called.get()).as("Factory called").isTrue();
        assertThat(parsed.getValue()).isEqualTo(jboss);
        assertThat(Enumerable.parse(BigCo.class, jboss, factory)).isEqualTo(parsed);
        assertThat(Enumerable.parse(BigCo.class, jboss, factory)).isNotSameAs(parsed);
    }

    @Test
    void testParse_constant_withFactory() {
        final String apple = BigCo.APPLE.getValue();
        final AtomicBoolean called = new AtomicBoolean(false);
        Callable<BigCo> factory = () -> {
            called.set(true);
            return new BigCo(apple);
        };

        assertThat(Enumerable.parse(BigCo.class, apple, factory)).isSameAs(BigCo.APPLE);
        assertThat(called.get()).as("Factory called").isFalse();
    }

    static final class EnumerableWithoutStringConstructor extends Enumerable {
        EnumerableWithoutStringConstructor() {
            super(null);
        }
    }

    @Test
    void testParse_classWithoutStringConstructor() {
        assertThatThrownBy(() -> Enumerable.parse(EnumerableWithoutStringConstructor.class, "Dummy value"))
                .hasMessageContaining(String.format("Could not create new \"%s\" object", EnumerableWithoutStringConstructor.class.getName()))
                .hasMessageContaining("value \"Dummy value\"");
    }

    @Test
    void testValues_typeNull() {
        assertThatThrownBy(() -> Enumerable.values(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Enumerable type is <null>");
    }

    @Test
    void testValues_AbstractEnumerableType() {
        assertThat(Enumerable.values(Enumerable.class)).isEmpty();
    }

    @Test
    void testValues() {
        assertThat(Enumerable.values(BigCo.class))
                .hasSize(4)
                .contains(BigCo.MICROSOFT, BigCo.APPLE, BigCo.IBM, BigCo.ORACLE);
        assertThat(Enumerable.values(Fruit.class))
                .hasSize(2)
                .contains(Fruit.APPLE, Fruit.ORANGE);
    }

    @Test
    void testPrint() {
        assertThat(Enumerable.print(null)).isNull();
        assertThat(Enumerable.print(BigCo.MICROSOFT)).isEqualTo("Microsoft");
        assertThat(Enumerable.print(new BigCo("JBoss"))).isEqualTo("JBoss");
    }

    @Test
    void testEquals() {
        assertThat(Fruit.APPLE.equals(Fruit.APPLE)).isTrue();
        assertThat(Fruit.APPLE.equals(BigCo.APPLE)).isFalse(); // although both are abstract Enumerable types..
        assertThat(Fruit.APPLE.equals(Fruit.ORANGE)).isFalse();
        assertThat(Fruit.APPLE.equals(Fruit.APPLE.getValue())).isFalse();
    }

    @Test
    void testHashcode() {
        assertThat(Fruit.APPLE.hashCode()).hasSameHashCodeAs(Fruit.APPLE);
        assertThat(Fruit.APPLE.hashCode()).hasSameHashCodeAs(new Fruit(Fruit.APPLE.getValue()));
    }

    @Test
    void testToString() {
        assertThat(Fruit.APPLE).hasToString("Fruit{name=APPLE, value=Apple}");
        assertThat(new Fruit("Apple")).hasToString("Fruit{name=APPLE, value=Apple}");
        assertThat(new Fruit("Pineapple")).hasToString("Fruit{value=Pineapple}");
    }

    @Test
    void testOrdinal() {
        assertThat(Fruit.APPLE.ordinal()).isZero();
        assertThat(Fruit.ORANGE.ordinal()).isEqualTo(1);
        assertThat(new Fruit(Fruit.APPLE.getValue()).ordinal()).isEqualTo(0);
        assertThat(new Fruit("Pineapple").ordinal()).isEqualTo(Integer.MAX_VALUE);

        BigCo[] bigCo = Enumerable.values(BigCo.class);
        for (int i = 0; i < bigCo.length; i++) {
            assertThat(bigCo[i].ordinal()).isEqualTo(i);
        }

        Fruit[] fruit = Enumerable.values(Fruit.class);
        for (int i = 0; i < fruit.length; i++) {
            assertThat(fruit[i].ordinal()).isEqualTo(i);
        }
    }

    @Test
    void testName() {
        assertThat(Fruit.APPLE.name()).isEqualTo("APPLE");
        assertThat(Fruit.ORANGE.name()).isEqualTo("ORANGE");
        assertThat(new Fruit(Fruit.APPLE.getValue()).name()).isEqualTo("APPLE");
        assertThat(new Fruit("Pineapple").name()).isNull();
    }

    @Test
    void testCompareTo_null() {
        assertThatThrownBy(() -> Fruit.APPLE.compareTo(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Cannot compare with enumerable <null>");
    }

    @Test
    void testCompareTo() {
        // An orange is bigger than an apple? ;-)
        assertThat(signum(Fruit.APPLE.compareTo(Fruit.ORANGE))).isEqualTo(-1);
        assertThat(signum(Fruit.APPLE.compareTo(Fruit.APPLE))).isEqualTo(0);
        assertThat(signum(Fruit.ORANGE.compareTo(Fruit.ORANGE))).isEqualTo(0);
        assertThat(signum(Fruit.ORANGE.compareTo(Fruit.APPLE))).isEqualTo(1);

        // And how about non-constants?
        assertThat(signum(Fruit.APPLE.compareTo(new Fruit("Grapefruit")))).isEqualTo(-1);
        assertThat(signum(Fruit.ORANGE.compareTo(new Fruit("Grapefruit")))).isEqualTo(-1);
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("Grapefruit")))).isZero();
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("Pineapple")))).isEqualTo(-1);
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("pineapple")))).isEqualTo(-1); // case-insensitive sorting?
        assertThat(signum(new Fruit("grapefruit").compareTo(new Fruit("Pineapple")))).isEqualTo(-1);
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("grapefruit")))).isEqualTo(-1); // equal; then case-sensitive?
        assertThat(signum(new Fruit("Grapefruit").compareTo(Fruit.APPLE))).isEqualTo(1);
        assertThat(signum(new Fruit("Grapefruit").compareTo(Fruit.ORANGE))).isEqualTo(1);

        // Different types should not be equal! (..mumble something about apples and oranges)
        assertThat(BigCo.APPLE.compareTo(Fruit.ORANGE)).isNotZero();
        assertThat(BigCo.APPLE.compareTo(Fruit.APPLE)).isNotZero();
    }

    @Test
    void testCompareTo_constantsBeforeParsedValues() {
        Fruit nonConstant = Enumerable.parse(Fruit.class, "Kiwano");
        assertThat(nonConstant.ordinal()).isEqualTo(Integer.MAX_VALUE);
        for (Fruit fruit : Fruit.VALUES) {
            assertThat(signum(fruit.compareTo(nonConstant))).isEqualTo(-1);
        }
    }

    @Test
    void testSetOf_null() {
        assertThat(Enumerable.setOf((Enumerable[]) null)).isNotNull().isEmpty();
    }

    @Test
    void testSetOf_empty() {
        assertThat(Enumerable.setOf()).isNotNull().isEmpty();
    }

    @Test
    void testSetOf_singleValue() {
        final Set<Fruit> singleValue = Enumerable.setOf(Fruit.APPLE);
        assertThat(singleValue).hasSize(1).contains(Fruit.APPLE);
        assertThatThrownBy(() -> singleValue.add(Fruit.ORANGE))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testSetOf_multipleValues() {
        final Set<Fruit> multipleValues = Enumerable.setOf(Fruit.ORANGE, Fruit.APPLE);
        assertThat(multipleValues).hasSize(2).contains(Fruit.ORANGE, Fruit.APPLE);
        Fruit grapefruit = Enumerable.parse(Fruit.class, "Grapefruit");
        assertThatThrownBy(() -> multipleValues.add(grapefruit))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testValueOf_typeNull() {
        assertThatThrownBy(() -> Enumerable.valueOf(null, "someName"))
                .hasMessageContaining("Enumerable type is <null>");
    }

    @Test
    void testValueOf_nameNull() {
        assertThatThrownBy(() -> Enumerable.valueOf(Fruit.class, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testValueOf_allValues() {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            assertThat(bigCo).isSameAs(Enumerable.valueOf(BigCo.class, bigCo.name()));
        }
        for (Fruit fruit : Enumerable.values(Fruit.class)) {
            assertThat(fruit).isSameAs(Enumerable.valueOf(Fruit.class, fruit.name()));
        }
    }

    @Test
    void testValueOf_nonConstantValue() {
        // First parse a grapefruit so it has been encountered before for this test...
        Fruit grapefruit = Enumerable.parse(Fruit.class, "Grapefruit");
        assertThat(grapefruit.getValue()).isEqualTo("Grapefruit");
        assertThat(grapefruit.name()).isNull(); // Non-constant value.
        assertThatThrownBy(() -> Enumerable.valueOf(Fruit.class, "Grapefruit"))
                .isInstanceOf(Enumerable.ConstantNotFoundException.class)
                .hasMessageContaining("No Enumerable constant \"Fruit.Grapefruit\" found.");
    }

    @Test
    void testDeserialize_nonConstantValue() {
        Fruit grapefruit = Enumerable.parse(Fruit.class, "Grapefruit");
        Fruit deserialized = deserialize(serialize(grapefruit));

        assertThat(deserialized).isEqualTo(grapefruit);
    }

    @Test
    void testDeserialize_sameConstantInstance() {
        Fruit deserialized = deserialize(serialize(Fruit.ORANGE));

        assertThat(deserialized).isEqualTo(Fruit.ORANGE).isSameAs(Fruit.ORANGE);
    }

    static byte[] serialize(Serializable value) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            new ObjectOutputStream(output).writeObject(value);
            return output.toByteArray();
        } catch (IOException ioe) {
            throw new IllegalStateException("Unexpected serialization I/O exception: " + ioe.getMessage(), ioe);
        }
    }

    @SuppressWarnings("unchecked")
    static <S extends Serializable> S deserialize(byte[] bytes) {
        try {
            return (S) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (IOException ioe) {
            throw new IllegalStateException("Unexpected deserialization I/O exception: " + ioe.getMessage(), ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Deserialized class missing: " + cnfe.getMessage(), cnfe);
        }
    }

}
