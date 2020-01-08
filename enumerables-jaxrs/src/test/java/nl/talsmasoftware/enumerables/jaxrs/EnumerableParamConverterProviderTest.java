/*
 * Copyright 2016-2020 Talsma ICT
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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * @author Sjoerd Talsma
 */
public class EnumerableParamConverterProviderTest {

    EnumerableParamConverterProvider provider;

    @Before
    public void setup() {
        provider = new EnumerableParamConverterProvider();
    }

    @Test
    public void testNonEnumerableTypes() {
        assertThat(provider.getConverter(null, null, null), is(nullValue()));
        assertThat(provider.getConverter(String.class, null, null), is(nullValue()));
    }

    @Test
    public void testProvideEnumerableParamConverter() {
        assertThat(provider.getConverter(TestEnumerable.class, null, null),
                is(instanceOf(EnumerableParamConverter.class)));
        assertThat(provider.getConverter(TestEnumerable.class, null, null).fromString("2nd"),
                is(sameInstance(TestEnumerable.SECOND)));
    }

}
