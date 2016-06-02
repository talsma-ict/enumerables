/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nl.talsmasoftware.reflection.beans;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReflectionSupportTest {

    @Before
    @After
    public void clearCaches() {
        ReflectionSupport.flushCaches();
    }

    @Test
    public void testUnsupportedConstructor() {
        TestUtil.assertUnsupportedConstructor(ReflectionSupport.class);
    }

    @Test
    public void testGetProperty_nulls() {
        assertThat(ReflectionSupport.getPropertyValue(null, null), is(nullValue()));
        assertThat(ReflectionSupport.getPropertyValue(null, "property"), is(nullValue()));
        assertThat(ReflectionSupport.getPropertyValue(new Object(), null), is(nullValue()));
        assertThat(ReflectionSupport.getPropertyValue(new Object(), ""), is(nullValue()));
    }

    @Test
    public void testGetProperty_nietGevonden() {
        assertThat(ReflectionSupport.getPropertyValue(new Object(), "nietGevonden"), is(nullValue()));
    }

    @Test
    public void testGetter() {
        assertThat(ReflectionSupport.getPropertyValue(new BeanMetGetter("a"), "value"), is(equalTo((Object) "a")));
    }

    @Test
    public void testBooleanProperty() {
        assertThat(ReflectionSupport.getPropertyValue(new BeanMetBooleanProperty(true), "indicatie"), is(equalTo((Object) true)));
    }

    @Test
    public void testAccessorsEnVelden() {
        BeanMetAccessorsEnVelden bean = new BeanMetAccessorsEnVelden("a", true);
        assertThat(ReflectionSupport.getPropertyValue(bean, "waarde"), is(equalTo((Object) "a")));
        assertThat(ReflectionSupport.getPropertyValue(bean, "indicatie"), is(equalTo((Object) true)));
        assertThat(ReflectionSupport.getPropertyValue(bean, "waarde2"), is(equalTo((Object) "a")));
        assertThat(ReflectionSupport.getPropertyValue(bean, "indicatie2"), is(equalTo((Object) true)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetProperties_mutability() {
        Iterator<ReflectedProperty> it = ReflectionSupport.getProperties(BeanMetAccessorsEnVelden.class).iterator();
        it.next();
        it.remove();
    }

    @Test
    public void testGetPropertyValues_null() {
        Map<String, Object> expected = Collections.emptyMap();
        assertThat(ReflectionSupport.getPropertyValues(null), is(equalTo(expected)));
    }

    @Test
    public void testGetPropertyValues() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("class", Object.class);
        assertThat("Properties of new Object()", ReflectionSupport.getPropertyValues(new Object()), is(equalTo(expected)));

        assertThat(ReflectionSupport.getPropertyValues("String waarde").entrySet(), hasSize(3));
        assertThat(ReflectionSupport.getPropertyValues("String waarde").get("class"), is(equalTo((Object) String.class)));
        assertThat(ReflectionSupport.getPropertyValues("String waarde").get("empty"), is(equalTo((Object) Boolean.FALSE)));
        assertThat(ReflectionSupport.getPropertyValues("String waarde").get("bytes"), is(instanceOf(byte[].class)));

        expected.clear();
        expected.put("class", BeanMetGetter.class);
        expected.put("value", "val");
        assertThat("Properties of BeanMetGetter",
                ReflectionSupport.getPropertyValues(new BeanMetGetter("val")), is(equalTo(expected)));

        expected.clear();
        expected.put("class", BeanMetBooleanProperty.class);
        expected.put("indicatie", Boolean.FALSE);
        assertThat("Properties of BeanMetBooleanProperty",
                ReflectionSupport.getPropertyValues(new BeanMetBooleanProperty(false)), is(equalTo(expected)));

        expected.clear();
        expected.put("class", BeanMetAccessorsEnVelden.class);
        expected.put("indicatie", Boolean.TRUE);
        expected.put("indicatie2", Boolean.TRUE);
        expected.put("waarde", "String waarde");
        expected.put("waarde2", "String waarde");
        assertThat("Properties of BeanMetAccessorsEnVelden",
                ReflectionSupport.getPropertyValues(new BeanMetAccessorsEnVelden("String waarde", true)), is(equalTo(expected)));
    }

    @Test
    public void testSetProperty_finalField() {
        BeanMetAccessorsEnVelden bean = new BeanMetAccessorsEnVelden("oude waarde", false);
        boolean result = ReflectionSupport.setPropertyValue(bean, "waarde", "nieuweWaarde");
        assertThat(result, is(equalTo(false)));
        assertThat(bean.waarde, is(equalTo("oude waarde")));
    }

    public static class BeanMetGetter {
        private final String value;

        public BeanMetGetter(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class BeanMetBooleanProperty {
        private final boolean indicatie;

        public BeanMetBooleanProperty(boolean indicatie) {
            this.indicatie = indicatie;
        }

        public boolean isIndicatie() {
            return indicatie;
        }
    }

    public static class BeanMetAccessorsEnVelden {
        public final String waarde;
        public final boolean indicatie;

        public BeanMetAccessorsEnVelden(String waarde, boolean indicatie) {
            this.waarde = waarde;
            this.indicatie = indicatie;
        }

        public String getWaarde2() {
            return waarde;
        }

        public boolean isIndicatie2() {
            return indicatie;
        }
    }
}
