[![Build Status][ci-img]][ci]
[![Coverage Status][coveralls-img]][coveralls]
[![Released Version][maven-img]][maven]

# Enumerables

Enumerables are similar to standard Java `Enum` types with the added ability 
to represent 'unknown' values.  
They are invaluable if you attempt to build a stable API.

## Background

The `Enumerable` superclass is **very** similar to a standard Java `Enum` type.  
However, it has a special feature that makes it suitable for using in an API you are maintaining.

Ever have an actual `Enum` returned in an API? Then have the customer come up with an additional value for
that `Enum` that should also be supported?  
\***Bang!**\* there goes your API compatibility.
You will either have to tel all your existing customers
_"sorry, the api is now broken"_ or create a new version 
**beside** the existing API and declare the old one deprecated.
However, you'll still have to think about how to represent 
the additional value in the old version or create a special 
exception for this case.  
Nasty in any conceivable scenario!  
What then.. _Strings_ ? But you want to share all those constant values you _do_ know in the API..

That is exactly the reason we've created the `Enumerable` type.
It is similar to `Enum` in that it represents a number of _known constants_ 
but also offers a possibility to represent _yet-unknown additional values_
by parsing them.  
In API terms, you make a slightly lighter promise to your customer:
I _currently_ know of these possible values which have meaning in the API, 
but please be prepared to receive any 'other' value as well (handle as you feel fit).
Those other values may actually even have meaning to the receiver when they are
introduced but allow for a stable API definition.
Introducing a new value in a new API release does not break the
existing contract since the consumer should have already anticipated this value.

## Example

Here's a working example of an `Enumerable` type:

```java
public final class CarBrand extends Enumerable {
    public static final CarBrand ASTON_MARTIN = new CarBrand("Aston martin");
    public static final CarBrand JAGUAR = new CarBrand("Jaguar");
    public static final CarBrand TESLA = new CarBrand("Tesla");
    
    // ... we all know there's more CarBrands than the ones we identified here, 
    // not a good fit for a java.lang.Enum, but suitable for Enumerable.

    private CarBrand(String value) { super(value); }
}
```

We can interact with `CarBrand` similar to `Enum`: 
 - `CarBrand[] knownCarBrands = Enumerable.values(CarBrand.class);`
 - `assert CarBrand.ASTON_MARTIN == Enumerable.valueOf(CarBrand.class, "ASTON_MARTIN");`
 - `assert CarBrand.JAGUAR.ordinal() == 1;`
 - `assert CarBrand.TESLA.name().equals("TESLA");`
 
But you can also:
 - `assert CarBrand.ASTON_MARTIN.getValue().equals("Aston martin");`
 - Parse another 'unknown' car brand: `CarBrand porsche = Enumerable.parse(CarBrand.class, "Porsche");`
 - Compare all `CarBrand` instances with each-other.  
   Constants sort in declaration order before 'unknown' values,
   which in turn sort alphabetically, case-insensitive.
 - Consistent with the sorting order, `ordinal()` of non-constants are _by definition_:  
   `assert porsche.ordinal() == Integer.MAX_VALUE;`
 - As they aren't defined by a constant, `name()` of non-constants _always_ returns `null`:  
   `assert porsche.name() == null;`

## Parsing / printing

```java
public static <E extends Enumerable> E parse(Class<E> type, CharSequence value);
```

The parse method first compares the given value with the values of all known constants 
for the specified `Enumerable` type. This results in a constant reference in most cases.
A new object instance is only created using the `String` constructor for non-constant values.

The counterpart of parsing, _printing_ is also covered:

```java
public static String print(Enumerable enumerable);
```

This simply returns `enumerable == null ? null : enumerable.getValue()`.

## Enum-like behaviour

### Enumerable.valueOf()

```java
public static <E extends Enumerable> E valueOf(Class<E> type, CharSequence name)
        throws ConstantNotFoundException;
```

This method is comparable with the `Enum.valueOf(Class, String)` method.

This method returns the constant with the specified `name`.
If there is no constant within the requested `type` found by that `name`,
the method will throw a `ConstantNotFoundException`.

Please note that this method looks at the constant `name` and **not** the String `value` 
of this enumerable object. To obtain an enumerable object from a specific `value`, 
please use the `parse(Class, CharSequence)` method instead.
That method will not throw any exceptions for yet-unknown values, but returns a
new enumerable instance containing the `value` instead.

### Enumerable.values()

```java
public static <E extends Enumerable> E[] values(Class<E> enumerableType);
```

This method is comparable with the `Enum.values()` method.

It finds all public constants of the requested `enumerableType`
that have been declared in the class of the enumerable itself.

These constants must be **public final static** fields of its own the `enumerableType`.

### Enumerable.ordinal()

```java
public final int ordinal();
```

Returns the 'enum ordinal' for an enumerable value.

For a constant value, this method returns the index of the constant within the `Enumerable.values()` array.

For non-constant values, this method will always return `Integer.MAX_VALUE`.
There are various reasons for this choice, but the most obvious one is that the `#compareTo(Enumerable)`
implementation is greatly influenced by this `ordinal` value, 
automatically sorting all constants before any non-constant parsed values.

### Enumerable.name()

```java
public final String name();
```

Returns the 'enum constant name' for an enumerable value.

For a constant value, this method returns the `name` of the defined constant as defined in the code (not its `value`).

For non-constant values, this method will always return `null`.

## Serialization / deserialization

*Serialization*: The `Enumerable` implements `Serializable`. 
This means that a concrete subclass is serializable if it does not contain 
any non-serializable and non-transient fields. 
Note that additional class fields should be either specified in the 
constant declaration or be deducable from the `String` constructor 
if they carry meaning after deserialization.

*Deserialization*: Similar to parsing, a deserialized `Enumerable` object 
resolves back to a listed constant reference if its value matches the constant.  
Only unanticipated values will result in new objects.

[//]: # (TODO: XML serialization)
[//]: # (TODO: Custom descriptions)


  [ci-img]: https://img.shields.io/travis/talsma-ict/enumerables/master.svg
  [ci]: https://travis-ci.org/talsma-ict/enumerables
  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22
  [coveralls-img]: https://coveralls.io/repos/github/talsma-ict/enumerables/badge.svg
  [coveralls]: https://coveralls.io/github/talsma-ict/enumerables