/*
 * Copyright 2016-2017 Talsma ICT
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import nl.talsmasoftware.enumerables.Enumerable;

import java.io.IOException;

/**
 * Deserializer for {@link Enumerable} objects of a specific type.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableJackson2Deserializer extends StdDeserializer<Enumerable> implements ContextualDeserializer {
    private final JavaType javaType;

    public EnumerableJackson2Deserializer() {
        this(null);
    }

    private EnumerableJackson2Deserializer(JavaType javaType) {
        super(javaType);
        this.javaType = javaType;
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (javaType == null) { // Are we the 'untyped' Enumerable deserializer?
            if (property != null && property.getType() != null) {
                return new EnumerableJackson2Deserializer(property.getType());
            } else if (ctxt != null) {
                final JavaType contextualType = Compatibility.getContextualType(ctxt);
                if (contextualType != null) {
                    return new EnumerableJackson2Deserializer(contextualType);
                }
            }
        }
        return this;
    }

    /**
     * Determines specific Enumerable subtype when known.
     * If no subtype can be determined, the {@link UnknownEnumerable} type is returned.
     * At least the received String value can be represtend by this type.
     *
     * @param jp Jackson parser to obtain the type from.
     * @return The actual Enumerable type being deserialized or {@code UnknownEnumerable}.
     * @throws IOException when reading the parser threw I/O exceptions.
     */
    protected Class<? extends Enumerable> getType(JsonParser jp) throws IOException {
        Class<? extends Enumerable> type = javaType == null ? null : asEnumerableSubtype(javaType.getRawClass());
        if (type == null) {
            final Object typeId = Compatibility.getTypeId(jp);
            type = asEnumerableSubtype(typeId instanceof JavaType ? ((JavaType) typeId).getRawClass() : typeId);
        }
        return type == null || Enumerable.class.equals(type) ? UnknownEnumerable.class : type;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enumerable> Class<E> asEnumerableSubtype(Object type) {
        return type instanceof Class<?> && Enumerable.class.isAssignableFrom((Class<?>) type) ? (Class<E>) type : null;
    }

    /**
     * Deserializes the JSON object as a concrete {@link Enumerable} instance.
     *
     * @param jp   Jackson parser to obtain the value from.
     * @param ctxt The deserialization context.
     * @return The enumerable object parsed from the JSON string.
     * @throws IOException when accessing the JsonParser threw an I/O exception.
     */
    @Override
    public Enumerable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final Class<? extends Enumerable> type = getType(jp);
        final JsonToken currentToken = jp.getCurrentToken();
        switch (currentToken) {
            case VALUE_NULL:
            case VALUE_STRING:
                return Enumerable.parse(type, jp.getText());
            case START_OBJECT:
                return parseObject(jp, type);
            default:
                throw new IllegalStateException("Could not parse a valid Enumerable object!",
                        new IllegalStateException(String.format("Unexpected parser token: \"%s\".", currentToken)));
        }
    }

    private Enumerable parseObject(JsonParser jp, Class<? extends Enumerable> type) throws IOException {
        Enumerable value = null;
        for (JsonToken nextToken = jp.nextToken(); nextToken != null; nextToken = jp.nextToken()) {
            switch (nextToken) {
                case VALUE_NULL:
                case VALUE_STRING:
                    if (value == null && "value".equals(jp.getCurrentName())) {
                        value = Enumerable.parse(type, jp.getText());
                    }
                    break;
                case END_OBJECT:
                    jp.clearCurrentToken();
                    if (value != null) return value;
                    throw new IllegalStateException("Attribute \"value\" is required for Enumerable JSON object.");
                case START_ARRAY:
                case START_OBJECT:
                    jp.skipChildren();
                    jp.clearCurrentToken();
                    break;
            }
        }
        throw new IllegalStateException("JSON stream ended while parsing an Enumerable object.");
    }

}