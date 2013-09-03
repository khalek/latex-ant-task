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
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.comparators.FileSystem;

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
	private String source;
	private String workingDir = System.getProperty("user.dir");
	private boolean clean;
	private boolean pdftex;
	
	// Files that should be removed by a clean
	private String cleanTargets = "*.log *.aux";

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
	 * Creates and return a file object based on two parameters. The first
	 * parameter is the directory to the file and the second, the name of the
	 * file.
	 * 
	 * @param dir
	 *            Path to the files directory.
	 * @param file
	 *            The name of the file.
	 * @return A File object for the file.
	 * @throws BuildException
	 *             If the file does not exist.
	 */
	private File convertToFile(String dir, String file, String type) throws BuildException {
		return convertToFile(dir + File.separator + file, type);
	}
	
	private File convertToFile(String path, String type) throws BuildException {
		try {
			File f = new File(path).getCanonicalFile();
			if (!f.exists()) {
				throw new BuildException(type + " " + path + " does not exist.");
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
		File workDir = convertToFile(workingDir, "Directory");

		// Convert the file name and its path to a File object.
		File sourceFile = convertToFile(workingDir, source, "File");

		log("Executing LaTeX ANT Task, Version " + version());

		// Log the values of all attributes.
		log("source \t = " + source);
		log("workingDir \t = " + workDir.getPath());
		log("clean \t = " + clean);
		log("pdftex \t = " + pdftex);

		// Execute pdflatex if attribute pdftex is true
		if (pdftex) {
			log("Exec: pdflatex -interaction=nonstopmode " + source);
			try {
				Process pdfTex = Runtime.getRuntime().exec(
						commands() + sourceFile.getCanonicalPath());

				// Log the output from pdflatex.
				logOutput(pdfTex);

				// Wait for pdfTex to finish and examine its exit value.
				int exitVal = pdfTex.waitFor();
				log("pdfTeX exited with exit value " + exitVal + ".");
				if (exitVal != 0) {
					throw new BuildException(
							"Failure in generating pdf document.");
				}

				// If set, clean the working directory.
				if (clean) {
					Delete del = new Delete();
					del.setDir(workDir);
					del.setIncludes(cleanTargets);
					del.execute();
				}
			} catch (IOException e) {
				throw new BuildException(e.getMessage());
			} catch (InterruptedException e) {
				throw new BuildException("Disaster occured :C - "
						+ e.getMessage());
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
}