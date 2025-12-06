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
package nl.talsmasoftware.enumerables.gson;

import nl.talsmasoftware.enumerables.Enumerable;

/**
 * Object to test serialization with.
 *
 * @author Sjoerd Talsma
 */
public class Car {

    public static final class Brand extends Enumerable {
        public static final Brand ALFA_ROMEO = new Brand("Alfa romeo");
        public static final Brand ASTON_MARTIN = new Brand("Aston martin");
        public static final Brand AUDI = new Brand("Audi");
        public static final Brand BENTLEY = new Brand("Bentley");
        public static final Brand BMW = new Brand("BMW");
        public static final Brand CADILLAC = new Brand("Cadillac");
        public static final Brand CHEVROLET = new Brand("Chevrolet");
        public static final Brand CHRYSLER = new Brand("Chrysler");
        public static final Brand CITROEN = new Brand("Citro\u00EBn");
        public static final Brand DODGE = new Brand("Dodge");
        public static final Brand FERRARI = new Brand("Ferrari");
        public static final Brand FIAT = new Brand("Fiat");
        public static final Brand FORD = new Brand("Ford");
        public static final Brand GM = new Brand("General Motors");
        public static final Brand HONDA = new Brand("Honda");
        public static final Brand HYUNDAI = new Brand("Hyundai");
        public static final Brand INFINITY = new Brand("Infinity");
        public static final Brand JAGUAR = new Brand("Jaguar");
        public static final Brand JEEP = new Brand("Jeep");
        public static final Brand KIA = new Brand("KIA Motors");
        public static final Brand LAMBORGHINI = new Brand("Lamborghini");
        public static final Brand LAND_ROVER = new Brand("Land Rover");
        public static final Brand LEXUS = new Brand("Lexus");
        public static final Brand MASERATI = new Brand("Maserati");
        public static final Brand MAZDA = new Brand("Mazda");
        public static final Brand MERCEDES_BENZ = new Brand("Mercedes-Benz");
        public static final Brand MINI = new Brand("Mini");
        public static final Brand MITSUBISHI = new Brand("Mitsubishi Motors");
        public static final Brand NISSAN = new Brand("Nissan");
        public static final Brand PEUGEOT = new Brand("Peugeot");
        public static final Brand PORSCHE = new Brand("Porsche");
        public static final Brand RENAULT = new Brand("Renault");
        public static final Brand ROLLS_ROYCE = new Brand("Rolls Royce");
        public static final Brand SUBARU = new Brand("Subaru");
        public static final Brand SUZUKI = new Brand("Suzuki");
        public static final Brand TESLA = new Brand("Tesla Motors");
        public static final Brand TOYOTA = new Brand("Toyota");
        public static final Brand VOLKSWAGEN = new Brand("Volkswagen");
        public static final Brand VOLVO = new Brand("Volvo");

        private Brand(String value) {
            super(value);
        }
    }

    public Brand brand;

    public Car() {
        this((Brand) null);
    }

    public Car(String brand) {
        this(Enumerable.parse(Brand.class, brand));
    }

    public Car(Brand brand) {
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