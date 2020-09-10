# OpenSCENARIO Expressions

# Simple Expression Support
OpenSCEANRIO supports simple mathematical operation for evaluating parameters and literal values. Expressions are notated with the following syntax in XML-files.
```
${expr}
```
Example:
```xml
<Dimensions width="${$defaultWidth + 12.3}" length="4.2" height="1.6"/>
```

Examples for Expressions:
*	Expressions that contains number and floating point literals like 
    - ${-15 + 3.14 + 23 + 2.1E-7 }
*	Expressions that further contain parameter values like 
    - ${$defaultWidth +3} 
*	Expressions with +,-,*,% operators like 
    - 	${$defaultWidth + 3/2}
    -  ${4*3 -$counter} 
    - ${$numberOfExecutionCount%5}
*	Expressions with unary minus: 
    - ${-$speed}
*	Nested Expressions with brackets: 
    - ${($defaultWidth + 3)/2}
## Basic Design Policies
This fundamental list of requirements must be fullfilled.
*	The use must be as intuitive as possible for the user.
*	Operators are exclusively supported for numerical value datatypes. For int, unsingedInt, unsignedShort and double datatypes. Not for boolean, string or dateTime datatypes.
*	The following operators are supported (in the order of operator precedence)
    - Unary Minus, negating a number (-)
    - Multiply operator (*)
    - Division operator (/)
    - Modulo Operator(%)
    - Minus Operator (-)
    - Minus Operator (+)
*	Nested Expressions with brackets are supported.
*	Explicit conversion is supported. Implicit conversion is applied where information loss is not an issue. 
*	The operators are intentionally limited. Simple operations in a sceanrio file should be possible while complex calculations should not expressed in a scenario file but externally.
## General Implementation Restrictions
OpenSCENARIO may process safety critical data which must guarantee accurate calculation. Explicit conversion between data types that imply data loss must be detected.
Due to mathematical rules and notations, the implemented evaluation of expression must:
*	follow the general rules for arithmetic operator precedence.
*	detect arithmetic errors (Division by zero)
*	detect conversion errors like ${(unsignedShort) 100000} or ${(unsignedShort) -10}
*	avoid arithmetic overflow.

# About this implementation
This is a test implementation as a prrof of concept for the expression language. It includes basic tests as well as the possibility to include test descriptions and test them with a command line tool.

The project is in incubation status and is not intended to be included in production mode projects.

# Building the Sources
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
| de.rac.openscenario.expr-X.Y.Z-javadoc.jar | The javadoc documentation|

# Executing the TestMain
The checker tools checks both:

* expressions against their expected values
* expressions against their expected errors.

The file format should be self-explaining Please see [this example](https://github.com/ahege/net.asam.openscenario.expr/blob/master/doc/examples/testDefinitions.json)

## Executing the standalone tool
The main class (net.asam.openscenario.expr.TestExprMain) is already set. Start with:

`java -jar net.asam.openscenario.expr-X.Y.Z-jar-with-dependencies.jar`

```
Usage: <filename>
```  
## Checking a File
When checking a file, any defined test int the file is executed (identified by its id). Sucess or error in issued on the command line.

`java -jar net.asam.openscenario.expr-X.Y.Z-jar-with-dependencies.jar -i ./testDefinitions.json`    

```
Checking 'C:\temp\testDefinitions.json'
Test 0 successful.
Test 1 successful.
Test 2 successful.
Test 3 successful.  
```
## Errors
Violations against expected values are issued:

For Test definion
```json
{
	"id" : 3,
	"expr": "${4+6*5}",
	"expectedValue": 35
}
```

```
Checking 'C:\temp\testDefinitions.json'
Test 0 successful.

Test 1 successful.

Test 2 successful.

Error in test 3
Expected Value: 35
Actual value: 34
```
