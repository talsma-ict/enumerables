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
package nl.talsmasoftware.enumerables.validation;

import nl.talsmasoftware.enumerables.constraints.IsOneOf;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Set;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sjoerd Talsma
 */
class IsOneOfCharSequenceValidatorTest {

    static class ValidatedObject {
        @IsOneOf({"Ferrari", "Aston martin"})
        public CharSequence brand;

        ValidatedObject(CharSequence brand) {
            this.brand = brand;
        }
    }

    static Locale oldDefaultLocale;
    Validator validator;
    Set<ConstraintViolation<ValidatedObject>> violations;

    @BeforeAll
    static void rememberOldDefaultLocale() {
        if (oldDefaultLocale == null) oldDefaultLocale = Locale.getDefault();
    }

    @AfterAll
    static void restoreOldDefaultLocale() {
        Locale.setDefault(oldDefaultLocale);
    }

    @BeforeEach
    void setUp() {
        Locale.setDefault(GERMAN); // Default to non-dutch or english to test.
        ClientLocaleHolder.set(ENGLISH);
        Configuration config = Validation.byDefaultProvider().configure();
        config = config.messageInterpolator(new ClientLocaleMessageInterpolator(config.getDefaultMessageInterpolator()));
        validator = config.buildValidatorFactory().getValidator();
    }


    @Test
    void testIsOneOfCharSeq_charsequence_null() {
        violations = validator.validate(new ValidatedObject(null));
        assertThat(violations).isEmpty();
    }

    @Test
    void testIsOneOfCharSeq_validString() {
        violations = validator.validate(new ValidatedObject("Ferrari"));
        assertThat(violations).isEmpty();
    }

    @Test
    void testIsOneOfCharSeq_otherString() {
        violations = validator.validate(new ValidatedObject("Lamborghini"));
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath()).hasToString("brand");
    }

    @Test
    void testIsOneOfCharSeq_ValidationMessage_i18n() {
        ClientLocaleHolder.set(ENGLISH);
        ConstraintViolation<ValidatedObject> violation = validator.validate(new ValidatedObject("Lamborghini")).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("is not one of [Ferrari, Aston martin]");

        ClientLocaleHolder.set(ClientLocaleHolder.DUTCH);
        violation = validator.validate(new ValidatedObject("Lamborghini")).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("is niet een waarde uit [Ferrari, Aston martin]");

        // Does fallback to default work as expected?
        ClientLocaleHolder.set(ClientLocaleHolder.FRISIAN);
        violation = validator.validate(new ValidatedObject("Lamborghini")).iterator().next();
        assertThat(violation.getMessage()).isEqualTo("is not one of [Ferrari, Aston martin]");
    }

}
