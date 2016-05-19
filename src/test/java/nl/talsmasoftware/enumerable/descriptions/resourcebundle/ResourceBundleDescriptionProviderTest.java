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
package nl.talsmasoftware.enumerable.descriptions.resourcebundle;

import nl.talsmasoftware.enumerable.CarBrand;
import nl.talsmasoftware.enumerable.Enumerable;
import nl.talsmasoftware.enumerable.descriptions.DescriptionProvider;
import nl.talsmasoftware.enumerable.descriptions.DescriptionProviderRegistry;
import org.junit.*;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class ResourceBundleDescriptionProviderTest {
    public static final Locale NL = new Locale("nl");

    public static final class RbBigCo extends Enumerable {
        private static final DescriptionProvider DESCRIPTIONS = ResourceBundleDescriptionProvider.DEFAULT;

        public static final RbBigCo MICROSOFT = new RbBigCo("Microsoft");
        public static final RbBigCo APPLE = new RbBigCo("Apple");
        public static final RbBigCo IBM = new RbBigCo("IBM");
        public static final RbBigCo ORACLE = new RbBigCo("Oracle");

        private RbBigCo(String value) {
            super(value);
        }
    }

    public static final class RbBigCoNl extends Enumerable {
        private static final DescriptionProvider DESCRIPTIONS = ResourceBundleDescriptionProvider.builder()
                .locale(new Locale("nl")).bundleName(RbBigCo.class.getName()).build();

        public static final RbBigCoNl MICROSOFT = new RbBigCoNl("Microsoft");
        public static final RbBigCoNl APPLE = new RbBigCoNl("Apple");
        public static final RbBigCoNl IBM = new RbBigCoNl("IBM");
        public static final RbBigCoNl ORACLE = new RbBigCoNl("Oracle");

        private RbBigCoNl(String value) {
            super(value);
        }
    }

    private static DescriptionProvider oldDescriptionProvider;
    private Locale oldDefaultLocale;

    @BeforeClass
    public static void rememberOldDescriptionProvider() {
        oldDescriptionProvider = DescriptionProviderRegistry.getInstance().getDescriptionProviderFor(RbBigCo.class);
    }

    @Before
    public void setUp() {
        oldDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @After
    public void tearDown() {
        Locale.setDefault(oldDefaultLocale);
    }

    @AfterClass
    public static void deregistreerDescriptionProvider() {
        DescriptionProviderRegistry.getInstance().registerDescriptionProviderFor(RbBigCo.class, oldDescriptionProvider);
    }

    @Test
    public void testDescription_withoutValue() {
        assertThat(RbBigCo.MICROSOFT.getDescription(), is(equalTo("Microsoft")));
    }

    @Test
    public void testDescription_defaultLocale() {
        assertThat(RbBigCo.IBM.getDescription(), is(equalTo("International Business Machines")));
        // And an enumerable with registered provider in a specific Locale.
        assertThat(RbBigCoNl.IBM.getDescription(), is(equalTo("Internationale Bedrijfs Machines")));
    }

    @Test
    public void testDescription_nlLocale() {
        assertThat(RbBigCo.IBM.getDescription(), is(equalTo("International Business Machines")));
        assertThat(RbBigCoNl.IBM.getDescription(), is(equalTo("Internationale Bedrijfs Machines")));

        // With the default locale set, both classes should get Dutch descriptions.
        Locale.setDefault(NL);
        assertThat(RbBigCo.IBM.getDescription(), is(equalTo("Internationale Bedrijfs Machines")));
        assertThat(RbBigCo.IBM.getDescription(), is(equalTo("Internationale Bedrijfs Machines")));
    }

    @Test
    public void testMissingDescription() {
        assertThat(ResourceBundleDescriptionProvider.builder().build().describe(CarBrand.AUDI),
                is(nullValue()));
        assertThat(ResourceBundleDescriptionProvider.builder().locale(Locale.GERMAN).build().describe(CarBrand.AUDI),
                is(nullValue()));
    }

    @Test
    public void testMissingDescription_inSpecificLocale() {
        assertThat(ResourceBundleDescriptionProvider.builder().build().describe(CarBrand.TESLA),
                is(equalTo("Tesla Motors")));
        assertThat(ResourceBundleDescriptionProvider.builder().locale(Locale.GERMAN).build().describe(CarBrand.TESLA),
                is(equalTo("Tesla Motors")));
        Locale.setDefault(Locale.FRENCH);
        assertThat(ResourceBundleDescriptionProvider.builder().locale(Locale.GERMAN).build().describe(CarBrand.TESLA),
                is(equalTo("Tesla Motors")));
    }

    @Test
    public void testDescription_inDifferentLocales() {
        assertThat(ResourceBundleDescriptionProvider.builder().build().describe(CarBrand.BMW),
                is(equalTo("Bavarian Motor Works")));
        assertThat(ResourceBundleDescriptionProvider.builder().locale(Locale.GERMAN).build().describe(CarBrand.BMW),
                is(equalTo("Bayerische Motoren Werke")));
        // No NL definition for BMW and default locale is English:
        assertThat(ResourceBundleDescriptionProvider.builder().locale(NL).build().describe(CarBrand.BMW),
                is(equalTo("Bavarian Motor Works")));
        // No NL definition for BMW and default locale is French:
        Locale.setDefault(Locale.FRENCH);
        assertThat(ResourceBundleDescriptionProvider.builder().locale(NL).build().describe(CarBrand.BMW),
                is(equalTo("BMW")));
    }
}
