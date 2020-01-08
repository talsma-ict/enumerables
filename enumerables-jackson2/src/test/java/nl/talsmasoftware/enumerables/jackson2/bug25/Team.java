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
package nl.talsmasoftware.enumerables.jackson2.bug25;

import nl.talsmasoftware.enumerables.Enumerable;

public class Team extends Enumerable {

    public static final Team RICHARD = new Team("Richard Kroon");
    public static final Team SJOERD = new Team("Sjoerd Talsma");

    private Team(String waarde) {
        super(waarde);
    }

}
