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
package nl.talsmasoftware.enumerables.jaxrs;

import nl.talsmasoftware.enumerables.Enumerable;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.fail;

public class EnumerableParamConverterTest {
    private static final TestEnumerable FOURTH = Enumerable.parse(TestEnumerable.class, "4th");

    @Test
    @SuppressWarnings("unchecked")
    public void testConstructor_nullType() {
        try {
            new EnumerableParamConverter(null);
            fail("Exception expected");
        } catch (RuntimeException expected) {
            assertThat(expected, hasToString(containsString("type is <null>")));
        }
    }

    @Test
    public void testFromString() {
        EnumerableParamConverter<TestEnumerable> converter = new EnumerableParamConverter<TestEnumerable>(TestEnumerable.class);
        assertThat(converter.fromString("1st"), is(sameInstance(TestEnumerable.FIRST)));
        assertThat(converter.fromString("2nd"), is(sameInstance(TestEnumerable.SECOND)));
        assertThat(converter.fromString("3rd"), is(sameInstance(TestEnumerable.THIRD)));
        assertThat(converter.fromString("4th"), is(equalTo(FOURTH)));
        assertThat(converter.fromString(null), is(nullValue()));
    }

    @Test
    public void testToString() {
        EnumerableParamConverter<TestEnumerable> converter = new EnumerableParamConverter<TestEnumerable>(TestEnumerable.class);
        assertThat(converter.toString(TestEnumerable.FIRST), is(equalTo("1st")));
        assertThat(converter.toString(TestEnumerable.SECOND), is(equalTo("2nd")));
        assertThat(converter.toString(TestEnumerable.THIRD), is(equalTo("3rd")));
        assertThat(converter.toString(FOURTH), is(equalTo("4th")));
        assertThat(converter.toString(null), is(nullValue()));
    }

    @Test
    public void testForLoggingErrors() {
        Logger logger = Logger.getLogger(EnumerableParamConverter.class.getName());
        logger.setLevel(Level.FINEST);
        testFromString();
        testToString();
        logger.setLevel(Level.INFO);
    }

    @Test
    public void testConverterToString() {
        assertThat(new EnumerableParamConverter<TestEnumerable>(TestEnumerable.class),
                hasToString(stringContainsInOrder(asList("EnumerableParamConverter", "TestEnumerable"))));
    }
}
