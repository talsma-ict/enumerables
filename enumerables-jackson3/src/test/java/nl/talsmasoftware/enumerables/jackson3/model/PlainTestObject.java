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

import java.util.Objects;

/**
 * @author Sjoerd Talsma
 */
public class PlainTestObject {

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
        return Objects.hash(bigCo);
    }

    public boolean equals(Object other) {
        return this == other || (other instanceof PlainTestObject
                && Objects.equals(this.bigCo, ((PlainTestObject) other).bigCo));
    }

    public String toString() {
        return getClass().getSimpleName() + "{bigCo=" + bigCo + '}';
    }
}

