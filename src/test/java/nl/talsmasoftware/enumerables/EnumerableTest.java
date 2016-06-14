/*
 * Copyright (C) 2016 Talsma ICT
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

import nl.talsmasoftware.enumerables.descriptions.DescriptionProvider;
import nl.talsmasoftware.enumerables.descriptions.DescriptionProviderRegistry;
import nl.talsmasoftware.enumerables.descriptions.Descriptions;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Integer.signum;
import static java.util.Locale.ENGLISH;
import static nl.talsmasoftware.enumerables.descriptions.Descriptions.capitalize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@SuppressWarnings({"unused", "deprecation"})
public class EnumerableTest {

    public static final class BigCo extends Enumerable {
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

        private BigCo(String waarde) {
            super(waarde);
        }
    }

    public static final class Fruit extends Enumerable {

        private static final long serialVersionUID = 1L;

        public static final Fruit APPLE = new Fruit(BigCo.APPLE.getValue());
        public static final Fruit ORANGE = new Fruit("Orange");

        private static final Fruit[] VALUES = values(Fruit.class);

        private Fruit(String waarde) {
            super(waarde);
        }
    }

    public static final class Wordpairs extends Enumerable {
        private static final DescriptionProvider provider = new NaiveReverseDescriptionProvider();

        public static final Wordpairs DESSERTS = new Wordpairs("desserts");
        public static final Wordpairs LIVED = new Wordpairs("lived");
        public static final Wordpairs EDIT = new Wordpairs("edit");
        public static final Wordpairs MAPS = new Wordpairs("maps");
        public static final Wordpairs STRAW = new Wordpairs("straw");

        private Wordpairs(String waarde) {
            super(waarde);
        }
    }

    public static final class ReverseDescriptionProvider implements DescriptionProvider {
        public String describe(Enumerable enumerable) {
            String description = Descriptions.defaultProvider().describe(enumerable);
            if (description == null || description.length() == 0) return description;
            StringBuilder reverse = new StringBuilder(description).reverse();
            reverse.setCharAt(reverse.length() - 1, Character.toLowerCase(reverse.charAt(reverse.length() - 1)));
            reverse.setCharAt(0, Character.toUpperCase(reverse.charAt(0)));
            return reverse.toString();
        }
    }

    /**
     * This description provider will trigger a getDescription() on the enumerable value itself!
     * This should be caught appropriately.
     */
    private static final class NaiveReverseDescriptionProvider implements DescriptionProvider {
        public String describe(Enumerable enumerable) {
            String description = enumerable == null ? null : enumerable.getDescription();
            return description == null ? null
                    : capitalize(new StringBuilder(description.toLowerCase(ENGLISH)).reverse().toString());
        }
    }


    public static final class ListWithReverseOmschrijvingProvider extends Enumerable {
        private static final DescriptionProvider provider = new ReverseDescriptionProvider();

        public static final ListWithReverseOmschrijvingProvider FIRST_ELEMENT = new ListWithReverseOmschrijvingProvider("FIRST_ELEMENT");

        private ListWithReverseOmschrijvingProvider(String waarde) {
            super(waarde);
        }
    }

    @Test
    public void testParse_null() {
        assertThat(Enumerable.parse(BigCo.class, null), is(nullValue()));
    }

    @Test
    public void testParse_empty() {
        BigCo bigCoEmpty = Enumerable.parse(BigCo.class, "");
        assertThat(bigCoEmpty, is(notNullValue()));
        assertThat(bigCoEmpty.getValue(), is(equalTo("")));
        assertThat(bigCoEmpty.getDescription(), is(equalTo("")));
        assertThat(bigCoEmpty, hasToString(equalTo("BigCo{value=\"\"}")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNull() {
        new Fruit(null);
    }

    @Test
    public void testParse_withoutEnumerableType() {
        try {
            Enumerable.parse(null, "value");
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), is(equalTo("Enumerable type is null.")));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testParse_abstractType() {
        Enumerable.parse(Enumerable.class, "value");
    }

    @Test
    public void testParse_withExceptionFromFactory() {
        try {
            Enumerable.parse(Enumerable.class, "value", new Callable<Enumerable>() {
                public Enumerable call() throws Exception {
                    throw new Exception("CHECKED EXCEPTION!");
                }

            });
            fail("exception expected");
        } catch (IllegalStateException expected) {
            assertThat(expected, hasToString(containsString(
                    String.format("Could not create new \"%s\" object with value \"value\".", Enumerable.class.getName()))));
            assertThat(expected.getCause().getMessage(), is(equalTo("CHECKED EXCEPTION!")));
        }
    }

    @Test
    public void testParse_withFactory() {
        final String jboss = "JBoss";
        final AtomicBoolean called = new AtomicBoolean(false);
        Callable<BigCo> factory = new Callable<BigCo>() {
            public BigCo call() throws Exception {
                called.set(true);
                return new BigCo(jboss);
            }
        };

        // Parsing of 'constant value'.
        BigCo parsed = Enumerable.parse(BigCo.class, "Microsoft", factory);
        assertThat("Factory may not have been used", called.get(), is(false));
        assertThat(parsed, is(sameInstance(BigCo.MICROSOFT)));

        parsed = Enumerable.parse(BigCo.class, jboss, factory);
        assertThat("Factory must have been used", called.get(), is(true));
        assertThat(parsed.getValue(), is(equalTo(jboss)));
        assertThat(Enumerable.parse(BigCo.class, jboss, factory), is(equalTo(parsed)));
        assertThat(Enumerable.parse(BigCo.class, jboss, factory), is(not(sameInstance(parsed))));
    }

    @Test
    public void testParse_constant_withFactory() {
        final String apple = BigCo.APPLE.getValue();
        final AtomicBoolean called = new AtomicBoolean(false);
        Callable<BigCo> factory = new Callable<BigCo>() {
            public BigCo call() throws Exception {
                called.set(true);
                return new BigCo(apple);
            }
        };

        assertThat(Enumerable.parse(BigCo.class, apple, factory), is(sameInstance(BigCo.APPLE)));
        assertThat("Factory may not have been called", called.get(), is(false));
    }

    @Test
    public void testValues_typeNull() {
        try {
            Enumerable.values(null);
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), is(equalTo("Enumerable type is null.")));
        }
    }

    @Test
    public void testValues_AbstractEnumerableType() {
        assertThat(Enumerable.values(Enumerable.class), is(arrayWithSize(0)));
    }

    @Test
    public void testValues() {
        assertThat(Enumerable.values(BigCo.class), is(arrayWithSize(4)));
        assertThat(Enumerable.values(BigCo.class), is(arrayContaining(
                BigCo.MICROSOFT, BigCo.APPLE, BigCo.IBM, BigCo.ORACLE)));
        assertThat(Enumerable.values(Fruit.class), is(arrayWithSize(2)));
        assertThat(Enumerable.values(Fruit.class), is(arrayContaining(Fruit.APPLE, Fruit.ORANGE)));
    }

    @Test
    public void testPrint() {
        assertThat(Enumerable.print(null), is(nullValue()));
        assertThat(Enumerable.print(BigCo.MICROSOFT), is(equalTo("Microsoft")));
        assertThat(Enumerable.print(new BigCo("JBoss")), is(equalTo("JBoss")));
    }

    @Test
    public void testEquals() {
        assertThat(Fruit.APPLE.equals(Fruit.APPLE), is(true));
        assertThat(Fruit.APPLE.equals(BigCo.APPLE), is(false)); // although both are abstract Enumerable types..
        assertThat(Fruit.APPLE.equals(Fruit.ORANGE), is(false));
        assertThat(Fruit.APPLE.equals(Fruit.APPLE.getValue()), is(false));
    }

    @Test
    public void testHashcode() {
        assertThat(Fruit.APPLE.hashCode(), is(equalTo(Fruit.APPLE.hashCode())));
        assertThat(Fruit.APPLE.hashCode(), is(equalTo(new Fruit(Fruit.APPLE.getValue()).hashCode())));
    }

    @Test
    public void testToString() {
        assertThat(Fruit.APPLE, hasToString("Fruit{name=\"APPLE\", value=\"Apple\"}"));
        assertThat(new Fruit("Apple"), hasToString("Fruit{name=\"APPLE\", value=\"Apple\"}"));
        assertThat(new Fruit("Pineapple"), hasToString("Fruit{value=\"Pineapple\"}"));
    }

    @Test
    public void testGetDescription() {
        assertThat(Fruit.APPLE.getDescription(), is(equalTo("Apple")));
        assertThat(Fruit.ORANGE.getDescription(), is(equalTo("Orange")));
        assertThat(new Fruit(Fruit.APPLE.getValue()).getDescription(), is(equalTo("Apple")));
        assertThat(new BigCo("JBoss").getDescription(), is(equalTo("Jboss")));
        assertThat(new BigCo("SOME_COMPANY").getDescription(), is(equalTo("Some company")));
    }

    @Test
    public void testOrdinal() {
        assertThat(Fruit.APPLE.ordinal(), is(0));
        assertThat(Fruit.ORANGE.ordinal(), is(1));
        assertThat(new Fruit(Fruit.APPLE.getValue()).ordinal(), is(0));
        assertThat(new Fruit("Pineapple").ordinal(), is(Integer.MAX_VALUE));

        BigCo[] bigCo = Enumerable.values(BigCo.class);
        for (int i = 0; i < bigCo.length; i++) {
            assertThat(bigCo[i].ordinal(), is(equalTo(i)));
        }

        Fruit[] fruit = Enumerable.values(Fruit.class);
        for (int i = 0; i < fruit.length; i++) {
            assertThat(fruit[i].ordinal(), is(equalTo(i)));
        }
    }

    @Test
    public void testName() {
        assertThat(Fruit.APPLE.name(), is(equalTo("APPLE")));
        assertThat(Fruit.ORANGE.name(), is(equalTo("ORANGE")));
        assertThat(new Fruit(Fruit.APPLE.getValue()).name(), is(equalTo("APPLE")));
        assertThat(new Fruit("Pineapple").name(), is(nullValue()));
    }

    @Test
    public void testCompareTo() {
        // Clearly an orange is bigger than an apple? ;-)
        assertThat(signum(Fruit.APPLE.compareTo(Fruit.ORANGE)), is(-1));
        assertThat(signum(Fruit.APPLE.compareTo(Fruit.APPLE)), is(0));
        assertThat(signum(Fruit.ORANGE.compareTo(Fruit.ORANGE)), is(0));
        assertThat(signum(Fruit.ORANGE.compareTo(Fruit.APPLE)), is(1));

        // And how about non-constants?
        assertThat(signum(Fruit.APPLE.compareTo(new Fruit("Grapefruit"))), is(-1));
        assertThat(signum(Fruit.ORANGE.compareTo(new Fruit("Grapefruit"))), is(-1));
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("Grapefruit"))), is(0));
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("Pineapple"))), is(-1));
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("pineapple"))), is(-1)); // case insensitive sorteren?
        assertThat(signum(new Fruit("grapefruit").compareTo(new Fruit("Pineapple"))), is(-1));
        assertThat(signum(new Fruit("Grapefruit").compareTo(new Fruit("grapefruit"))), is(-1)); // gelijk; dan wel case sensitive?
        assertThat(signum(new Fruit("Grapefruit").compareTo(Fruit.APPLE)), is(1));
        assertThat(signum(new Fruit("Grapefruit").compareTo(Fruit.ORANGE)), is(1));

        // Different types should not be equal! (..mumble something about apples and oranges)
        assertThat(BigCo.APPLE.compareTo(Fruit.ORANGE), is(not(0)));
    }

    @Test
    public void testSetOf_null() {
        assertThat(Enumerable.setOf((Enumerable[]) null), is(notNullValue()));
        assertThat(Enumerable.setOf((Enumerable[]) null), hasSize(0));
    }

    @Test
    public void testSetOf_empty() {
        assertThat(Enumerable.setOf(), is(notNullValue()));
        assertThat(Enumerable.setOf(), hasSize(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetOf_singleValue() {
        final Set<Fruit> singleValue = Enumerable.setOf(Fruit.APPLE);
        assertThat(singleValue, hasSize(1));
        assertThat(singleValue, contains(Fruit.APPLE));
        singleValue.add(Fruit.ORANGE);
        fail("setOf(APPLE) should be unmodifiable.");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetOf_multipleValues() {
        final Set<Fruit> multipleValues = Enumerable.setOf(Fruit.ORANGE, Fruit.APPLE);
        assertThat(multipleValues, hasSize(2));
        assertThat(multipleValues, contains(Fruit.ORANGE, Fruit.APPLE));
        multipleValues.add(Enumerable.parse(Fruit.class, "Grapefruit"));
        fail("setOf(ORANGE, APPLE) should be unmodifiable.");
    }

    @Test
    public void testValueOf_typeNull() {
        try {
            Enumerable.valueOf(null, "someName");
            fail("Exception expected.");
        } catch (RuntimeException expected) {
            assertThat(expected.getMessage(), is(equalTo("Enumerable type is null.")));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOf_nameNull() {
        Enumerable.valueOf(Fruit.class, null);
    }

    @Test
    public void testValueOf_allValues() {
        for (BigCo bigCo : Enumerable.values(BigCo.class)) {
            assertThat(bigCo, is(sameInstance(Enumerable.valueOf(BigCo.class, bigCo.name()))));
        }
        for (Fruit fruit : Enumerable.values(Fruit.class)) {
            assertThat(fruit, is(sameInstance(Enumerable.valueOf(Fruit.class, fruit.name()))));
        }
    }

    @Test
    public void testValueOf_nonConstantValue() {
        // First parse a grapefruit so it has been encountered before for this test...
        Fruit grapefruit = Enumerable.parse(Fruit.class, "Grapefruit");
        assertThat(grapefruit.getValue(), is(equalTo("Grapefruit")));
        assertThat(grapefruit.name(), is(nullValue())); // Non-constant value.
        try {
            Enumerable.valueOf(Fruit.class, "Grapefruit");
            fail("Exception expected due to non-constant value.");
        } catch (Enumerable.ConstantNotFoundException expected) {
            assertThat(expected.getMessage(), is(equalTo("No Enumerable constant \"Fruit.Grapefruit\" found.")));
        }
    }

    @Test
    public void testDescription_byProvider() {
        final DescriptionProviderRegistry registry = DescriptionProviderRegistry.getInstance();
        final DescriptionProvider previousProvider = registry.getDescriptionProviderFor(BigCo.class);
        try {

            registry.registerDescriptionProviderFor(BigCo.class, new DescriptionProvider() {
                public String describe(Enumerable enumerable) {
                    return enumerable.getClass().getSimpleName() + "." + enumerable.name();
                }
            });
            assertThat(BigCo.APPLE.getDescription(), is(equalTo("BigCo.APPLE")));

        } finally {
            registry.registerDescriptionProviderFor(BigCo.class, previousProvider);
        }
    }

    @Test
    public void testDescription_exceptionObtainingProvider() {
        assertThat(new Enumerable("tst") {
            @Override
            protected DescriptionProvider descriptionProvider() {
                throw new IllegalStateException("Error getting provider!");
            }
        }.getDescription(), is(equalTo("Tst")));
    }

    @Test
    public void testDescription_providerException() {
        assertThat(new Enumerable("apple") {
            @Override
            protected DescriptionProvider descriptionProvider() {
                return new DescriptionProvider() {
                    public String describe(Enumerable enumerable) {
                        throw new UnsupportedOperationException("Not allowed!");
                    }
                };
            }
        }.getDescription(), is(equalTo("Apple")));
    }

    @Test
    public void testDescription_selfConfiguringProvider() {
        assertThat(ListWithReverseOmschrijvingProvider.FIRST_ELEMENT.getDescription(),
                is(equalTo("Tnemele tsrif")));
    }

    @Test
    public void testDescription_descriptionProviderRecursionFix() {
        assertThat(Wordpairs.DESSERTS.getDescription(), is(equalTo("Stressed")));
        assertThat(Wordpairs.LIVED.getDescription(), is(equalTo("Devil")));
        assertThat(Wordpairs.EDIT.getDescription(), is(equalTo("Tide")));
        assertThat(Wordpairs.MAPS.getDescription(), is(equalTo("Spam")));
        assertThat(Wordpairs.STRAW.getDescription(), is(equalTo("Warts")));
    }

}
