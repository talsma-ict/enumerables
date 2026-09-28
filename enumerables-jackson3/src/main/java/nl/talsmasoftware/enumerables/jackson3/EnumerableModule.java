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
package nl.talsmasoftware.enumerables.jackson3;

import nl.talsmasoftware.enumerables.Enumerable;
import tools.jackson.databind.module.SimpleModule;

/// Jackson 3 [module][tools.jackson.databind.JacksonModule] for mapping [Enumerable] types to and from JSON.
///
/// Registering this module allows deserializing both primitive strings and JSON objects into concrete [Enumerable]
/// instances, and serializing [Enumerable] instances into their string representation.
///
/// ### Usage example:
/// ```java
/// JsonMapper mapper = JsonMapper.builder()
///         .addModule(new EnumerableModule())
///         .build();
/// ```
///
/// @author Sjoerd Talsma
public class EnumerableModule extends SimpleModule {

    /// Constructs a new [EnumerableModule] configuring serializer and deserializer for [Enumerable] types.
    public EnumerableModule() {
        super.addSerializer(new EnumerableSerializer());
        super.addDeserializer(Enumerable.class, new EnumerableDeserializer<>());
    }

    /// Sets up the module by registering serializer, deserializer, and a deserializer modifier to handle any subtype of [Enumerable].
    ///
    /// @param context The setup context used to register deserializer modifier
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context.addDeserializerModifier(new EnumerableDeserializer.Modifier()));
    }
}
