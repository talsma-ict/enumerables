[![Build Status][ci-img]][ci]
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

## Parsing

The parsing functionality first compares the given value with all known constants 
of the specified `Enumerable` type, so normally you will get a constant reference back.
A new object instance is only created for non-constant values.

## Enum-like behaviour

### Enumerable.valueOf()

_Documentation coming when I get some time_

### Enumerable.values()

_Documentation coming when I get some time_

### Enumerable.ordinal()

_Documentation coming when I get some time_

### Enumerable.name()

_Documentation coming when I get some time_

## Serialization / deserialization

Similar to parsing, a deserialized `Enumerable` object normally resolves back to the
listed constant reference if it is recognized.
Only unanticipated values will result in new objects.

## JSON

_Documentation coming when I get some time_

## XML

_Documentation coming when I get some time_

## Descriptions

_Documentation coming when I get some time_

[//]: # (TODO: Enum.values)
[//]: # (TODO: Enum.ordinal)
[//]: # (TODO: other enum concepts)
[//]: # (TODO: Document an example on how to use it)


  [ci-img]: https://img.shields.io/travis/talsma-ict/enumerables/master.svg
  [ci]: https://travis-ci.org/talsma-ict/enumerables
  [maven-img]: https://img.shields.io/maven-central/v/nl.talsmasoftware.enumerables/enumerables.svg
  [maven]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22nl.talsmasoftware.enumerables%22
