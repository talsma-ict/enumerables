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
/**
 * The main class in this library is
 * {@link nl.talsmasoftware.enumerables.Enumerable Enumerable}, similar to the standard Java {@link java.lang.Enum Enum}
 * type. It has one special feature that makes it more suitable for using in an API that you need to maintain.
 * <p>
 * Ever have an actual {@link java.lang.Enum} in an exposed API?
 * Then have the customer come up with an additional value for that {@link java.lang.Enum} that should also be supported?
 * <strong>*Bang!*</strong> there goes your API compatibility. You will either have to tel all your existing customers
 * <em>"sorry, the api is now broken"</em> or create a new version <strong>beside</strong> the existing API and declare
 * the old one deprecated. However, you'll still have to think about how to represent the additional value in the old
 * version or create a special exception for this case.<br>
 * Nasty in any case.
 * <p>
 * That is exactly the reason we've created the <code>Enumerable</code> type.
 * It is similar to {@link java.lang.Enum} in that it represents a list of <em>known constants</em>
 * but also offers a possibility to represent <em>yet unknown additional values</em> by parsing them.
 * In fact; parsing a known value results in the single constant reference.
 * Same goes for serialization and deserialization.
 *
 * @author Sjoerd Talsma
 */
package nl.talsmasoftware.enumerables;