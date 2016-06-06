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
 */

package nl.talsmasoftware.enumerables.support.validation;

import nl.talsmasoftware.enumerables.Enumerable;

import javax.validation.ConstraintValidatorContext;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class IsOneOfEnumerablesValidator extends IsOneOfAbstractValidator<Enumerable> {

    public boolean isValid(Enumerable value, ConstraintValidatorContext context) {
        return value == null || isAccepted(value.getValue());
    }

}
