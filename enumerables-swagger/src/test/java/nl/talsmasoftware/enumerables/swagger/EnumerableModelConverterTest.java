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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;

public class EnumerableModelConverterTest {

    final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    final EnumerableModelConverter converter = new EnumerableModelConverter();

    @Before
    public void setUp() {
        ModelConverters.getInstance().addConverter(converter);
    }

    @After
    public void tearDown() {
        ModelConverters.getInstance().removeConverter(converter);
    }

    @Test
    public void testSwaggerModelForCar() throws JsonProcessingException, JSONException {
        // To test with example rendered as a JSON object:
//        ObjectMapper mapper = this.mapper.setConfig(this.mapper.getSerializationConfig()
//                .with(ContextAttributes.getEmpty()
//                        .withSharedAttribute(SerializationMethod.class.getName(), SerializationMethod.AS_OBJECT)));

        String expectedCarModel = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "    \"brand\": {\"$ref\": \"#/definitions/CarBrand\"}, " +
                "    \"type\": {\"type\": \"string\"}" +
                "}}";

        String expectedCarBrandModel = "{" +
                "\"type\": \"string\", " +
                "\"name\": \"CarBrand\", " +
                "\"simple\": true, " +
                "\"enum\": [\"Tesla\", \"Uniti Sweden\"], " +
                "\"description\": \"Known CarBrand values, although unknown values are also allowed.\", " +
                "\"example\": \"Tesla\", " +
                "\"externalDocs\": {\"description\": \"Enumerables\", \"url\": \"https://github.com/talsma-ict/enumerables\"}" +
                "}";

        Map<String, Model> swagger = ModelConverters.getInstance().readAll(Car.class);
        // System.out.println(mapper.writeValueAsString(swagger));

        assertThat(swagger, hasKey("Car"));
        JSONAssert.assertEquals(expectedCarModel, mapper.writeValueAsString(swagger.get("Car")), true);

        assertThat(swagger, hasKey("CarBrand"));
        assertThat(swagger.get("CarBrand"), instanceOf(EnumerableModel.class));
        JSONAssert.assertEquals(expectedCarBrandModel, mapper.writeValueAsString(swagger.get("CarBrand")), true);
    }

}
