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
package nl.talsmasoftware.enumerables.support.json.jackson2;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.json.SerializationMethod;
import nl.talsmasoftware.enumerables.support.maven.MavenVersion;

import static nl.talsmasoftware.enumerables.support.json.SerializationMethod.PLAIN_STRING;

/**
 * Mapping module for converting one or more {@link Enumerable} types from and to JSON (or YAML) using the Jackson (v2)
 * library.
 * <p>
 * If a non-<code>null</code> {@link SerializationMethod} is specified to the module, that will be used for serializing
 * {@link Enumerable} objects. Otherwise, {@link SerializationMethod#PLAIN_STRING plain-String} serialization will be
 * used by default.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */

public class EnumerableModule extends SimpleModule {

    private final SerializationMethod serializationMethod;
    private final EnumerableDeserializer enumerableDeserializer = new EnumerableDeserializer();

    /**
     * Constructor for a Jackson (v2) module to serialize and deserialize {@link Enumerable} objects to and from
     * plain JSON string values.
     */
    public EnumerableModule() {
        this(null);
    }

    /**
     * Constructor for a Jackson (v2) module to serialize and deserialize {@link Enumerable} objects to and from
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
        super.addSerializer(Enumerable.class, new EnumerableSerializer(this.serializationMethod));
        super.addDeserializer(Enumerable.class, enumerableDeserializer);
    }

    private static boolean isEnumerableSubtype(BeanDescription beanDesc) {
        Class<? extends Enumerable> beanType = EnumerableDeserializer.asEnumerableSubclass(beanDesc);
        return beanType != null && !Enumerable.class.equals(beanType);
    }

    /**
     * Configures the Jackson module.
     * <p>
     * This configures a {@link BeanDeserializerModifier} that will return the configured deserializer from this
     * module for <em>any</em> subtype of {@link Enumerable}.
     *
     * @param setupContext De setup context to initialize with.
     *                     The Enumerable deserializer will be selected for any subtype of Enumerable.
     */
    @Override
    public void setupModule(final SetupContext setupContext) {
        if (setupContext != null) {
            setupContext.addBeanDeserializerModifier(new BeanDeserializerModifier() {
                @Override
                public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                    return EnumerableDeserializer.asEnumerableSubclass(beanDesc) != null ? enumerableDeserializer
                            : super.modifyDeserializer(config, beanDesc, deserializer);
                }
            });
        }
        super.setupModule(setupContext);
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
                    mavenVersion.getSuffix(), groupId, artifactId);
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
