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
package nl.talsmasoftware.enumerables.jackson3.model;

import nl.talsmasoftware.enumerables.Enumerable;

public class BigCo extends Enumerable {
    public static final BigCo APPLE = new BigCo("Apple");
    public static final BigCo MICROSOFT = new BigCo("Microsoft");
    public static final BigCo ORACLE = new BigCo("Oracle");
    public static final BigCo IBM = new BigCo("IBM");

    private BigCo(String value) {
        super(value);
    }
}
