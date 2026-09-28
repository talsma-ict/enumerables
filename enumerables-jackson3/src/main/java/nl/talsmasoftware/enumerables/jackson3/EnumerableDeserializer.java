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

/// Deserializer for [Enumerable] objects using Jackson 3.
///
/// Can deserialize either primitive JSON strings or JSON objects containing a `"value"` property
/// into concrete [Enumerable] instances using [Enumerable#parse(Class, CharSequence)].
///
/// @param <E> The concrete [Enumerable] type being deserialized
/// @author Sjoerd Talsma
public class EnumerableDeserializer<E extends Enumerable> extends StdDeserializer<E> {

    /// Default constructor for untyped deserialization.
    EnumerableDeserializer() {
        this((Class<E>) null);
    }

    /// Constructs a deserializer for the specified [Enumerable] subtype.
    ///
    /// @param enumerableType The concrete [Enumerable] class to deserialize into
    public EnumerableDeserializer(Class<E> enumerableType) {
        this(TypeFactory.createDefaultInstance().constructType(enumerableType != null ? enumerableType : Enumerable.class));
    }

    /// Constructs a deserializer for the specified [JavaType] representing an [Enumerable] subtype.
    ///
    /// @param valueType The [JavaType] representing the [Enumerable] subtype
    protected EnumerableDeserializer(JavaType valueType) {
        super(valueType);
    }

    /// Deserializes JSON content into a concrete [Enumerable] instance.
    ///
    /// Supports deserialization from:
    /// - JSON string or null value: parsed directly using [Enumerable#parse(Class, CharSequence)]
    /// - JSON object: parsed by extracting the `"value"` property
    ///
    /// @param parser JSON parser containing the content to deserialize
    /// @param context Deserialization context
    /// @return The deserialized [Enumerable] instance
    /// @throws JacksonException if an error occurs during parsing or if the token is unsupported
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
        E value = null;
        boolean parsed = false;
        for (JsonToken nextToken = parser.nextToken(); nextToken != null; nextToken = parser.nextToken()) {
            switch (nextToken) {
                case VALUE_NULL, VALUE_STRING:
                    if (!parsed && "value".equals(parser.currentName())) {
                        value = Enumerable.parse(enumerableType, parser.getString());
                        parsed = true;
                    }
                    break;
                case END_OBJECT:
                    parser.clearCurrentToken();
                    if (!parsed) {
                        throw new IllegalStateException("Attribute \"value\" is required for Enumerable JSON object.");
                    }
                    return value;
                case START_ARRAY, START_OBJECT:
                    parser.skipChildren();
                    parser.clearCurrentToken();
                    break;
            }
        }
        throw new IllegalStateException("JSON stream ended while parsing an Enumerable object.");
    }

    private Class<E> determineEnumerableType(JsonParser parser) {
        Class<?> enumerableType = this._valueClass;
        if (parser.getTypeId() instanceof JavaType type && type.isTypeOrSubTypeOf(Enumerable.class)) {
            enumerableType = type.getRawClass();
        }
        return ensureEnumerableType(enumerableType);
    }

    private static <E> Class<E> ensureEnumerableType(Class<?> enumerableType) {
        if (enumerableType == null || Enumerable.class.equals(enumerableType) || !Enumerable.class.isAssignableFrom(enumerableType)) {
            enumerableType = UnknownEnumerable.class;
        }
        return (Class<E>) enumerableType;
    }

    /// Non-abstract [Enumerable] class to deserialize if the concrete type can somehow not be determined.
    ///
    /// _Note:_ This type is not for general use.
    static final class UnknownEnumerable extends Enumerable {
        private UnknownEnumerable(String value) {
            super(value);
        }
    }

    /// Enumerable deserializer Modifier.
    ///
    /// Checks if the bean to be deserialized happens to be a subtype of [Enumerable] and if so,
    /// returns a typed instance of the [EnumerableDeserializer] to be used as value deserializer.
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
