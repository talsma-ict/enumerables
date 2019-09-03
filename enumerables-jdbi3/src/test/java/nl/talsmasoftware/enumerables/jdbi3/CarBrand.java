/*
 * Copyright 2016-2019 Talsma ICT
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
package nl.talsmasoftware.enumerables.jdbi3;

import nl.talsmasoftware.enumerables.Enumerable;

public final class CarBrand extends Enumerable {
    public static final CarBrand ASTON_MARTIN = new CarBrand("Aston martin");
    public static final CarBrand JAGUAR = new CarBrand("Jaguar");
    public static final CarBrand TESLA = new CarBrand("Tesla");
    // We all know there are more CarBrands than the ones we identified here...
    // Not a good fit for a java.lang.Enum, but suitable for Enumerable.

    private CarBrand(String value) {
        super(value);
    }
}
