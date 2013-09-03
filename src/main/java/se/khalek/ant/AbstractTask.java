package se.khalek.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public abstract class AbstractTask extends Task {
	// Attributes //
	/**
	 * Attribute for the filename of the LaTeX document.
	 */
	protected String source;
	/**
	 * Attribute for the path to the document's directory.
	 */
	protected String workingDir;

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
	 * Creates and return a File object based on the path to the file or
	 * directory.
	 * 
	 * @param path
	 *            Path to the directory or file.
	 * @return The file or directory represented as a File object.
	 * @throws BuildException
	 *             If the file or directory does not exist.
	 */
	protected File convertToFile(String path) throws BuildException {
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
	protected File convertToFile(String dir, String file) throws BuildException {
		return convertToFile(dir + File.separator + file);
	}

	/**
	 * Send the output from a given process to the log for the Ant task
	 * represented by this class.
	 * 
	 * @param p
	 *            The process, which outputs will be logged.
	 */
	protected void logOutput(Process p) {
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
	 * Executes the task.
	 * 
	 * @throws BuildException
	 *             if failures are caught during execution of the task.
	 */
	public abstract void execute() throws BuildException;
}
