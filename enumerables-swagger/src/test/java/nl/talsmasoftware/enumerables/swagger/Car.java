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
package nl.talsmasoftware.enumerables.swagger;

import nl.talsmasoftware.enumerables.Enumerable;

public class Car {

    public static final class CarBrand extends Enumerable {
        public static final CarBrand TESLA = new CarBrand("Tesla");
        public static final CarBrand UNITI = new CarBrand("Uniti Sweden");

        private CarBrand(String value) {
            super(value);
        }
    }

    public CarBrand brand;
    public String type;

    public Car() {
        this(null, null);
    }

    public Car(CarBrand brand, String type) {
        this.brand = brand;
        this.type = type;
    }


    public int hashCode() {
        return hash(brand, type);
    }

    public boolean equals(Object other) {
        return this == other || (other instanceof Car
                && equals(this.brand, ((Car) other).brand)
                && equals(this.type, ((Car) other).type)
        );
    }

    public String toString() {
        return getClass().getSimpleName() + "{brand=" + brand + ", type=" + type + '}';
    }

    // Unfortunately, we're not in java 7 land yet..
    private static int hash(Object... objs) {
        int hash = 0;
        for (Object obj : objs) {
            hash = 31 * hash;
            if (obj != null) hash += obj.hashCode();
        }
        return hash;
    }

    private static boolean equals(Object obj1, Object obj2) {
        return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    }
}
