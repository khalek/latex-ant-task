package se.khalek.ant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;

/**
 * Implementation for an Ant task that compiles LaTeX documents with pdfTeX. The
 * task contains a number of attributes, which are private fields in this class
 * and are set in a public method "setX", where X is the name of the attribute.
 * For example, if we have an attribute to the Ant task called verbose, we will
 * have to create a private field called {@code verbose} and a public method
 * {@code setVerbose()}.
 */
public class LaTeX extends AbstractTask {
	// Attributes of the task. //
	private boolean clean;
	private String includes;

	/**
	 * Default constructor. Sets some initial values to some of the attributes.
	 */
	public LaTeX() {
		super();
		workingDir = System.getProperty("user.dir");
		clean = false;
		includes = "*.log *.aux";
	}

	/**
	 * Sets the value of the attribute clean.
	 * 
	 * @param doClean
	 *            Assign true if files, such as .log and .aux, should be removed
	 *            after compilation, false otherwise.
	 */
	public void setClean(boolean doClean) {
		clean = doClean;
	}

	/**
	 * Sets the value of attribute includes. Determines which files to remove by
	 * a clean.
	 * 
	 * @param includes
	 *            The pattern for which files to remove by clean as assigned in
	 *            a build file.
	 */
	public void setIncludes(String includes) {
		this.includes = includes;
	}

	/**
	 * Determines the pdflatex of the system and adds all necessary arguments,
	 * which are returned together as a single String.
	 * 
	 * @return The command to execute pdflatex and its arguments.
	 */
	private String commands() {
		// Add any additional commands into this array.
		String[] cmdArr = { "pdflatex", "-interaction=nonstopmode",
				"-output-directory=" + workingDir };
		// Convert to a single String.
		String commands = "";
		for (String c : cmdArr) {
			commands += c + " ";
		}
		return commands;
	}

	/**
	 * Removes files from a directory according to the pattern designated in the
	 * includes attribute.
	 * 
	 * @param directory
	 *            The directory that the clean should target.
	 */
	private void performClean(File directory) {
		// Create a file set for the directory and files to delete.
		FileSet fs = new FileSet();
		fs.setDir(directory);
		fs.setIncludes(includes);

		// Create a new delete task
		Delete del = new Delete();
		del.setProject(getProject());
		del.setTaskName("clean");
		del.addFileset(fs);
		del.setVerbose(true);

		// Execute the clean
		log("Excuting clean");
		del.execute();
	}

	/**
	 * Retrieves the current application version from the version.properties
	 * resource.
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
		}
		return version;
	}

	/**
	 * Executes this tasks. Relevant information gets logged during execution of
	 * the task.
	 * 
	 * @throws BuildException
	 *             if an error occurs during execution.
	 */
	public void execute() throws BuildException {
		// Make sure that a source file is given as an attribute.
		if (source == null) {
			throw new BuildException("No latex source file was given.");
		}

		// Convert working directory String to a directory with canonical path.
		File workDir = convertToFile(workingDir);

		// Convert the file name and its path to a File object.
		File sourceFile = convertToFile(workingDir, source);

		log("Executing LaTeX ANT Task, Version " + version());

		// Log the values of all attributes.
		log("source \t = " + source);
		log("workingdir \t = " + workDir.getPath());
		log("clean \t = " + clean);
		log("pdftex \t = " + pdftex);

		// Execute pdflatex if attribute pdftex is true
		int exitVal = 0;
		if (pdftex) {
			log("Exec: pdflatex -interaction=nonstopmode " + source);
			try {
				Process pdfTex = Runtime.getRuntime().exec(
						commands() + sourceFile.getPath());

				// Log the outputs from pdflatex.
				logOutput(pdfTex);

				// Wait for pdfTex to finish and examine its exit value.
				exitVal = pdfTex.waitFor();
				log("pdfTeX exited with exit value " + exitVal + ".");
				if (exitVal != 0) {
					throw new BuildException(
							"Failure in generating pdf document.");
				}
				// Should only be thrown by Runtime.getRunTime().exec(String).
			} catch (IOException e) {
				throw new BuildException("Failed to execute pdflatex: "
						+ e.getMessage());
			} catch (InterruptedException e) {
				throw new BuildException("Disaster occured :C - "
						+ e.getMessage());
			}
		}

		// If set and no failures were generated, clean the working directory.
		if (clean && exitVal == 0) {
			performClean(workDir);
		}
	}
}