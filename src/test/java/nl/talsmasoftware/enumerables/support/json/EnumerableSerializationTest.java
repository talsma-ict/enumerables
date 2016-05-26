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

package nl.talsmasoftware.enumerables.support.json;

import nl.talsmasoftware.enumerables.CarBrand;
import org.junit.Test;

import static nl.talsmasoftware.enumerables.support.json.EnumerableSerialization.JSON_OBJECT;
import static nl.talsmasoftware.enumerables.support.json.EnumerableSerialization.PLAIN_STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableSerializationTest {

    @Test
    public void testDefaultSerializationMethod() {
        assertThat(PLAIN_STRING.serializeAsJsonObject(CarBrand.class), is(false));
        assertThat(JSON_OBJECT.serializeAsJsonObject(CarBrand.class), is(true));
    }

    @Test
    public void testExceptMethods() {
        assertThat(PLAIN_STRING.except(CarBrand.class).serializeAsJsonObject(CarBrand.class), is(true));
        assertThat(JSON_OBJECT.except(CarBrand.class).serializeAsJsonObject(CarBrand.class), is(false));
    }

}
