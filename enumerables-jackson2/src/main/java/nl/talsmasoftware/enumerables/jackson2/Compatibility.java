/*
 * Copyright 2016-2022 Talsma ICT
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.type.SimpleType;
import nl.talsmasoftware.enumerables.Enumerable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

/**
 * Compatibility class to overcome API differences between various Jackson-2 versions.
 *
 * @author Sjoerd Talsma
 */
final class Compatibility {
    private static final Logger LOGGER = Logger.getLogger(Compatibility.class.getName());

    // Names of 'inclusions' where null values should be skipped.
    private static final Collection<String> SKIP_NULL_INCLUSIONS = unmodifiableCollection(asList(
            "NON_NULL", "NON_ABSENT", "NON_EMPTY"
    ));


    private static boolean supportsContextualType = true;
    private static boolean supportsTypeId = true;

    /**
     * {@link DeserializationContext#getContextualType()} exists since Jackson 2.5
     */
    static JavaType getContextualType(DeserializationContext ctxt) {
        if (supportsContextualType) try {
            return ctxt.getContextualType();
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "DeserializationContext.getContextualType() unavailable. Using Jackson < 2.5?", le);
            supportsContextualType = false;
        }
        return null;
    }

    /**
     * {@link JsonParser#getTypeId()} exists since Jackson 2.3
     */
    static Object getTypeId(JsonParser jp) throws IOException {
        if (supportsTypeId) try {
            return jp.getTypeId();
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "JsonParser.getTypeId() unavailable. Using Jackson < 2.3?", le);
            supportsTypeId = false;
        }
        return null;
    }

    static boolean mustIncludeNull(SerializationConfig config, Class<? extends Enumerable> enumerableType) {
        try { // getDefaultPropertyInclusion exists since Jackson 2.7
            JsonInclude.Value inclusion = config.getDefaultPropertyInclusion(enumerableType);
            if (inclusion != null) return !SKIP_NULL_INCLUSIONS.contains(inclusion.getValueInclusion().name());
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "SerializationConfig.getDefaultPropertyInclusion() unavailable. Using Jackson < 2.7?", le);
        }
        try {
            JsonInclude.Include inclusion = config.getSerializationInclusion();
            if (inclusion != null) return !SKIP_NULL_INCLUSIONS.contains(inclusion.name());
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "SerializationConfig.getSerializationInclusion() unavailable. Deprecation removed?", le);
        }
        return true;
    }

    static JavaType asJavaType(SerializationConfig config, Class<? extends Enumerable> enumerableType) {
        JavaType javaType = null;
        if (config != null && enumerableType != null) try {
            javaType = config.constructType(enumerableType);
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "SerializationConfig.constructType() unavailable.", le);
        } catch (RuntimeException rte) {
            if (LOGGER.isLoggable(Level.FINE)) LOGGER.log(Level.FINE,
                    "Exception constructing Jackson JavaType for " + enumerableType.getSimpleName() + ".", rte);
        }

        if (javaType == null) try {
            javaType = SimpleType.construct(enumerableType);
        } catch (LinkageError le) {
            LOGGER.log(Level.FINEST, "SimpleType.construct() unavailable. Deprecation removed?", le);
        }

        return javaType;
    }

    private static transient Version moduleVersion = null;

    static Version moduleVersion() {
        if (moduleVersion == null) {
            moduleVersion = mavenVersionFor("nl.talsmasoftware.enumerables", "enumerables-jackson2");
        }
        return moduleVersion;
    }

    /**
     * Deprecated method copied from Jackson VersionUtil as this is exactly what we want :)
     *
     * @param groupId    The maven groupId of this module.
     * @param artifactId The maven artifactId of this module.
     * @return The version of this maven artifact.
     */
    private static Version mavenVersionFor(/*ClassLoader cl,*/ String groupId, String artifactId) {
        String groupPath = groupId.replaceAll(Pattern.quote("."), "/");
        String resource = "/META-INF/maven/" + groupPath + '/' + artifactId + "/pom.properties";
        InputStream pomProperties = null;
        try {
            Properties props = new Properties();
            props.load(pomProperties = Compatibility.class.getResourceAsStream(resource));
            String versionStr = props.getProperty("version");
            String pomPropertiesArtifactId = props.getProperty("artifactId");
            String pomPropertiesGroupId = props.getProperty("groupId");
            return VersionUtil.parseVersion(versionStr, pomPropertiesGroupId, pomPropertiesArtifactId);
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Exception ignored while loading maven version.", e);
        } finally {
            _close(pomProperties);
        }
        return Version.unknownVersion();
    }

    private static void _close(Closeable closeable) {
        if (closeable != null) try {
            closeable.close();
        } catch (Exception closeEx) {
            LOGGER.log(Level.FINEST, "Exception ignored while closing resource.", closeEx);
        }
    }
}
