# OpenSCENARIO Expressions

# Simple Expression Support
OpenSCENARIO supports simple mathematical operation for evaluating parameters and literal values. Expressions are notated with the following syntax in XML-files.
```
${expr}
```
Example:
```xml
<Dimensions width="${$defaultWidth + 12.3}" length="4.2" height="1.6"/>
```

Examples for Expressions:
*	Expressions that contains number and floating point literals like 
    - ${-15 + 3.14 + 23 + 2.1E-7 + sqrt(9)}
*	Expressions that further contain parameter values like 
    - ${$defaultWidth +3} 
*	Expressions with +,-,*,% operators or sqrt function like 
    - ${$defaultWidth + 3/2}
    - ${4*3 -$counter} 
    - ${$numberOfExecutionCount%5}
    - ${sqrt(2* $acceleration * $distance)}
*	Expressions with unary minus: 
    - ${-$speed}
*	Nested expressions with brackets: 
    - ${($defaultWidth + 3)/2}
## Basic Design Policies
This fundamental list of requirements must be fullfilled.
* The use must be as intuitive as possible for the user.
* Operators are exclusively supported for numerical value datatypes. For int, unsingedInt, unsignedShort and double datatypes. Not for boolean, string or dateTime datatypes.
* The following operators are supported (in the order of operator precedence)
    - Square root function (sqrt)
    - Unary Minus, negating a number (-)
    - Multiply operator (*)
    - Division operator (/)
    - Modulo operator(%)
    - Minus operator (-)
    - Minus operator (+)
* Nested Expressions with brackets are supported.
* Explicit conversion is supported. 
    - ${(unsignedInt) $doubleValue}
    - ${(unsignedShort) $doubleValue}
    - ${(int) $doubleValue}
* Implicit conversion is applied where information loss is not an issue. 
* The operators are intentionally limited. Simple operations in a sceanrio file should be possible while complex calculations should not expressed in a scenario file but externally.
* Other mathematical operators/functions might be added in the future.

## General implementation restrictions
OpenSCENARIO may process safety critical data which must guarantee accurate calculation. Explicit conversion between data types that imply data loss must be detected.
Due to mathematical rules and notations, the implemented evaluation of expression must:
* follow the general rules for arithmetic operator precedence.
* detect arithmetic errors (Division by zero, sqrt of a negative value.)
* detect conversion errors like ${(unsignedShort) 100000} or ${(unsignedShort) -10}
* avoid internal arithmetic overflow or abort with an error.

## Avoiding internal arithemtic overflow
The internal datatypes must ensure, that arithmetic overflow is avoided. This may most efficiently and safely achieved when using 64-byte values for internal calculation for integer numbers and using 64 byte double values for floating point value.
It is recommended that int, unsingedInt, unsignedShort values are converted into 64 byte integer values for internal calculations.
Any arithmetic overflow must be avoided or must issue an error for internal calculation. When internal limits (e.g. 64 byte limits for integer values) are reached, the calculation must abort with an error.

E.g. If 64 byte long values are used for internal calculation the expression of the next example must abort with an error, since the limit for 64 byte long values is exceeded.

```
${-2147483648 * -2147483648 * -2147483648}
```
E.g. with an error : ```"Internal Overflow (limits of internal 64 bit integer value exceeded)"```

In this way, any internal arithmetic overflow is detected.

## Conversion policies
Implicit conversion is not a problem when double and 64 byte datatypes are used for internal calculation. 
* Any integer datatype (int, unsingedInt, unsignedShort) can be safely converted to an 64 byte integer value. 
* Any integer datatype (int, unsingedInt, unsignedShort) datatype can be safely converted to a 64 byte double value.

### Information loss on conversion
Conversion from double to an integer value type (int, unsingedInt, unsignedShort) is done by cutting off the digits after the decimal separator (3.45 => 3, -3.54 => -3).
As there is loss of information when converting from double to an integer datatype, this conversion must be explicit.

E.g. the expression in this example must issue an error (e.g. "Floating point values must be explicitly casted. Use (int), (unsignedInt) or (unsignedShort) for explicit cast.")

```xml
<ParameterDeclaration parameterType="unsignedInt" name="numberOfExecutions" value="${34+3.45}"/>
```
This is the right way to do explicit conversion.

```xml
<ParameterDeclaration parameterType="unsignedInt" name="numberOfExecutions" value="${(unsignedInt) (34+3.45)}"/>
```

# About this implementation
This is a test implementation as a proof of concept for the expression language. It includes basic tests as well as the possibility to include test descriptions and test them with a command line tool.

The project is in incubation status and is not intended to be included in production mode projects.

# Building the sources
## JAVA
* Clone the master branch (the `java` path from this project)
* Install maven
* Make sure that the maven binary directory is set in your environment
* Make sure that JAVA_HOME is set and points to a jdk (version 8 or above).
* Change your current dir to the `java` directory.
* Execute `mvn install`

Three packages are built in the `target` folder.

| package | description |
|-|-|
| de.rac.openscenario.expr-X.Y.Z.jar | A package that includes the compiled source code |
| de.rac.openscenario.expr-X.Y.Z-jar-with-dependencies.jar | A package that includes the compiled source code with any dependency embedded. |

# Executing the tester
The binaries may either be build with maven or downloaded [here](https://github.com/ahege/net.asam.openscenario.expr/tree/master/download):

The tester tools checks both:

* expressions against their expected values.
* expressions against their expected errors.

The file format should be self-explaining Please see [this example](https://github.com/ahege/net.asam.openscenario.expr/blob/master/doc/examples/testDefinitions.json)

## Executing the standalone tool
The main class (net.asam.openscenario.expr.TestExprMain) is already set. Start with:

`java -jar net.asam.openscenario.expr-X.Y.Z-jar-with-dependencies.jar`

```
Usage: <filename>
```  
## Checking a file
When checking a file, any defined test int the file is executed (identified by its id). Sucess or error in issued on the command line.

`java -jar net.asam.openscenario.expr-X.Y.Z-jar-with-dependencies.jar ./testDefinitions.json`    

```
Checking 'C:\temp\testDefinitions.json'
Test 0 successful.
Test 1 successful.
Test 2 successful.
Test 3 successful.  
```
## Errors
There are three categories of violations:

1. Violation against expected values
2. Violation against expected errors
3. Unexpected errors

### Expected values
When the result does not match the expected value, an error is issued:

```json
{
 "id" : 3,
 "expr": "${4+6*5}",
 "expectedValue": 35
}
```
The following error is issued:

```
Error in test 3
Expected Value: 35
Actual value: 34
```
### Expected error
When the expected error does not occur, an error is issued:

Either no error occurs (obviously no Error occurs here):

```json
{
 "id" : 4,
 "expr": "${5/4}",
 "expectedError": {
	"message": "Division by zero",
	"column": 2
	}
}
```
The following error is issued:
```
Error in test 4
Expected error: Division by zero
```

Or when another error occurs (obviously the wrong error is expected):
```json
{
 "id" : 4,
 "expr": "${(unsignedShort) 66000}",
 "expectedError": {
	"message": "Division by zero",
	"column": 2
	}
}
```
The following error is issued:
```
Expected error: Division by zero
Actual error: Value '66000' cannot be converted to type 'unsignedShort'

```

### Unexpected error
If an unexpected error occurs, this is also issued:
```json
{
 "id" : 4,
 "expr": "${(unsignedShort) 66000}",
 "expectedValue": 66000
}
```
The following error is issued:
```
Error in test 4
Value '66000' cannot be converted to type 'unsignedShort'(2)
```

