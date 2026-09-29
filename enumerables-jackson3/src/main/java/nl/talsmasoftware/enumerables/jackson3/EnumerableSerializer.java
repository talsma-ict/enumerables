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
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * Serializer for {@link Enumerable} objects using Jackson 3.
 *
 * <p>
 * Serializes {@link Enumerable} instances into their string value (obtained via {@link Enumerable#print(Enumerable)}).
 * {@code null} values are written as JSON {@code null}.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableSerializer extends StdSerializer<Enumerable> {

    /**
     * Constructs a default {@link EnumerableSerializer} for {@link Enumerable} types.
     */
    public EnumerableSerializer() {
        super(Enumerable.class);
    }

    /**
     * Serializes an {@link Enumerable} value to JSON.
     *
     * @param value Value to serialize; written as string or {@code null} if {@code null}
     * @param gen   Generator used to output JSON content
     * @param ctxt  Context that can be used to access information about serialization process
     * @throws JacksonException if an error occurs during serialization
     */
    @Override
    public void serialize(Enumerable value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(Enumerable.print(value));
        }
    }

}
