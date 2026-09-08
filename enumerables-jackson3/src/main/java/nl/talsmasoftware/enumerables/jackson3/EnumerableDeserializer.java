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
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.databind.type.TypeFactory;

public class EnumerableDeserializer<E extends Enumerable> extends StdDeserializer<E> {

    public EnumerableDeserializer(Class<E> enumerableType) {
        super(TypeFactory.createDefaultInstance().constructType(enumerableType != null ? enumerableType : Enumerable.class));
    }

    protected EnumerableDeserializer(JavaType valueType) {
        super(valueType);
    }

    @Override
    public E deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        Class<E> enumerableType = determineEnumerableType(parser);
        JsonToken currentToken = parser.currentToken();
        return switch (currentToken) {
            case VALUE_NULL, VALUE_STRING -> Enumerable.parse(enumerableType, parser.getString());
            case START_OBJECT -> parseObject(parser, enumerableType);
            default ->
                    throw ValueInstantiationException.from(parser, "Could not deserialize a valid Enumerable object.",
                            this._valueType, new IllegalStateException(String.format("Unexpected parser token: \"%s\".", currentToken)));
        };
    }

    private E parseObject(JsonParser parser, Class<E> enumerableType) {
        throw new IllegalStateException("Object support not available yet.");
    }

    private Class<E> determineEnumerableType(JsonParser parser) {
        if (parser.getTypeId() instanceof JavaType type && type.isTypeOrSubTypeOf(Enumerable.class)) {
            return (Class<E>) type.getRawClass();
        }
        return (Class<E>) UnknownEnumerable.class;
    }

    /// Non-abstract [Enumerable] class to deserialize if the concrete type can somehow not be determined.
    ///
    /// This type is not for general use.
    static final class UnknownEnumerable extends Enumerable {
        private UnknownEnumerable(String value) {
            super(value);
        }
    }

    static final class Modifier extends ValueDeserializerModifier {
        @Override
        public ValueDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription.Supplier beanDescription, ValueDeserializer<?> deserializer) {
            final JavaType type = beanDescription.getType();
            return type != null && type.isTypeOrSubTypeOf(Enumerable.class)
                    ? new EnumerableDeserializer<>(type)
                    : super.modifyDeserializer(config, beanDescription, deserializer);
        }
    }
}
