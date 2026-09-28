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

/// Jackson 3 support for [Enumerable][nl.talsmasoftware.enumerables.Enumerable] types.
///
/// This package provides serialization and deserialization capabilities for [Enumerable][nl.talsmasoftware.enumerables.Enumerable]
/// types using Jackson 3 (`tools.jackson`).
///
/// The main entry point is [EnumerableModule], which can be registered with a Jackson 3 mapper:
///
/// ```java
/// JsonMapper mapper = JsonMapper.builder()
///         .addModule(new EnumerableModule())
///         .build();
/// ```
///
/// The module can also be automatically discovered by Jackson:
///
/// ```java
/// JsonMapper mapper = JsonMapper.builder()
///         .findAndAddModules()
///         .build();
/// ```
///
/// @author Sjoerd Talsma
package nl.talsmasoftware.enumerables.jackson3;
