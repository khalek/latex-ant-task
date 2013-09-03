package se.khalek.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
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
public class LaTeXTask extends Task {
	// Attributes of the task. //
	private boolean clean;
	private boolean pdftex;
	private String includes;
	private String source;
	private String workingDir;

	public LaTeXTask() {
		super();
		workingDir = System.getProperty("user.dir");
		clean = false;
		includes = "*.log *.aux";
	}

	/**
	 * Sets the required attribute for filename to source latex file.
	 * 
	 * @param src
	 *            The filename of the source latex file.
	 */
	public void setSource(String src) {
		source = src;
	}

	/**
	 * Sets the working directory for pdfTeX. If this attribute is not set then
	 * the current directory will be chosen.
	 * 
	 * @param dir
	 *            Working directory.
	 */
	public void setWorkingDir(String dir) {
		workingDir = dir;
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
	 * Creates and return a File object based on the path to the directory and
	 * the filename to a specific file.
	 * 
	 * @param dir
	 *            The directory path to the file.
	 * @param file
	 *            The filename
	 * @return A File object that represents an existing file.
	 * @throws BuildException
	 *             If the file does not exist for the given path.
	 */
	private File convertToFile(String dir, String file) throws BuildException {
		return convertToFile(dir + File.separator + file);
	}

	/**
	 * Creates and return a File object based on the path to the file or
	 * directory.
	 * 
	 * @param path
	 *            Path to the directory or file.
	 * @return The file or directory represented as a File object.
	 * @throws BuildException
	 *             If the file or directory does not exist.
	 */
	private File convertToFile(String path) throws BuildException {
		try {
			File f = new File(path).getCanonicalFile();
			if (!f.exists()) {
				throw new BuildException(path + " does not exist.");
			}
			return f;
		} catch (IOException e) {
			throw new BuildException(e.getMessage());
		}
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
	 * Send the output from a given process to the log for the Ant task
	 * represented by this class.
	 * 
	 * @param p
	 *            The process, which outputs will be logged.
	 */
	private void logOutput(Process p) {
		// Reader for the incoming stream from the process.
		BufferedReader output = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		String line;
		try {
			while ((line = output.readLine()) != null) {
				log(line);
			}
		} catch (IOException e) {
			// Just log that output no longer works.
			log("Output ceased to function");
		}
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

				// Log the output from pdflatex.
				logOutput(pdfTex);

				// Wait for pdfTex to finish and examine its exit value.
				exitVal = pdfTex.waitFor();
				log("pdfTeX exited with exit value " + exitVal + ".");
				if (exitVal != 0) {
					throw new BuildException(
							"Failure in generating pdf document.");
				}
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
			// Create the file set with the target directory and files to
			// delete.
			FileSet fs = new FileSet();
			fs.setDir(workDir);
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
	}
}