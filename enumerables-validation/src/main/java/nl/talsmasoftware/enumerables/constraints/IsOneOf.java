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
package nl.talsmasoftware.enumerables.constraints;

import nl.talsmasoftware.enumerables.validation.IsOneOfCharSequencesValidator;
import nl.talsmasoftware.enumerables.validation.IsOneOfEnumerablesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint that can be used to allow only a subset of known values for an Enumerable object. Given the nature of this
 * check, it may also be used to validate {@link CharSequence} objects with.
 * <p>
 * Supported types are all subclasses of:
 * <ul>
 * <li>{@link nl.talsmasoftware.enumerables.Enumerable Enumerable}, or
 * <li>{@link CharSequence}
 * </ul>
 * <p>
 * The constraint allows specifying whether or not the matching should be done case sensitive.
 *
 * @author Sjoerd Talsma
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsOneOfEnumerablesValidator.class, IsOneOfCharSequencesValidator.class})
public @interface IsOneOf {

    /**
     * @return The message to use if this constraint is violated.
     */
    String message() default "{nl.talsmasoftware.enumerables.constraints.IsOneOf.message}";

    /**
     * @return The array of values that are accepted by this constraint.
     */
    String[] value() default {};

    /**
     * @return Whether the matching for the accepted values should be done case-sensitive, defaults to <code>true</code>.
     */
    boolean caseSensitive() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}