/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.enumerables.descriptions;

import static java.lang.Character.*;

/**
 * @author Sjoerd Talsma
 */
public final class Descriptions {

    /**
     * Private constructor to prevent instances of this static utility class.
     */
    private Descriptions() {
        throw new UnsupportedOperationException("Utility class may not be initialized.");
    }

    /**
     * Factory method to provide a reference to the default {@link DescriptionProvider}.
     *
     * @return The default <code>DescriptionProvider</code>
     */
    public static DescriptionProvider defaultProvider() {
        return DefaultDescriptionProvider.INSTANCE;
    }

    /**
     * Utility method to make the first character of a String an uppercase character.
     * The method was included here to avoid unncecessary dependencies to other (more complex) libraries.
     *
     * @param value The string to be capitalized.
     * @return The capitalized string with the first character as an uppercase character.
     */
    public static String capitalize(final String value) {
        if (value == null || value.length() == 0 || isTitleCase(value.charAt(0))) return value;
        final char[] chars = value.toCharArray();
        chars[0] = toTitleCase(chars[0]);
        return new String(chars);
    }

    /**
     * This method 'de-camelizes' a specified String value.
     * The method replaces each upper-case character that is preceded by a lowercase character by a space plus the
     * character converted into lowercase (unless the character is followed by another uppercase character).
     *
     * @param value The string value that needs to be decamelized.
     * @return
     */
    public static String decamelize(final String value) {
        if (value == null) return null;
        final int len = value.length();
        if (len > 1) {
            StringBuilder buf = null;
            for (int i = 1, j = i; i < len; i++, j++) {
                final char prev = value.charAt(i - 1);
                final char ch = value.charAt(i);
                if (isLowerCase(prev) && (isUpperCase(ch) || isTitleCase(ch))) {
                    buf = buf(buf, value, 1.3f).insert(j++, ' '); // regular 'cC' lower -> upper change.
                    if (i == len - 1 || !isUpperCase(value.charAt(i + 1))) buf.setCharAt(j, toLowerCase(ch));
                    // End-of-abbreviation is not yet common, this would for instance replace "JBoss" with "JB oss".
                    // } else if (i > 1 && isLowerCase(ch) && isUpperCase(prev) && isUpperCase(value.charAt(i - 2))) {
                    // buf = buf(buf, value, 1.3f).insert(j++, ' '); // end of abbreviation
                }
            }
            if (buf != null) return buf.toString();
        }
        return value;
    }

    private static StringBuilder buf(StringBuilder buf, String source, float factor) {
        return buf == null ? new StringBuilder((int) (factor * source.length())).append(source) : buf;
    }

}
