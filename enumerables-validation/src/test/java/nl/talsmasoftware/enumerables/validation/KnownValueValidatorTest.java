/*
 * Copyright 2016-2017 Talsma ICT
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
package nl.talsmasoftware.enumerables.validation;

import nl.talsmasoftware.enumerables.constraints.KnownValue;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Set;

import static java.util.Locale.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Sjoerd Talsma
 */
public class KnownValueValidatorTest {

    static class ValidatedObject {
        @KnownValue
        CarBrand brand;

        ValidatedObject(CarBrand brand) {
            this.brand = brand;
        }
    }

    static class StandardMessagesObject {
        @NotNull
        String testedProperty;
    }

    Validator validator;
    Set<ConstraintViolation<ValidatedObject>> violations;

    @BeforeClass
    public static void rememberOldDefaultLocale() {
        IsOneOfCharSequenceValidatorTest.rememberOldDefaultLocale();
    }

    @AfterClass
    public static void restoreOldDefaultLocale() {
        IsOneOfCharSequenceValidatorTest.restoreOldDefaultLocale();
    }

    @Before
    public void setUp() {
        Locale.setDefault(GERMAN); // Default to non-dutch or english to test.
        ClientLocaleHolder.set(ENGLISH);
        Configuration config = Validation.byDefaultProvider().configure();
        config = config.messageInterpolator(new ClientLocaleMessageInterpolator(config.getDefaultMessageInterpolator()));
        validator = config.buildValidatorFactory().getValidator();
    }

    @Test
    public void testKnownValue_enumerable_null() {
        violations = validator.validate(new ValidatedObject(null));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testKnownValue_validEnumerable() {
        violations = validator.validate(new ValidatedObject(CarBrand.FERRARI));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testKnownValue_otherEnumerable() {
        ClientLocaleHolder.set(ENGLISH);
        violations = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand")));
        assertThat(violations, hasSize(1));
        assertThat(violations.iterator().next().getPropertyPath(), hasToString("brand"));
    }

    @Test
    public void testKnownValue_ValidationMessage_i18n() {
        ClientLocaleHolder.set(ENGLISH);
        ConstraintViolation<ValidatedObject> violation = validator
                .validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand"))).iterator().next();
        assertThat(violation.getMessage(), equalTo("not a known constant for CarBrand"));

        ClientLocaleHolder.set(ClientLocaleHolder.DUTCH);
        violation = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Onbekend merk"))).iterator().next();
        assertThat(violation.getMessage(), equalTo("geen bekende constante voor CarBrand"));

        // Does fallback to default work as expected?
        ClientLocaleHolder.set(ClientLocaleHolder.FRISIAN);
        violation = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand"))).iterator().next();
        assertThat(violation.getMessage(), equalTo("not a known constant for CarBrand"));
    }

    @Test
    public void testStandardMessages_i18n() {
        ClientLocaleHolder.set(ENGLISH);
        ConstraintViolation<StandardMessagesObject> violation = validator.validate(new StandardMessagesObject()).iterator().next();
        assertThat(violation.getMessage(), equalTo("must not be null"));

        ClientLocaleHolder.set(FRENCH);
        violation = validator.validate(new StandardMessagesObject()).iterator().next();
        assertThat(violation.getMessage(), equalTo("ne peut pas \u00EAtre nul"));
    }

}
