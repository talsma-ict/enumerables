/*
 * Copyright 2016-2022 Talsma ICT
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
package nl.talsmasoftware.enumerables.jdbi;

public class Car {

    public CarBrand brand;
    public String type;
    public Integer productionYear;

    public Car() {
        this(null, null, null);
    }

    public Car(CarBrand brand, String type, Integer productionYear) {
        this.brand = brand;
        this.type = type;
        this.productionYear = productionYear;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Car
                && equals(this.brand, ((Car) other).brand)
                && equals(this.type, ((Car) other).type)
                && equals(this.productionYear, ((Car) other).productionYear)
        );
    }

    // because.. java 5
    private static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
