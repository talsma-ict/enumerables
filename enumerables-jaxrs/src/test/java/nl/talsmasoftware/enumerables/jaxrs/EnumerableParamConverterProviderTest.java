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


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.ext.ParamConverter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sjoerd Talsma
 */
class EnumerableParamConverterProviderTest {

    EnumerableParamConverterProvider provider;

    @BeforeEach
    void setup() {
        provider = new EnumerableParamConverterProvider();
    }

    @Test
    void testNonEnumerableTypes() {
        assertThat(provider.getConverter(null, null, null)).isNull();
        assertThat(provider.getConverter(String.class, null, null)).isNull();
    }

    @Test
    void testProvideEnumerableParamConverter() {
        ParamConverter<TestEnumerable> converter = provider.getConverter(TestEnumerable.class, null, null);
        assertThat(converter).isInstanceOf(EnumerableParamConverter.class);
        assertThat(converter.fromString("2nd")).isSameAs(TestEnumerable.SECOND);
    }

}
