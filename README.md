# Z-- Compiler & Web Interface

Z-- is a JVM based language.
This repository includes both an online web interface, and a compiler.

The online web interface is done with [TeaVM](https://teavm.org)

The compiler is written in java.

# Web version limits
Due to how [TeaVM](https://teavm.org) works, some limits exist in web version.

```
//Supported calls

//java.lang.System
System.currentTimeMillis();
System.nanoTime();
System.getProperty();
System.clearProperty();

System.out;
System.err;

//java.lang.String
String.trim();
String.toString();
String.substring();
String.equals();
String.concat();
String.replace();

//java.io.PrintStream
PrintStream.println();
PrintSteram.print();
```

# Language Style

```
using java.util.List;
//imports

extend = List;
//super classes

implements[Runnable, Comparator];
//interfaces

static String a;
//fields

main {
    
}
//only for main method

static void method(String argument) {
    println(argument);
    //default functions
}
//method declaration
```
