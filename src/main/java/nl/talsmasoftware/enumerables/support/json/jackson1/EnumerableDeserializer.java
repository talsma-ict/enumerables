/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nl.talsmasoftware.enumerables.support.json.jackson1;

import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.UnknownEnumerable;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;

/**
 * Deserializer for Jackson-1 to deserialize Enumerable Objects from JSON with.
 * This deserializer can read {@link Enumerable#getValue() values} from both plain-String representations and
 * JSON Object representations.
 * Therefore, the {@link nl.talsmasoftware.enumerables.support.json.SerializationMethod serialization method} is not
 * relevant for this deserializer.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableDeserializer extends StdDeserializer<Enumerable> implements ContextualDeserializer {

    /**
     * Either a concrete subtype of {@link Enumerable} or the class {@link UnknownEnumerable}.
     */
    private final Class<? extends Enumerable> enumerableType;

    /**
     * Constructor for the general 'untyped' deserializer that delegates to a more specific instance when there is more
     * type information available (i.e. after {@link #createContextual(DeserializationConfig, BeanProperty)} has been
     * called.
     */
    protected EnumerableDeserializer() {
        this(null);
    }

    /**
     * Constructor for a more specific 'subtype' Enumerable object that remembers this subtype so it knows into which
     * concrete type the JSON object should be parsed.
     *
     * @param enumerableType The actual type of the enumerable to be deserialized into
     *                       (or <code>null</code> for the 'untyped' deserializer).
     */
    protected EnumerableDeserializer(Class<? extends Enumerable> enumerableType) {
        super(enumerableType);
        this.enumerableType = enumerableType == null || Enumerable.class.equals(enumerableType)
                ? UnknownEnumerable.class : enumerableType;
    }

    /**
     * Creates a more specific deserializer in case this instance is the 'general untyped' instance. The available
     * type information from the bean property and the deserialization context will be used to return a more specific
     * instance in that case.
     * If this deserializer is already specific, or no additional type information can be obtained, the method simply
     * returns a reference to <code>this</code> instance.
     *
     * @param config   The deserialization configuration to obtain type information from, if possible.
     * @param property The bean property to obtain type information from, if possible.
     * @return A more specific deserializer or a reference to <code>this</code> instance in case no more specific
     * deserializer could be found.
     */
    public JsonDeserializer<?> createContextual(DeserializationConfig config, BeanProperty property) {
        EnumerableDeserializer other = new EnumerableDeserializer(asEnumerableSubclass(property));
        return enumerableType.equals(other.enumerableType) ? this : other;
    }

    /**
     * This operation deserializes the JSON Object as a concrete Enumerable object instance.
     *
     * @param jp   The Jackson parser to obtain the value from.
     * @param ctxt The deserialization context.
     * @return The parsed enumerable object.
     * @throws IOException in case the parser encountered any I/O errors while reading the object.
     */
    @Override
    public Enumerable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        final JsonToken currentToken = jp.getCurrentToken();
        switch (currentToken) {
            case VALUE_NULL:
            case VALUE_STRING:
                return Enumerable.parse(enumerableType, jp.getText());
            case START_OBJECT:
                return parseObject(jp);
            default:
                throw new IllegalStateException("Could not parse a valid Enumerable object!",
                        new IllegalStateException(String.format("Onverwacht parser token: \"%s\".", currentToken)));
        }
    }

    private Enumerable parseObject(JsonParser jp) throws IOException {
        Enumerable value = null;
        while (true) {
            final JsonToken nextToken = jp.nextToken();
            if (nextToken == null)
                throw new IllegalStateException("JSON stream ended while parsing an Enumerable object.");

            switch (nextToken) {
                case VALUE_NULL:
                case VALUE_STRING:
                    if (value == null && "value".equals(jp.getCurrentName())) {
                        value = Enumerable.parse(enumerableType, jp.getText());
                    }
                    break;
                case END_OBJECT:
                    jp.clearCurrentToken();
                    if (value == null)
                        throw new IllegalStateException("Attribute \"value\" is required to parse an Enumerable JSON object.");
                    return value;
                case START_ARRAY:
                case START_OBJECT:
                    jp.skipChildren();
                    jp.clearCurrentToken();
                    break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    static <E extends Enumerable> Class<E> asEnumerableSubclass(Object type) {
        if (type instanceof BeanDescription) type = ((BeanDescription) type).getType();
        if (type instanceof BeanProperty) type = ((BeanProperty) type).getType();
        if (type instanceof JavaType) type = ((JavaType) type).getRawClass();
        return type instanceof Class<?> && Enumerable.class.isAssignableFrom((Class<?>) type) ? (Class<E>) type : null;
    }

}
