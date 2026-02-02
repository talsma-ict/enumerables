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
package nl.talsmasoftware.enumerables.jaxrs;

import nl.talsmasoftware.enumerables.Enumerable;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumerableParamConverterTest {
    static final TestEnumerable FOURTH = Enumerable.parse(TestEnumerable.class, "4th");

    @Test
    @SuppressWarnings("unchecked")
    void testConstructor_nullType() {
        assertThatThrownBy(() -> new EnumerableParamConverter(null))
                .hasMessageContaining("type is <null>");
    }

    @Test
    void testFromString() {
        EnumerableParamConverter<TestEnumerable> converter = new EnumerableParamConverter<TestEnumerable>(TestEnumerable.class);
        assertThat(converter.fromString("1st")).isSameAs(TestEnumerable.FIRST);
        assertThat(converter.fromString("2nd")).isSameAs(TestEnumerable.SECOND);
        assertThat(converter.fromString("3rd")).isSameAs(TestEnumerable.THIRD);
        assertThat(converter.fromString("4th")).isEqualTo(FOURTH);
        assertThat(converter.fromString(null)).isNull();
    }

    @Test
    void testToString() {
        EnumerableParamConverter<TestEnumerable> converter = new EnumerableParamConverter<TestEnumerable>(TestEnumerable.class);
        assertThat(converter.toString(TestEnumerable.FIRST)).isEqualTo("1st");
        assertThat(converter.toString(TestEnumerable.SECOND)).isEqualTo("2nd");
        assertThat(converter.toString(TestEnumerable.THIRD)).isEqualTo("3rd");
        assertThat(converter.toString(FOURTH)).isEqualTo("4th");
        assertThat(converter.toString(null)).isNull();
    }

    @Test
    void testForLoggingErrors() {
        Logger logger = Logger.getLogger(EnumerableParamConverter.class.getName());
        logger.setLevel(Level.FINEST);
        testFromString();
        testToString();
        logger.setLevel(Level.INFO);
    }

    @Test
    void testConverterToString() {
        assertThat(new EnumerableParamConverter<>(TestEnumerable.class))
                .hasToString("EnumerableParamConverter{%s}", TestEnumerable.class.getName());
    }
}
