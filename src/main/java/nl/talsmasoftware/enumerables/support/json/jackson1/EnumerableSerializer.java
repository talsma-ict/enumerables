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
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.BasicClassIntrospector;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.type.SimpleType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;

/**
 * Serializer for Jackson-1 to serialize Enumerable Objects with.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableSerializer extends SerializerBase<Enumerable> {

    /**
     * Static cache for reflected objects based on their class names.
     */
    private static final ConcurrentMap<String, List<BeanPropertyDefinition>> CACHE = new ConcurrentHashMap<String, List<BeanPropertyDefinition>>();

    /**
     * Hardcoded names of <code>Inclusion</code> values that should skip <code>null</code> property values.
     */
    private static final Collection<String> SKIP_NULL_INCLUSIONS = unmodifiableCollection(asList(
            "NON_NULL", "NON_ABSENT", "NON_EMPTY"
    ));

    /**
     * The serialization method to determine whether we need to serialize a plain String or a JSON Object.
     */
    private final SerializationMethod serializationMethod;

    protected EnumerableSerializer() {
        this(null);
    }

    public EnumerableSerializer(SerializationMethod serializationMethod) {
        super(Enumerable.class);
        this.serializationMethod = serializationMethod != null ? serializationMethod : PLAIN_STRING;
    }

    @Override
    public void serialize(Enumerable value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (value == null) {
            jgen.writeNull();
        } else if (serializationMethod.serializeAsJsonObject(value.getClass())) {
            serializeObject(value, jgen, provider.getConfig());
        } else {
            jgen.writeString(Enumerable.print(value));
        }
    }

    private void serializeObject(Enumerable value, JsonGenerator jgen, SerializationConfig config) throws IOException {
        jgen.writeStartObject();
        for (BeanPropertyDefinition property : serializationPropertiesFor(value.getClass(), config)) {
            if (property.couldSerialize()) {
                final Object propertyValue = readProperty(property, value);
                if (propertyValue != null || property.isExplicitlyIncluded() || mustIncludeNull(config)) {
                    jgen.writeObjectField(property.getName(), propertyValue);
                }
            }
        }
        jgen.writeEndObject();
    }

    private Object readProperty(BeanPropertyDefinition property, Enumerable value) {
        try {
            AnnotatedMember accessor = property.getAccessor();
            Member member = accessor != null ? accessor.getMember() : null;
            if (member instanceof Method) return ((Method) member).invoke(value);
            else if (member instanceof Field) return ((Field) member).get(value);
        } catch (IllegalAccessException iae) {
            Logger.getLogger(getClass().getName()).log(Level.FINEST, "Not allowed to read property {0} from {1}: {2}",
                    new Object[]{property, value, iae.getMessage(), iae});
        } catch (InvocationTargetException ite) {
            Throwable exception = ite.getCause() != null ? ite.getCause() : ite;
            throw exception instanceof RuntimeException ? (RuntimeException) exception
                    : new RuntimeException(exception.getMessage(), exception);
        }
        return null;
    }

    private boolean mustIncludeNull(SerializationConfig config) {
        JsonSerialize.Inclusion inclusion = config.getSerializationInclusion();
        return inclusion == null || !SKIP_NULL_INCLUSIONS.contains(inclusion.name());
    }

    private static List<BeanPropertyDefinition> serializationPropertiesFor(Class<?> simpleType, SerializationConfig config) {
        final String cacheKey = simpleType.getName();
        List<BeanPropertyDefinition> properties = CACHE.get(cacheKey);
        if (properties == null) {
            properties = new BasicClassIntrospector()
                    .forSerialization(config, SimpleType.construct(simpleType), null)
                    .findProperties();
            CACHE.putIfAbsent(cacheKey, properties);
        }
        return properties;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' + serializationMethod + '}';
    }

}
