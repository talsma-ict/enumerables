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

package nl.talsmasoftware.enumerables.support.maven;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to determine the version of this library (and other dependencies) with.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public final class MavenVersion implements Comparable<MavenVersion>, Serializable {
    private static final Logger LOGGER = Logger.getLogger(MavenVersion.class.getName());

    /**
     * Regular expression pattern to parse the maven version with.
     */
    private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)([\\.-](\\d+)([\\.-](\\d+))?)?([\\.-](\\S+))?\\s*");

    private final int major;
    private final Integer minor, increment;
    private final String suffix;

    private MavenVersion(int major, Integer minor, Integer increment, String suffix) {
        this.major = major;
        this.minor = minor;
        this.increment = increment;
        this.suffix = suffix;
    }

    /**
     * This method returns the version of the specified Maven dependency, based on its <code>groupId</code> and
     * <code>artifactId</code>. This will be read from the bundled <code>META-INF</code> data from the specified
     * library within the classpath.
     *
     * @param groupId    The maven group ID of the requested dependency version.
     * @param artifactId The maven artifact ID of the requested dependency version.
     * @return The version of the requested dependency or <code>null</code> if this could not be determined.
     */
    public static MavenVersion forDependency(String groupId, String artifactId) {
        final String resource = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties";
        InputStream stream = null;
        try {
            Properties properties = new Properties();
            properties.load(stream = MavenVersion.class.getResourceAsStream(resource));
            return parse(properties.getProperty("version"));
        } catch (IOException ioe) {
            LOGGER.log(Level.FINEST, "Could not open stream to \"{0}\".", new Object[]{resource, ioe});
        } catch (RuntimeException rte) {
            LOGGER.log(Level.FINEST, "Error reading from \"{0}\".", new Object[]{resource, rte});
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    LOGGER.log(Level.FINEST, "Could not close stream to \"{0}\".", new Object[]{resource, ioe});
                }
            }
        }
        return null;
    }

    /**
     * This operation tries to parse a character sequence as a version object.
     * A numerical 'major' version is required and 'major.minor' or 'major.minor.increment' are optional components.
     * Additional '-SNAPSHOT' information is also allowed after the numeric part of the version.
     *
     * @param version The version to be parsed.
     * @return The version as a 'major.minor.increment' or <code>null</code> if it could not be parsed.
     */
    public static MavenVersion parse(CharSequence version) {
        if (version != null && version.length() > 0) {
            final Matcher matcher = PATTERN.matcher(version);
            if (matcher.matches()) {
                int groupCount = matcher.groupCount();
                String g0 = matcher.group(0);
                String g1 = matcher.group(1);
                String g2 = matcher.group(2);
                String g3 = matcher.group(3);
                String g4 = matcher.group(4);
                String g5 = matcher.group(5);
                String g6 = matcher.group(6);
                String g7 = matcher.group(7);
                return new MavenVersion(parseInt(matcher.group(1), 0),
                        parseInt(matcher.group(3), null),
                        parseInt(matcher.group(5), null),
                        matcher.group(7));
            } else {
                LOGGER.log(Level.FINE, "Could not parse version: \"{0}\".", version);
            }
        }
        return null;
    }

    private static Integer parseInt(String value, Integer defaultValue) {
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    /**
     * @return The major version number
     */
    public int getMajor() {
        return major;
    }

    /**
     * @return The minor version number or zero (<code>0</code>) if unspecified.
     */
    public int getMinor() {
        return minor != null ? minor : 0;
    }

    /**
     * @return The increment version number or zero (<code>0</code>) if unspecified.
     */
    public int getIncrement() {
        return increment != null ? increment : 0;
    }

    /**
     * @return The suffix to the version (e.g. <code>"SNAPSHOT"</code>) or <code>null</code> if unspecified.
     */
    public String getSuffix() {
        return suffix;
    }

    public int compareTo(MavenVersion other) {
        if (other == null) throw new IllegalArgumentException("Cannot compare version to null!");
        int delta = major - other.major;
        if (delta == 0) {
            delta = getMinor() - other.getMinor();
            if (delta == 0) {
                delta = getIncrement() - other.getIncrement();
                if (delta == 0) {
                    delta = suffix == null ? (other.suffix == null ? 0 : 1)
                            : other.suffix == null ? -1 : suffix.compareTo(other.suffix);
                }
            }
        }
        return Integer.signum(delta);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 * major) + getMinor() + getIncrement()) + (suffix != null ? suffix.hashCode() : 0);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof MavenVersion
                && compareTo((MavenVersion) other) == 0);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append(major);
        if (minor != null) result.append('.').append(minor);
        if (increment != null) result.append('.').append(increment);
        if (suffix != null) result.append('-').append(suffix);
        return result.toString();
    }

}
