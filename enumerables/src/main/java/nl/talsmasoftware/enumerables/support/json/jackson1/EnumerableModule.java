/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *
 */
package nl.talsmasoftware.enumerables.support.json.jackson1;

import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;
import nl.talsmasoftware.enumerables.support.maven.MavenVersion;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.module.SimpleModule;

import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;
import static nl.talsmasoftware.enumerables.support.json.jackson1.EnumerableDeserializer.asEnumerableSubclass;

/**
 * Mapping module for converting one or more {@link Enumerable} types from and to JSON using the Jackson (v1)
 * library.
 * <p>
 * If a non-<code>null</code> {@link SerializationMethod} is specified in the module, that will be used for serializing
 * {@link Enumerable} objects. Otherwise, {@link SerializationMethod#PLAIN_STRING plain-String} serialization will be
 * used by default.
 *
 * @author Sjoerd Talsma
 */
public class EnumerableModule extends SimpleModule {

    private final SerializationMethod serializationMethod;
    private final EnumerableDeserializer enumerableDeserializer;

    /**
     * Constructor for a Jackson (v1) module to serialize and deserialize {@link Enumerable} objects to and from
     * plain JSON string values.
     */
    public EnumerableModule() {
        this(null);
    }

    /**
     * Constructor for a Jackson (v1) module to serialize and deserialize {@link Enumerable} objects to and from
     * JSON objects. This constructor allows the caller to specify an explicit {@link SerializationMethod} to be used.
     * If the <code>serializationMethod</code> is <code>null</code>, the module will use
     * {@link SerializationMethod#PLAIN_STRING plain String} serialization for {@link Enumerable} object
     * {@link Enumerable#getValue() values}.
     *
     * @param serializationMethod The serialization method to be used by this module.
     *                            String serialization will be used when <code>null</code>.
     */
    public EnumerableModule(SerializationMethod serializationMethod) {
        super("Enumerable mapping module", determineVersion("nl.talsmasoftware", "enumerables"));
        this.serializationMethod = serializationMethod != null ? serializationMethod : PLAIN_STRING;
        this.enumerableDeserializer = new EnumerableDeserializer();
        super.addSerializer(Enumerable.class, new EnumerableSerializer(this.serializationMethod));
        super.addDeserializer(Enumerable.class, enumerableDeserializer);
    }

    @Override
    public void setupModule(SetupContext context) {
        if (context != null) context.addBeanDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(
                    DeserializationConfig config, BasicBeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                return asEnumerableSubclass(beanDesc) != null ? enumerableDeserializer
                        : super.modifyDeserializer(config, beanDesc, deserializer);
            }
        });
        super.setupModule(context);
    }

    /**
     * Determines the version or return {@link Version#unknownVersion() unknownVersion} if this could not be determined.
     *
     * @param groupId    The group ID of the maven dependency to determine the version for.
     * @param artifactId The artifact ID of the maven dependency to determine the version for.
     * @return The version of the dependency.
     */
    private static Version determineVersion(String groupId, String artifactId) {
        Version version = Version.unknownVersion();
        MavenVersion mavenVersion = MavenVersion.forDependency(groupId, artifactId);
        if (mavenVersion != null) {
            version = new Version(mavenVersion.getMajor(), mavenVersion.getMinor(), mavenVersion.getIncrement(),
                    mavenVersion.getSuffix());
        }
        return version;
    }

    @Override
    public int hashCode() {
        return serializationMethod.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EnumerableModule
                && serializationMethod.equals(((EnumerableModule) other).serializationMethod));
    }

    /**
     * @return The module class name and the serializationMethod used.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{serializationMethod=" + serializationMethod + '}';
    }

}
