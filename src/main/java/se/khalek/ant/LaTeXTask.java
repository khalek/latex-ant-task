package se.khalek.ant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Ant task for compiling LaTeX documents. The task uses pdfTeX as compiler.
 */
public class LaTeXTask extends Task {
		// Attributes of the task. //
		private String source;
		private String workingDir = System.getProperty("user.dir");
		private boolean clean;

		/**
		 * Sets the required attribute for filename to source latex file.
		 * 
		 * @param src The filename of the source latex file.
		 */
		public void setSource(String src) {
			source = src;
		}
		
		/**
		 * Sets the working directory for pdfTeX.
		 * If this attribute is not set then the current directory will
		 * be chosen.
		 * 
		 * @param dir Working directory.
		 */
		public void setWorkingDir(String dir) {
			File directory = new File(workingDir + "/" + dir);
			workingDir = directory.getAbsolutePath();
		}
		
		/**
		 * Sets the value of the attribute clean.
		 * 
		 * @param doClean Assign true if files, such as .log and .aux, 
		 * should be removed after compilation, false otherwise.
		 */
		public void setClean(boolean doClean) {
			clean = doClean;
		}
		
		/**
		 * Retrieves the current application version from
		 * the version.properties resource.
		 * 
		 * @return The version number.
		 */
		private String version() {
			String version = "";
			URL resource = getClass().getResource("/version.properties");
			Properties props = new Properties();
			try {
				props.load(resource.openStream());
				version = props.getProperty("version");
			} catch (IOException e) {
				version = "undetermined";
				e.printStackTrace();
			}
			return version;
		}

		/**
		 * Executes this and any nested tasks.
		 * 
		 * @throws BuildException if an error occurs during execution.
		 */
		public void execute() throws BuildException {
			// Make sure that source file is a required attribute.
			if (source == null) {
				throw new BuildException("No latex source file was given.");
			}
			String absolutePath = workingDir + "/" + source;
			File file = new File(absolutePath);
			if (! file.exists()) {
				throw new BuildException("File " + absolutePath + " does not exist.");
			}
						
			log("Executing LaTeX ANT Task, Version " + version());
			
			// Log the values of all attributes.
			log("source \t = " + source);
			log("workingDir \t = " + workingDir);
			log("clean \t = " + clean);
		}
}
