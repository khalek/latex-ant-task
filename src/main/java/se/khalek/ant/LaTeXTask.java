package se.khalek.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;

/**
 * The class that performs the actual execution of {@code pdflatex}. The purpose
 * of this class is foremost to be used by the class {@code LaTeX} so that
 * compilation is done twice. This is to get cross-references in the document
 * right. This class can also be used for testing purposes.
 */
public class LaTeXTask extends AbstractTask {
	private int exitValue;

	/**
	 * Default constructor, does nothing.
	 */
	public LaTeXTask() {
		super();
	}

	/**
	 * Alternative constructor. Sets the values for all attributes in this
	 * class.
	 * 
	 * @param source
	 *            The file name of the LaTeX document.
	 * @param workingDir
	 *            The path to the directory for the document.
	 */
	protected LaTeXTask(String source, String workingDir) {
		super();
		this.source = source;
		this.workingDir = workingDir;
		exitValue = 0;
	}

	/**
	 * Retrieves and returns the exit value from the latest execution of
	 * {@code pdflatex}.
	 * 
	 * @return The exit value for the latest execution of {@code pdflatex}.
	 */
	public int getExitValue() {
		return exitValue;
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
	 * Execute this task. This task will attempt to execute the system
	 * installation of {@code pdflatex.} Any failures during the execution will
	 * result in a {@code BuildException}.
	 * 
	 * @throws BuildException
	 *             if a failure occurs during compilation of the document.
	 */
	public void execute() throws BuildException {
		// Convert the file name and its path to a File object.
		File sourceFile = convertToFile(workingDir, source);

		// Execute pdflatex
		log("Exec: pdflatex -interaction=nonstopmode " + sourceFile.getName());
		try {
			Process pdfTex = Runtime.getRuntime().exec(
					commands() + sourceFile.getPath());

			// Log the outputs from pdflatex.
			logOutput(pdfTex);

			// Wait for pdfTex to finish and examine its exit value.
			exitValue = pdfTex.waitFor();
			log("pdfTeX exited with exit value " + exitValue + ".");
			if (exitValue != 0) {
				throw new BuildException("Failure in generating pdf document.");
			}
			// Should only be thrown by Runtime.getRunTime().exec(String).
		} catch (IOException e) {
			throw new BuildException("Failed to execute pdflatex: "
					+ e.getMessage());
		} catch (InterruptedException e) {
			throw new BuildException("Disaster occured :C - " + e.getMessage());
		}
	}
}
