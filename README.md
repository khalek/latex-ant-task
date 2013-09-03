Simple LaTeX ANT Task
==============

Features:

 * Compile with pdfTeX as an ANT Task 
 * Configure the compilation via attributes in the ANT target (Partial implementation)
 * Support for BibTeX (Not yet implemented)

## Building and installing

## Usage

### A simple task

First, this is the most simple way to execute the task. Makes use of default values.

```xml
<?xml version="1.0" encoding="utf-8"?>
<project name="MyProject" basedir="." default="pdf">

	<!-- First define the task. Let's call it latex -->
	<taskdef name="latex"
		classname="se.khalek.ant.LaTeXTask"
		classpath="path/to/jar"/>

	<!-- Compile a LaTeX document to PDF. -->
	<target name="pdf">
		<latex
			source="document.tex"
			pdfTex="true"/>
	</target>
</project>
```
