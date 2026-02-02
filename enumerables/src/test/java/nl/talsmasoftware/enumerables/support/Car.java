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
package nl.talsmasoftware.enumerables.support;

import nl.talsmasoftware.enumerables.CarBrand;

/**
 * Object to test serialization with.
 *
 * @author Sjoerd Talsma
 */
public class Car {

    public CarBrand brand;

    public Car() {
        this(null);
    }

    public Car(CarBrand brand) {
        this.brand = brand;
    }


    @Override
    public int hashCode() {
        return brand.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Car
                && brand == null ? ((Car) other).brand == null : brand.equals(((Car) other).brand));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{brand=" + brand + '}';
    }
}
