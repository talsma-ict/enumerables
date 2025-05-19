/*
 * Copyright 2016-2025 Talsma ICT
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
package nl.talsmasoftware.enumerables.jakarta.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import nl.talsmasoftware.enumerables.jakarta.validation.constraints.KnownValue;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static java.util.Locale.GERMAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sjoerd Talsma
 */
class KnownValueValidatorTest {

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

    @BeforeAll
    static void rememberOldDefaultLocale() {
        IsOneOfCharSequenceValidatorTest.rememberOldDefaultLocale();
    }

    @AfterAll
    static void restoreOldDefaultLocale() {
        IsOneOfCharSequenceValidatorTest.restoreOldDefaultLocale();
    }

    @BeforeEach
    void setUp() {
        Locale.setDefault(GERMAN); // Default to non-dutch or english to test.
        ClientLocaleHolder.set(ENGLISH);
        validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ClientLocaleMessageInterpolator(new ParameterMessageInterpolator()))
                .buildValidatorFactory()
                .getValidator();
    }

    @Test
    void testKnownValue_enumerable_null() {
        violations = validator.validate(new ValidatedObject(null));
        assertThat(violations).isEmpty();
    }

    @Test
    void testKnownValue_validEnumerable() {
        violations = validator.validate(new ValidatedObject(CarBrand.FERRARI));
        assertThat(violations).isEmpty();
    }

    @Test
    void testKnownValue_otherEnumerable() {
        ClientLocaleHolder.set(ENGLISH);
        violations = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand")));
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("brand");
    }

    @Test
    void testKnownValue_ValidationMessage_i18n() {
        ClientLocaleHolder.set(ENGLISH);
        ConstraintViolation<ValidatedObject> violation = validator
                .validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand"))).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("not a known constant value");

        ClientLocaleHolder.set(ClientLocaleHolder.DUTCH);
        violation = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Onbekend merk"))).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("geen bekende constante waarde");

        // Does fallback to default work as expected?
        ClientLocaleHolder.set(ClientLocaleHolder.FRISIAN);
        violation = validator.validate(new ValidatedObject(CarBrand.parseLeniently("Unknown brand"))).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("not a known constant value");
    }

    @Test
    void testStandardMessages_i18n() {
        final String englishNonNullMessage = "must not be null";
        ClientLocaleHolder.set(ENGLISH);
        ConstraintViolation<StandardMessagesObject> violation = validator.validate(new StandardMessagesObject()).iterator().next();
        assertThat(violation.getMessage()).isEqualTo(englishNonNullMessage);

        ClientLocaleHolder.set(FRENCH);
        violation = validator.validate(new StandardMessagesObject()).iterator().next();
        assertThat(violation.getMessage()).isNotEqualTo(englishNonNullMessage);
    }

}
