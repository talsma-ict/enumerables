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

package nl.talsmasoftware.enumerables.descriptions;

import nl.talsmasoftware.enumerables.CarBrand;
import nl.talsmasoftware.enumerables.Enumerable;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Sjoerd Talsma
 */
public class DefaultDescriptionProviderTest {

    DescriptionProvider provider = DefaultDescriptionProvider.INSTANCE;

    private static Enumerable parse(String value) {
        return Enumerable.parse(CarBrand.class, value);
    }

    @Test
    public void testDescribe() {
        assertThat(provider.describe(null), is(nullValue()));
        assertThat(provider.describe(parse("")), is(""));
        assertThat(provider.describe(parse("simple")), is("Simple"));
        assertThat(provider.describe(parse("dashed-value")), is("Dashed-value"));
        assertThat(provider.describe(parse("underscored_value")), is("Underscored value"));
        assertThat(provider.describe(parse("CONSTANT_NAME")), is("Constant name"));
        assertThat(provider.describe(parse("CamelCase")), is("Camel case"));

        // Use constant name if available
        assertThat(provider.describe(parse("TESLA")), is("Tesla"));
        assertThat(provider.describe(parse("Tesla Motors")), is("Tesla"));
    }

}
