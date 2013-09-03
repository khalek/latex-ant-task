Simple LaTeX ANT Task
==============

Features:

 * Compile with pdfTeX as an ANT Task 
 * Configure the compilation via attributes in the ANT task
 * Support for BibTeX (Not yet implemented)

## Building and installing

With Maven it is fairly simple to build the source. We only need to provide one argument.

```
# In the root catalog
mvn package
```
If we would like to build without running all tests, that is also possible.

```
mvn package -Dmaven.test.skip=true
``` 
Once the build is complete, we can find the jar in `target/latexant-0.1.jar`. It is recommended to install it, copy the jar, to the library `libs` catalog in Ant's home directory `$ANT_HOME/libs` . In this way, we don't have to specify the `classpath`.

## Usage

The basics of the task will be defined here, together with a few examples.

### A simple task

First we will look at the most simple way to execute the task that makes use of default values. The only required attribute so far is `source`, which is the filename for the LaTeX document. Default is to not run `pdflatex` so to compile our document we have to set the attribute `pdftex` to ***true***.

```xml
<?xml version="1.0" encoding="utf-8"?>
<project name="MyProject" basedir="." default="pdf">

	<!-- First define the task. Let's call it latex -->
	<taskdef name="latex"
		classname="se.khalek.ant.LaTeX"
		classpath="path/to/jar"/>

	<!-- Compile a LaTeX document to PDF. -->
	<target name="pdf">
		<latex
			source="document.tex"
			pdftex="true"/>
	</target>
</project>
```
As we can see, the task first needs to be defined before we can use it. In the definition, we pass on the name of the Java class that implements the task, with the package name as prefix. In this example we also provide the path to the jar.

If we would like to clean after a compilation by perhaps removing some of the produced files, we can enable the attribute `clean`. The default files that will be removed is those with the file extensions .log or .aux. With the attribute `includes` we can choose our own files.

```xml
<target name="pdf">
	<latex
		source="document.tex"
		pdftex="true"
		clean="true"
		includes="*.log *.aux *.toc"/>
</target>
```
