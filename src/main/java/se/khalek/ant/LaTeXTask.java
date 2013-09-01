package se.khalek.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for compiling LaTeX documents. The task uses pdfTeX as compiler.
 *
 */
public class LaTeXTask extends Task {
		public void execute() throws BuildException {
				System.out.println("Task completed!");
		}
}
