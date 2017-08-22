# Enumerable validation

Enumerable values can be validated using the [java validation API][validation-api].

## Available constraint annotations

The following annotations are defined in the `nl.talsmasoftware.enumerables.constraints` package
for validating `Enumerable` types:

### `@KnownValue`

This will result in `ConstraintViolations` if the `Enumerable` value is not one of the known constants.

### `@IsOneOf`

This will result in `ConstraintViolations` if the `Enumerable` value is not one of the values specified in the annotation.
The annotation accepts an optional parameter `caseSensitive=false` allowing a case-insensitive match with the provided
values.

**Note**: _The `@IsOneOf` constraint can just as easily be applied to any `CharSequence` property including `String`s.
The validation rules will be the same._

### Example

```java
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import nl.talsmasoftware.enumerables.constraints.KnownValue;

public class Car {    
    @KnownValue
    @IsOneOf(value = {"Ferrari", "Aston martin"}, caseSensitive = false)
    private CarBrand brand;

    public Car(CarBrand brand) {
        this.brand = brand;
    }
    
    public Set<ConstraintViolation<Car>> validateMe() {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(this);
    }
}
``` 

## Internationalization (i18n)

This module provides a `ValidationMessages` resource bundle with the following default messages
```
nl.talsmasoftware.enumerables.constraints.KnownValue.message=not a known constant for ${validatedValue.class.simpleName}
nl.talsmasoftware.enumerables.constraints.IsOneOf.message=is not one of {value}
```

Translations are provided for the following languages

- [English](src/main/resources/ValidationMessages_en.properties)
- [Dutch](src/main/resources/ValidationMessages_nl.properties)

We welcome additional translations; you may submit them through github as a new issue or (preferably) as a pull-request.

  [validation-api]: http://beanvalidation.org/
