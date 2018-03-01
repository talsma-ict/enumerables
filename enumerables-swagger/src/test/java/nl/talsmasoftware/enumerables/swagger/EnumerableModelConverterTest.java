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
import org.junit.*;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Locale;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

public class EnumerableModelConverterTest {

    final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    final EnumerableModelConverter converter = new EnumerableModelConverter();

    static Locale previousDefault;

    @BeforeClass
    public static void captureDefaultLocale() {
        previousDefault = Locale.getDefault();
    }

    @AfterClass
    public static void restoreDefaultLocale() {
        Locale.setDefault(previousDefault);
    }

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
        Locale.setDefault(Locale.ENGLISH);
        String expectedCarModel = "{" +
                "\"type\": \"object\"," +
                "\"properties\": {" +
                "    \"brand\": {\"$ref\": \"#/definitions/CarBrand\"}, " +
                "    \"type\": {\"type\": \"string\"}" +
                "}}";

        String expectedCarBrandModel = "{" +
                "\"type\": \"string\", " +
                "\"format\": \"enumerable\", " +
                "\"description\": \"CarBrand with known values [Tesla, Uniti Sweden]\", " +
                "\"externalDocs\": {\"description\": \"Enumerables\", \"url\": \"https://github.com/talsma-ict/enumerables\"}" +
                "}";

        Map<String, Model> swagger = ModelConverters.getInstance().readAll(Car.class);
        // System.out.println(mapper.writeValueAsString(swagger));

        assertThat(swagger, hasKey("Car"));
        JSONAssert.assertEquals(expectedCarModel, mapper.writeValueAsString(swagger.get("Car")), true);

        assertThat(swagger, hasKey("CarBrand"));
        JSONAssert.assertEquals(expectedCarBrandModel, mapper.writeValueAsString(swagger.get("CarBrand")), true);
    }

}
