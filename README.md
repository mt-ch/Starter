# Starter

Starter is an entry level general-purpose programming language for beginners to the subject. It is a Java-based, interoperated language that follows a procedural and declarative paradigm providing an easy model for beginners to understand.

Statements and expressions include, variables, loops, if-statements and function definitions are similarly composed to JavaScript, however syntax has been simplified for ease of learning.

Starter follows a static and strongly typed system by not allowing the computation of different types. In addtion, variables such as strings, integers, doubles, booleans and arrays can be changed and are all mutable, however immutable constant values can still be declared.

## Data Types

Starter supports the following data types:

- Integers
- Rational numbers
- Strings
- Booleans
- Arrays

Operations cannot be performed on mixed types to reduce error handling and increasing the simplicity of the code. Integers use the long type and rational numbers use the double type.

## Declarations and Definitions

Variables can be declared without a type which allows the user to not worry about assigning the correct type to each variable, however the strong type system does not allow the computation of two different types.

Arrays must be declared using the `store` and `[]` keywords, which can store a single defined data type and be referenced to using the index of the array name.

Functions are defined in a similar nature to JavaScript, using the `function` keyword followed by the specified parameters followed with a return statement with the block.

Variables defined at the top of the program have a global scope and can be accessed from anywhere in the program. In comparison, variables defined in functions or blocks can only be accessed with their defined scope and are removed from the block when finished.

The examples bellow demonstrate how regular definitions and declarations should be carried out:

```
store foo				// Declaring a variable 
store n = 21				// Defining a variable
store str = “bar”			// Defining does not require a type  
const pi = 3.14 			// Defining a constant
store status = true			// Defining a Boolean
fn sumAndCube (param1, param 2) { 	// Defining a function
	return (param1 + param2) ^ 3
	}
store Array[]				// Declaring an array
store Array[] = {“foo”, “bar”}		// Array definition using an implicit cap

store Array[]={“foo”,”bar”}	
print(Array[0])				//Accessing arrays
end
```

## Syntax

In Starter semi-colons are not required at the end of each line of code, to enable easier to read code and fewer errors based around this, however control structures like for loops use them for defining parameters.

Shorthand operators are similar to what is already found in JavaScript to provide already known operators that are familiar to the user, and also allow the use of existing code snippets.

Arithmetic operations can only use its specified type system and cannot be merged with another. This means an integer must be computed with an integer and will result in the same type.

Identifiers can be declared without a type and are globally accessible to the program when declared inside a control construct.

```
store v1 = 2		//Storing variables
store v2 = 8
v3 = “Hello world!”	//Variables can be stored without declaring type

v1 + v2			//Addition between two variables
v2 % v1			//Modulus of two variables
v2 ^ v1			//Power of two variables
			//Arithmetic calculations are restricted to int & double types
result = v2/v1 		//Variables can be made on the fly – globally accessible //Comparison expressions
if (result >= 3){
	print(result)	//Printing to the console
	}
end			//Terminate the program
```

Starter contains the basic control flow constructs as can be seen bellow, by using a simplified naming system and facilitating nested loops for more complex programs.

```
if (8 > 10){ 
	print (1)		//Example if/else statement
} else {
	print (2)
}
end
a=0 
while(a<5){			//While with nested loop inside of statement
	print(a)
	a=a+1
	loop(i=0; i<10; i=i+2){	//Nested for loop statement
		print(i) 
	}
} 
end
```

## Functions

Starter includes some useful built-in features, that can be utilised by the developer.

### read/input() - <read/input>()
Reads the next line from the user.

### random() – <random/rand> (min value, max value)
Returns a random rational number or integer in the range of the specified min and max arguments.

### sin/cos/tan() – <sin/cos/tan> (angle (degrees))
Returns the sin, cos or tan of the specified angle. Angles must be entered in degrees not radians.

## Examples

To run examples:

(For best results, run the shell script)

Option 1:

Navigate a terminal session to the root folder and run:
`runTests.sh`

Example:
`./runTests `

Options 2: 

Navigate a terminal session to inside of the folder "Starter" Run the following command: 
`java -classpath ./bin Starter < "[path to file]" `
					
Example:
`java -classpath ./bin Starter < "./Examples/arrays.starter # Starter`

This was a project for my Language Design module at univeristy. 

Thank you for reading!
