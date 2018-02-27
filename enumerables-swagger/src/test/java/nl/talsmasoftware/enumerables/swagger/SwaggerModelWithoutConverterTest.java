/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.enumerables.swagger;

import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SwaggerModelWithoutConverterTest {

    @Before
    public void setUp() {
        ModelConverters.getInstance().removeConverter(new EnumerableModelConverter());
    }

    @Test
    public void testSwaggerModelForCar() {
        Map<String, Model> swagger = ModelConverters.getInstance().readAll(Car.class);
        assertThat(swagger, hasKey("CarBrand"));
        Model brandModel = swagger.get("CarBrand");
        assertThat("CarBrand model", brandModel, notNullValue());
        assertThat(brandModel, instanceOf(ModelImpl.class));
        assertThat(((ModelImpl) brandModel).getType(), is("object"));
    }

}
