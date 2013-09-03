Simple LaTeX ANT Task
==============

Features:

 * Compile with pdfTeX as an ANT Task 
 * Configure the compilation via attributes in the ANT target (Partial implementation)
 * Support for BibTeX (Not yet implemented)

## Building and installing

With Maven is is fairly simple to build the source. We only need to provide the argument ```package```.

```
# In the root catalog
mvn package
````
If we would like to build without running all tests, that is also possible.

```
mvn package -Dmaven.test.skip=true
``` 
Once the build is complete, we can find the jar in `target/latexant-0.1.jar`. It is recommended to install it, copy the jar, to the library catalog in Ant's home directory `$ANT_HOME/libs`. In this way, we don`t have to specify the `classpath`. 

## Usage

The basics of the task will be defined here, togheter with a few examples.

### A simple task

First, this is the most simple way to execute the task. Makes use of default values.

```xml
<?xml version="1.0" encoding="utf-8"?>
<project name="MyProject" basedir="." default="pdf">

	<!-- First define the task. Let's call it latex -->
	<taskdef name="latex"
		classname="se.khalek.ant.LaTeXTask"
		<!-- If required -->
		classpath="path/to/jar"/>

	<!-- Compile a LaTeX document to PDF. -->
	<target name="pdf">
		<latex
			source="document.tex"
			pdfTex="true"/>
	</target>
</project>
```
