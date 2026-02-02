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
package nl.talsmasoftware.enumerables.jaxrs;

import nl.talsmasoftware.enumerables.Enumerable;

/**
 * @author Sjoerd Talsma
 */
public final class TestEnumerable extends Enumerable {
    public static final TestEnumerable FIRST = new TestEnumerable("1st");
    public static final TestEnumerable SECOND = new TestEnumerable("2nd");
    public static final TestEnumerable THIRD = new TestEnumerable("3rd");

    private TestEnumerable(String value) {
        super(value);
    }
}
