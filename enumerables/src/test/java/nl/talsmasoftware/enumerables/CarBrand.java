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
package nl.talsmasoftware.enumerables;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sjoerd Talsma
 */
public final class CarBrand extends Enumerable {

    public static final CarBrand ALFA_ROMEO = new CarBrand("Alfa romeo");
    public static final CarBrand ASTON_MARTIN = new CarBrand("Aston martin");
    public static final CarBrand AUDI = new CarBrand("Audi");
    public static final CarBrand BENTLEY = new CarBrand("Bentley");
    public static final CarBrand BMW = new CarBrand("BMW");
    public static final CarBrand CADILLAC = new CarBrand("Cadillac");
    public static final CarBrand CHEVROLET = new CarBrand("Chevrolet");
    public static final CarBrand CHRYSLER = new CarBrand("Chrysler");
    public static final CarBrand CITROEN = new CarBrand("Citro\u00EBn");
    public static final CarBrand DODGE = new CarBrand("Dodge");
    public static final CarBrand FERRARI = new CarBrand("Ferrari");
    public static final CarBrand FIAT = new CarBrand("Fiat");
    public static final CarBrand FORD = new CarBrand("Ford");
    public static final CarBrand GM = new CarBrand("General Motors");
    public static final CarBrand HONDA = new CarBrand("Honda");
    public static final CarBrand HYUNDAI = new CarBrand("Hyundai");
    public static final CarBrand INFINITY = new CarBrand("Infinity");
    public static final CarBrand JAGUAR = new CarBrand("Jaguar");
    public static final CarBrand JEEP = new CarBrand("Jeep");
    public static final CarBrand KIA = new CarBrand("KIA Motors");
    public static final CarBrand LAMBORGHINI = new CarBrand("Lamborghini");
    public static final CarBrand LAND_ROVER = new CarBrand("Land Rover");
    public static final CarBrand LEXUS = new CarBrand("Lexus");
    public static final CarBrand MASERATI = new CarBrand("Maserati");
    public static final CarBrand MAZDA = new CarBrand("Mazda");
    public static final CarBrand MERCEDES_BENZ = new CarBrand("Mercedes-Benz");
    public static final CarBrand MINI = new CarBrand("Mini");
    public static final CarBrand MITSUBISHI = new CarBrand("Mitsubishi Motors");
    public static final CarBrand NISSAN = new CarBrand("Nissan");
    public static final CarBrand PEUGEOT = new CarBrand("Peugeot");
    public static final CarBrand PORSCHE = new CarBrand("Porsche");
    public static final CarBrand RENAULT = new CarBrand("Renault");
    public static final CarBrand ROLLS_ROYCE = new CarBrand("Rolls Royce");
    public static final CarBrand SUBARU = new CarBrand("Subaru");
    public static final CarBrand SUZUKI = new CarBrand("Suzuki");
    public static final CarBrand TESLA = new CarBrand("Tesla Motors");
    public static final CarBrand TOYOTA = new CarBrand("Toyota");
    public static final CarBrand VOLKSWAGEN = new CarBrand("Volkswagen");
    public static final CarBrand VOLVO = new CarBrand("Volvo");

    private CarBrand(String value) {
        super(value);
    }

    private static final Map<String, CarBrand> LENIENT_BRANDS = Collections.unmodifiableMap(new HashMap<String, CarBrand>() {{
        for (CarBrand brand : Enumerable.values(CarBrand.class)) {
            put(lenient(brand.getValue()), brand);
            put(lenient(brand.name()), brand);
        }
    }});

    /**
     * Lenient parser because I am too lazy to remember exactly how I declared the constants ;-) ..
     * <p>
     * Also could be used as an example of how lenient parsing might be achieved.
     *
     * @param carbrand The car brand to parse.
     * @return The parsed carbrand.
     */
    public static CarBrand parseLeniently(final CharSequence carbrand) {
        final CarBrand foundConstant = LENIENT_BRANDS.get(lenient(carbrand));
        return foundConstant != null ? foundConstant : Enumerable.parse(CarBrand.class, carbrand);
    }

    /**
     * Provides some leniency by stripping the string of all non-relevant characters for the comparison and converting
     * it to lowercase.
     */
    private static String lenient(CharSequence value) {
        return value != null ? value.toString().replaceAll("[\\s\\-_]*", "").toLowerCase(Locale.ENGLISH) : null;
    }

}
