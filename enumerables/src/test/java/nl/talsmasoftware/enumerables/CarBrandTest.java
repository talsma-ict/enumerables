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
package nl.talsmasoftware.enumerables;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic 'sanity-check' for the parseLeniently method that is being used in other tests.
 *
 * @author Sjoerd Talsma
 */
class CarBrandTest {

    @Test
    void testParseLeniently_null() {
        assertThat(CarBrand.parseLeniently(null)).isNull();
    }

    @Test
    void testParseLeniently_empty() {
        CarBrand result = CarBrand.parseLeniently("");
        assertThat(result).isNotNull();
        assertThat(result.name()).isNull();
        assertThat(result.getValue()).isEmpty();
    }

    @Test
    void testParseLeniently() {
        assertThat(CarBrand.parseLeniently("citroen")).isSameAs(CarBrand.CITROEN);
        assertThat(CarBrand.parseLeniently("mercedesbenz")).isSameAs(CarBrand.MERCEDES_BENZ);
        assertThat(CarBrand.parseLeniently("mercedes benz")).isSameAs(CarBrand.MERCEDES_BENZ);
        assertThat(CarBrand.parseLeniently("gm")).isSameAs(CarBrand.GM);
        assertThat(CarBrand.parseLeniently("generalMotors")).isSameAs(CarBrand.GM);
    }

    @Test
    void testParseLeniently_notFound() {
        CarBrand rover = CarBrand.parseLeniently("Rover");
        assertThat(rover).isNotNull();
        assertThat(rover.name()).isNull();
        assertThat(rover.getValue()).isEqualTo("Rover");
        assertThat(CarBrand.parseLeniently("Rover"))
                .isNotSameAs(rover)
                .isEqualTo(rover);
    }
}
