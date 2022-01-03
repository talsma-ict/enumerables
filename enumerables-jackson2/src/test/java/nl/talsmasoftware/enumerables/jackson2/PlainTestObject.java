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
package nl.talsmasoftware.enumerables.jackson2;

import nl.talsmasoftware.enumerables.Enumerable;

/**
 * @author Sjoerd Talsma
 */
public class PlainTestObject {

    public static class BigCo extends Enumerable {
        public static final BigCo ORACLE = new BigCo("Oracle");
        public static final BigCo IBM = new BigCo("IBM");
        public static final BigCo MICROSOFT = new BigCo("Microsoft");
        public static final BigCo APPLE = new BigCo("Apple");

        private BigCo(String value) {
            super(value);
        }
    }

    private BigCo bigCo;

    public PlainTestObject() {
        this(null);
    }

    public PlainTestObject(BigCo bigCo) {
        this.bigCo = bigCo;
    }

    public BigCo getBigCo() {
        return bigCo;
    }

    public void setBigCo(BigCo bigCo) {
        this.bigCo = bigCo;
    }

    public int hashCode() {
        return bigCo == null ? -1 : bigCo.hashCode();
    }

    private static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public boolean equals(Object other) {
        return this == other || (other instanceof PlainTestObject
                && equals(this.bigCo, ((PlainTestObject) other).bigCo));
    }

    public String toString() {
        return getClass().getSimpleName() + "{bigCo=" + bigCo + '}';
    }
}
