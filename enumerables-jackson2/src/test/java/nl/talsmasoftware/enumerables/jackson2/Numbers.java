/*
 * Copyright 2016-2020 Talsma ICT
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
package nl.talsmasoftware.enumerables.jackson2;

import nl.talsmasoftware.enumerables.Enumerable;

/**
 * Enumerable for the numbers {@code 0..10}.
 *
 *
 * @author Sjoerd Talsma
 */
public final class Numbers extends Enumerable {

    public static final Numbers ZERO = new Numbers("ZERO", 0);
    public static final Numbers ONE = new Numbers("ONE", 1);
    public static final Numbers TWO = new Numbers("TWO", 2);
    public static final Numbers THREE = new Numbers("THREE", 3);
    public static final Numbers FOUR = new Numbers("FOUR", 4);
    public static final Numbers FIVE = new Numbers("FIVE", 5);
    public static final Numbers SIX = new Numbers("SIX", 6);
    public static final Numbers SEVEN = new Numbers("SEVEN", 7);
    public static final Numbers EIGHT = new Numbers("EIGHT", 8);
    public static final Numbers NINE = new Numbers("NINE", 9);
    public static final Numbers TEN = new Numbers("TEN", 10);

    public final Integer number;

    private Numbers(String value) {
        this(value, null);
    }

    private Numbers(String value, Integer number) {
        super(value);
        this.number = number;
    }

}
