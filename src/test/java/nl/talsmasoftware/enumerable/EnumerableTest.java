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
package nl.talsmasoftware.enumerable;

import nl.talsmasoftware.enumerable.descriptions.DescriptionProvider;
import nl.talsmasoftware.enumerable.descriptions.Descriptions;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Locale.ENGLISH;
import static nl.talsmasoftware.enumerable.descriptions.Descriptions.capitalize;
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
                    "Could not create new \"nl.talsmasoftware.enumerable.Enumerable\" object with value \"value\".")));
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

//    @Test
//    public void testEquals() {
//        assertTrue(Fruit.APPLE.equals(Fruit.APPLE));
//        assertFalse(Fruit.APPLE.equals(BigCo.APPLE)); // Wel beide abstract Waardenlijst type..
//        assertFalse(Fruit.APPLE.equals(Fruit.ORANGE));
//        assertTrue(Fruit.APPLE.equals(new Fruit(Fruit.APPLE.getWaarde())));
//        assertFalse(Fruit.APPLE.equals(Fruit.APPLE.getWaarde()));
//    }
//
//    @Test
//    public void testHashcode() {
//        assertEquals(Fruit.APPLE.hashCode(), Fruit.APPLE.hashCode());
//        assertEquals(Fruit.APPLE.hashCode(), new Fruit(Fruit.APPLE.getWaarde()).hashCode());
//    }
//
//    @Test
//    public void testToString() {
//        assertEquals("Fruit{name=\"APPLE\", waarde=\"Apple\"}", Fruit.APPLE.toString());
//        assertEquals("Fruit{name=\"APPLE\", waarde=\"Apple\"}", new Fruit(Fruit.APPLE.getWaarde()).toString());
//        assertEquals("Fruit{waarde=\"Appel\"}", new Fruit("Appel").toString());
//    }
//
//    @Test
//    public void testGetOmschrijving() {
//        assertEquals("Apple", Fruit.APPLE.getOmschrijving());
//        assertEquals("Orange", Fruit.ORANGE.getOmschrijving());
//        assertEquals("Apple", new Fruit(Fruit.APPLE.getWaarde()).getOmschrijving());
//        assertEquals("Jboss", new BigCo("JBoss").getOmschrijving());
//        assertEquals("Some company", new BigCo("SOME_COMPANY").getOmschrijving());
//    }
//
//    @Test
//    public void testOrdinal() {
//        assertEquals(0, Fruit.APPLE.ordinal());
//        assertEquals(1, Fruit.ORANGE.ordinal());
//        assertEquals(0, new Fruit(Fruit.APPLE.getWaarde()).ordinal());
//        assertEquals(Integer.MAX_VALUE, new Fruit("Appel").ordinal());
//
//        BigCo[] bigCo = Waardenlijst.values(BigCo.class);
//        for (int i = 0; i < bigCo.length; i++) {
//            assertEquals(i, bigCo[i].ordinal());
//        }
//
//        Fruit[] fruit = Waardenlijst.values(Fruit.class);
//        for (int i = 0; i < fruit.length; i++) {
//            assertEquals(i, fruit[i].ordinal());
//        }
//    }
//
//    @Test
//    public void testName() {
//        assertEquals("APPLE", Fruit.APPLE.name());
//        assertEquals("ORANGE", Fruit.ORANGE.name());
//        assertEquals("APPLE", new Fruit(Fruit.APPLE.getWaarde()).name());
//        assertEquals(null, new Fruit("Appel").name());
//    }
//
//    @Test
//    public void testCompareTo() {
//        // Clearly an orange is bigger than an apple? ;-)
//        assertEquals(-1, Integer.signum(Fruit.APPLE.compareTo(Fruit.ORANGE)));
//        assertEquals(0, Integer.signum(Fruit.APPLE.compareTo(Fruit.APPLE)));
//        assertEquals(0, Integer.signum(Fruit.ORANGE.compareTo(Fruit.ORANGE)));
//        assertEquals(1, Integer.signum(Fruit.ORANGE.compareTo(Fruit.APPLE)));
//
//        // En hoe met niet-constanten?
//        assertEquals(-1, Integer.signum(Fruit.APPLE.compareTo(new Fruit("Grapefruit"))));
//        assertEquals(-1, Integer.signum(Fruit.ORANGE.compareTo(new Fruit("Grapefruit"))));
//        assertEquals(0, Integer.signum(new Fruit("Grapefruit").compareTo(new Fruit("Grapefruit"))));
//        assertEquals(-1, Integer.signum(new Fruit("Grapefruit").compareTo(new Fruit("Pineapple"))));
//        assertEquals(-1, Integer.signum(new Fruit("Grapefruit").compareTo(new Fruit("pineapple")))); // case insensitive sorteren?
//        assertEquals(-1, Integer.signum(new Fruit("grapefruit").compareTo(new Fruit("Pineapple"))));
//        assertEquals(-1, Integer.signum(new Fruit("Grapefruit").compareTo(new Fruit("grapefruit")))); // gelijk; dan wel case sensitive?
//        assertEquals(1, Integer.signum(new Fruit("Grapefruit").compareTo(Fruit.APPLE)));
//        assertEquals(1, Integer.signum(new Fruit("Grapefruit").compareTo(Fruit.ORANGE)));
//
//        // Verschillende types zijn hopelijk niet gelijk? (iets met apples en oranges)
//        assertEquals(BigCo.APPLE.ordinal(), Fruit.ORANGE.ordinal());
//        assertTrue(0 != BigCo.APPLE.compareTo(Fruit.ORANGE));
//    }
//
//    @Test
//    public void testSetOf_null() {
//        assertSame(Collections.EMPTY_SET, Waardenlijst.setOf((Waardenlijst[]) null));
//    }
//
//    @Test
//    public void testSetOf_empty() {
//        assertSame(Collections.EMPTY_SET, Waardenlijst.setOf());
//    }
//
//    @Test
//    public void testSetOf_singleValue() {
//        Set<Fruit> set = Waardenlijst.setOf(Fruit.APPLE);
//        assertEquals(Collections.singleton(Fruit.APPLE), set);
//        try {
//            set.add(Fruit.ORANGE);
//            fail("setOf(APPLE) zou unmodifiable moeten zijn.");
//        } catch (UnsupportedOperationException expected) {
//        }
//    }
//
//    @Test
//    public void testSetOf_multipleValues() {
//        Set<Fruit> set = Waardenlijst.setOf(Fruit.ORANGE, Fruit.APPLE);
//        assertEquals(new HashSet<Fruit>(asList(Fruit.VALUES)), set);
//        try {
//            set.add(Waardenlijst.parse(Fruit.class, "Grapefruit"));
//            fail("setOf(ORANGE, APPLE) zou unmodifiable moeten zijn.");
//        } catch (UnsupportedOperationException expected) {
//        }
//    }
//
//    @Test
//    public void testValueOf_typeNull() {
//        try {
//            Waardenlijst.valueOf(null, "someName");
//            fail("Foutmelding verwacht.");
//        } catch (NullPointerException expected) {
//            assertFoutmelding(expected);
//        }
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testValueOf_nameNull() {
//        Waardenlijst.valueOf(Fruit.class, null);
//    }
//
//    @Test
//    public void testValueOf_alleValues() {
//        for (BigCo bigCo : Waardenlijst.values(BigCo.class)) {
//            assertSame(bigCo, Waardenlijst.valueOf(BigCo.class, bigCo.name()));
//        }
//        for (Fruit fruit : Waardenlijst.values(Fruit.class)) {
//            assertSame(fruit, Waardenlijst.valueOf(Fruit.class, fruit.name()));
//        }
//    }
//
//    @Test
//    public void testValueOf_nietConstanteWaarde() {
//        Fruit grapefruit = Waardenlijst.parse(Fruit.class, "Grapefruit"); // ..Zodat deze al eens is 'langsgekomen' voor de test
//        assertEquals("Grapefruit", grapefruit.getWaarde());
//        assertNull(grapefruit.name()); // Niet-constante waarde.
//        try {
//            Waardenlijst.valueOf(Fruit.class, "Grapefruit");
//            fail("Exceptie verwacht wegens niet-constante waarde.");
//        } catch (IllegalArgumentException expected) {
//            assertEquals("Geen waardenlijst constante 'Fruit.Grapefruit'.", expected.getMessage());
//        }
//    }
//
//    @Test
//    public void testOmschrijving_viaProvider() {
//        try {
//            OmschrijvingProviderRegistry.getInstance().registreerOmschrijvingProvider(BigCo.class, new OmschrijvingProvider() {
//                @Override
//                public String omschrijvingVoor(Waardenlijst waarde) {
//                    return waarde.getClass().getSimpleName() + "." + waarde.name();
//                }
//            });
//            assertThat(BigCo.APPLE.getOmschrijving(), equalTo("BigCo.APPLE"));
//        } finally {
//            OmschrijvingProviderRegistry.getInstance().registreerOmschrijvingProvider(BigCo.class, null);
//        }
//    }
//
//    @Test
//    public void testOmschrijving_foutBijOphalenProvider() {
//        String omschrijving = new Waardenlijst("tst") {
//            @Override
//            protected OmschrijvingProvider omschrijvingProvider() {
//                throw new IllegalStateException("Error getting provider!");
//            }
//        }.getOmschrijving();
//    }
//
//    @Test
//    public void testOmschrijving_providerFout() {
//        try {
//            OmschrijvingProviderRegistry.getInstance().registreerOmschrijvingProvider(BigCo.class, new OmschrijvingProvider() {
//                @Override
//                public String omschrijvingVoor(Waardenlijst waarde) {
//                    throw new UnsupportedOperationException("Not allowed!");
//                }
//            });
//            assertThat(BigCo.APPLE.getOmschrijving(), equalTo("Apple"));
//        } finally {
//            OmschrijvingProviderRegistry.getInstance().registreerOmschrijvingProvider(BigCo.class, null);
//        }
//    }
//
//    @Test
//    public void testOmschrijving_zelfconfigurerendeProvider() {
//        assertThat(LijstMetReverseOmschrijvingProvider.EERSTE_ELEMENT.getOmschrijving(), equalTo("tnemele etsreE"));
//    }
//
//    @Test
//    public void testOmschrijving_omschrijvingProviderRecursieFix() {
//        assertThat(Woordparen.DOOR.getOmschrijving(), equalTo("rood"));
//        assertThat(Woordparen.DROOM.getOmschrijving(), equalTo("moord"));
//        assertThat(Woordparen.LEVEN.getOmschrijving(), equalTo("nevel"));
//        assertThat(Woordparen.ROOK.getOmschrijving(), equalTo("koor"));
//    }

}
