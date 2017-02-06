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

package nl.talsmasoftware.enumerables;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Basic 'sanity-check' for the parseLeniently method that is being used in other tests.
 *
 * @author Sjoerd Talsma
 */
public class CarBrandTest {

    @Test
    public void testParseLeniently_null() {
        assertThat(CarBrand.parseLeniently(null), is(nullValue()));
    }

    @Test
    public void testParseLeniently_empty() {
        assertThat(CarBrand.parseLeniently(""), is(not(nullValue())));
        assertThat(CarBrand.parseLeniently("").name(), is(nullValue()));
        assertThat(CarBrand.parseLeniently("").getValue(), is(equalTo("")));
    }

    @Test
    public void testParseLeniently() {
        assertThat(CarBrand.parseLeniently("citroen"), is(sameInstance(CarBrand.CITROEN)));
        assertThat(CarBrand.parseLeniently("mercedesbenz"), is(sameInstance(CarBrand.MERCEDES_BENZ)));
        assertThat(CarBrand.parseLeniently("mercedes benz"), is(sameInstance(CarBrand.MERCEDES_BENZ)));
        assertThat(CarBrand.parseLeniently("gm"), is(sameInstance(CarBrand.GM)));
        assertThat(CarBrand.parseLeniently("generalMotors"), is(sameInstance(CarBrand.GM)));
    }

    @Test
    public void testParseLeniently_notFound() {
        CarBrand rover = CarBrand.parseLeniently("Rover");
        assertThat(rover, is(not(nullValue())));
        assertThat(rover.name(), is(nullValue()));
        assertThat(rover.getValue(), is(equalTo("Rover")));
        assertThat(CarBrand.parseLeniently("Rover"), is(equalTo(rover)));
    }
}
