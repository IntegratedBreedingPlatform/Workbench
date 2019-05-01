
package org.generationcp.ibpworkbench.germplasm.pedigree;

import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

@Configurable
public class GraphVizUtility {

	public static final String START_GRAPH = "strict digraph G {";
	public static final String END_GRAPH = "}";

	private static final String TEMP_DIR = System.getProperty("user.dir");
	private static final Logger LOG = LoggerFactory.getLogger(GraphVizUtility.class);

	private static final String BSLASH = "\\";
	private static final String FSLASH = "/";

	@Value("${graphviz.executable.path}")
	private String graphvizExecutablePath;

	/**
	 * Where is your dot program located? It will be called externally.
	 */
	private String dotPath = null;

	/**
	 * The source of the graph written in dot language.
	 */
	private final StringBuilder graph = new StringBuilder();

	private String imageOutputPath = null;

	/**
	 * Initialize this GraphVizUtility instance.
	 * <p>
	 * This method should set the path of GraphViz dot executable.
	 */
	public void initialize() {
		final File dotFile = new File(graphvizExecutablePath).getAbsoluteFile();
		this.dotPath = dotFile.getAbsolutePath();
	}

	public void setImageOutputPath(final String imageOutputPath) {
		this.imageOutputPath = imageOutputPath;
	}

	/**
	 * Returns the graph's source description in dot language.
	 *
	 * @return Source of the graph in dot language.
	 */
	public String getDotSource() {
		return this.graph.toString();
	}

	/**
	 * Adds a string to the graph's source (without newline).
	 */
	public void add(final String line) {
		this.graph.append(line);
	}

	/**
	 * Adds a string to the graph's source (with newline).
	 */
	public void addln(final String line) {
		this.graph.append(line + "\n");
	}

	/**
	 * Returns the graph as an image in binary format.
	 *
	 * @param dotSource Source of the graph to be drawn.
	 * @param type      Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
	 * @return A byte array containing the image of the graph.
	 */
	public byte[] getGraph(final String dotSource, final String type) {
		final File dot;
		byte[] imgStream = null;

		try {
			dot = this.writeDotSourceToFile(dotSource);
			if (dot != null) {
				imgStream = this.getImgStream(dot, type);
				if (!dot.delete()) {
					GraphVizUtility.LOG.error("Warning: " + dot.getAbsolutePath() + " could not be deleted!");
				}
				return imgStream;
			}
			return new byte[0];
		} catch (final java.io.IOException ioe) {
			GraphVizUtility.LOG.error(ioe.getMessage(), ioe);
			return new byte[0];
		}
	}

	/**
	 * Writes the graph's image in a file.
	 *
	 * @param img A byte array containing the image of the graph.
	 * @param to  A File object to where we want to write.
	 * @return Success: 1, Failure: -1
	 */
	public int writeGraphToFile(final byte[] img, final File to) {
		try {
			try (final FileOutputStream fos = new FileOutputStream(to)) {
				fos.write(img);
			}
		} catch (final java.io.IOException ioe) {
			GraphVizUtility.LOG.error(ioe.getMessage(), ioe);
			return -1;
		}
		return 1;
	}

	/**
	 * It will call the external dot program, and return the image in binary format.
	 *
	 * @param dot  Source of the graph (in dot language).
	 * @param type Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
	 * @return The image of the graph in .gif format.
	 */
	private byte[] getImgStream(final File dot, final String type) {
		final File img;
		byte[] imgStream = null;

		try {
			img = File.createTempFile("graph_", "." + type, new File(GraphVizUtility.TEMP_DIR));
			final Runtime rt = Runtime.getRuntime();
			final String[] args = {this.dotPath, "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath()};
			final Process p = rt.exec(args);
			p.waitFor();

			try (final FileInputStream in = new FileInputStream(img.getAbsolutePath())) {
				imgStream = new byte[in.available()];
				in.read(imgStream);
			}

			if (!img.delete()) {
				GraphVizUtility.LOG.error("Warning: " + img.getAbsolutePath() + " could not be deleted!");
			}
		} catch (final java.io.IOException ioe) {
			GraphVizUtility.LOG.error(
				"Error: In I/O processing of tempfile in dir " + GraphVizUtility.TEMP_DIR + "\n or in calling external command", ioe);
		} catch (final InterruptedException ie) {
			GraphVizUtility.LOG.error("Error: the execution of the external program was interrupted", ie);
		}

		return imgStream;
	}

	/**
	 * Writes the source of the graph in a file, and returns the written file as a File object.
	 *
	 * @param str Source of the graph (in dot language).
	 * @return The file (as a File object) that contains the source of the graph.
	 */
	private File writeDotSourceToFile(final String str) throws java.io.IOException {
		final File temp;
		try {
			temp = File.createTempFile("graph_", ".dot.tmp", new File(GraphVizUtility.TEMP_DIR));
			try (final FileWriter fout = new FileWriter(temp)) {
				fout.write(str);
			}
		} catch (final Exception e) {
			GraphVizUtility.LOG.error("Error: I/O error while writing the dot source to temp file!", e);
			return null;
		}
		return temp;
	}

	public static String createImageOutputPathForWindow(final Window window) {
		return window.getWindow().getApplication().getContext().getBaseDirectory().getAbsolutePath().replace(
			GraphVizUtility.BSLASH,
			GraphVizUtility.FSLASH) + "/WEB-INF/image";
	}

	public String graphVizOutputPath(final String fileName) {
		return this.imageOutputPath + File.separator + fileName;
	}

	protected void setGraphvizExecutablePath(final String graphvizExecutablePath) {
		this.graphvizExecutablePath = graphvizExecutablePath;
	}

	protected String getDotPath() {
		return dotPath;
	}

}
