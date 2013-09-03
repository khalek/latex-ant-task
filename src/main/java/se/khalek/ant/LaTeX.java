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
	private boolean pdftex;

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
	 * Sets the value of the attribute pdftex.
	 * 
	 * @param doPdftex
	 *            Pass true if the task should execute pdflatex, false
	 *            otherwise.
	 */
	public void setPdftex(boolean doPdftex) {
		pdftex = doPdftex;
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

		log("Executing LaTeX ANT Task, Version " + version());

		// Log the values of all attributes.
		log("source \t = " + source);
		log("workingdir \t = " + workDir.getPath());
		log("clean \t = " + clean);
		log("pdftex \t = " + pdftex);
		
		int exitValue = 0;
		
		// Create the task that is executing pdflatex, if pdftex is true.
		if (pdftex) {
			LaTeXTask execTask = new LaTeXTask(source, workingDir);
			execTask.setProject(getProject());
			execTask.setTaskName(getTaskName());
			execTask.execute();
			exitValue = execTask.getExitValue();
		}

		// If set and no failures were generated, clean the working directory.
		if (clean && exitValue == 0) {
			performClean(workDir);
		}
	}
}