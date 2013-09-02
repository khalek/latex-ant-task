package se.khalek.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for compiling LaTeX documents. The task uses pdfTeX as compiler.
 *
 */
public class LaTeXTask extends Task {
		String source;

		public void setSource(String src) {
			source = src;
		}

		public void execute() throws BuildException {
			if (source == null) {
					throw new BuildException("No latex source file was given.");
			}
			log(source);
		}
}
