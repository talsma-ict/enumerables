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

package nl.talsmasoftware.enumerables.descriptions;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Sjoerd Talsma
 */
public class DescriptionsTest {

    @Test
    public void testCapitalize() {
        assertThat(Descriptions.capitalize(null), is(nullValue()));
        assertThat(Descriptions.capitalize(""), is(""));
        assertThat(Descriptions.capitalize(" "), is(" "));
        assertThat(Descriptions.capitalize("all-lower"), is("All-lower"));
        assertThat(Descriptions.capitalize("ALL-CAPS"), is("ALL-CAPS"));
        assertThat(Descriptions.capitalize("CamelCase"), is("CamelCase"));
        assertThat(Descriptions.capitalize(" untrimmed "), is(" untrimmed "));
    }

    @Test
    public void testDecamelize() {
        assertThat(Descriptions.decamelize(null), is(nullValue()));
        assertThat(Descriptions.decamelize(""), is(""));
        assertThat(Descriptions.decamelize(" "), is(" "));
        assertThat(Descriptions.decamelize("all-lower"), is("all-lower"));
        assertThat(Descriptions.decamelize("ALL-CAPS"), is("ALL-CAPS"));
        assertThat(Descriptions.decamelize("CamelCase"), is("Camel case"));
        assertThat(Descriptions.decamelize(" untrimmed "), is(" untrimmed "));
        assertThat(Descriptions.decamelize(" unTrimmed "), is(" un trimmed "));
        // End camelized
        assertThat(Descriptions.decamelize("camelCaseD"), is("camel case d"));
        // Abbreviations  (multiple uppercase characters) should be left unchanged.
        assertThat(Descriptions.decamelize("camelCaseAB"), is("camel case AB"));
        assertThat(Descriptions.decamelize("containsABBR"), is("contains ABBR"));

        // This is up for discussion; for now, we'll leave end-of abbreviations alone:
        assertThat(Descriptions.decamelize("JBoss"), is("JBoss"));
        assertThat(Descriptions.decamelize("containsABBRcontinued"), is("contains ABBRcontinued"));
    }

}
