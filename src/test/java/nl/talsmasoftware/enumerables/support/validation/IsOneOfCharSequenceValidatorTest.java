/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *
 */

package nl.talsmasoftware.enumerables.support.validation;

import nl.talsmasoftware.enumerables.constraints.IsOneOf;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IsOneOfCharSequenceValidatorTest {
    static class ValidatedObject {
        @IsOneOf({"Ferrari", "Aston martin"})
        public String brand;

        ValidatedObject(String brand) {
            this.brand = brand;
        }
    }

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<ValidatedObject>> violations;

    @Test
    public void testIsOneOf_charsequence_null() {
        violations = validator.validate(new ValidatedObject(null));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testIsOneOf_validString() {
        violations = validator.validate(new ValidatedObject("Ferrari"));
        assertThat(violations, is(empty()));
    }

    @Test
    public void testIsOneOf_otherString() {
        violations = validator.validate(new ValidatedObject("Lamborghini"));
        assertThat(violations, hasSize(1));
        // assertThat(violations.iterator().next().getMessage(), equalTo("bla")); // not yet
    }

}
