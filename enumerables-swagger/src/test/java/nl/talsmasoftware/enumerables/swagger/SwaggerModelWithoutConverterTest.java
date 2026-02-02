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
package nl.talsmasoftware.enumerables.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerModelWithoutConverterTest {

    final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @BeforeEach
    @AfterEach
    void removeEnumerableModelConverter() {
        ModelConverters.getInstance().removeConverter(new EnumerableModelConverter());
    }

    @Test
    void testSwaggerModelForCar() throws JsonProcessingException, JSONException {
        String expectedCarModel = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "    \"brand\": {\"$ref\": \"#/definitions/CarBrand\"}, " +
                "    \"type\": {\"type\": \"string\"}" +
                "}}";

        String expectedCarBrandModel = "{" +
                "\"type\": \"object\", " +
                "\"properties\": {" +
                "    \"value\": {" +
                "        \"type\": \"string\"" +
                "    }" +
                "}}";

        Map<String, Model> swagger = ModelConverters.getInstance().readAll(Car.class);
        // System.out.println(mapper.writeValueAsString(swagger));

        assertThat(swagger).containsKey("Car");
        JSONAssert.assertEquals(expectedCarModel, mapper.writeValueAsString(swagger.get("Car")), false);

        assertThat(swagger).containsKey("CarBrand");
        JSONAssert.assertEquals(expectedCarBrandModel, mapper.writeValueAsString(swagger.get("CarBrand")), true);
    }

}
