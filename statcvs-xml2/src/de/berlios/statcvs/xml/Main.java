/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.berlios.statcvs.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import net.sf.statcvs.input.Builder;
import net.sf.statcvs.input.CvsLogfileParser;
import net.sf.statcvs.input.EmptyRepositoryException;
import net.sf.statcvs.input.LogSyntaxException;
import net.sf.statcvs.input.RepositoryFileManager;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.util.LogFormatter;
import de.berlios.statcvs.xml.output.DocumentRenderer;
import de.berlios.statcvs.xml.output.DocumentSuite;
import de.berlios.statcvs.xml.output.XDocRenderer;
import de.berlios.statcvs.xml.util.FileHelper;

/**
 * StatCvs Main Class; it starts the application and controls command-line
 * related stuff
 * @author Lukasz Pekacki
 * @author Richard Cyganiak
 * @version $Id: Main.java,v 1.10 2004/02/25 16:29:14 squig Exp $
 */
public class Main {
	private static String projectName;

	private static Logger logger = Logger.getLogger("net.sf.statcvs");

	public static final String VERSION = "@VERSION@";
	/**
	 * Main method of StatCvs
	 * @param args command line options
	 */
	public static void main(String[] args) {
		System.out.println(I18n.tr("StatCvs-XML - CVS statistics generation")+"\n");
		System.setProperty("java.awt.headless", "true");
		
		if (args.length == 0) {
			printProperUsageAndExit();
		}
		if (args.length == 1) {
			String arg = args[0].toLowerCase();
			if (arg.equals("-h") || arg.equals("-help")) {
				printProperUsageAndExit();
			} else if (arg.equals("-version")) {
				printVersionAndExit();
			}
		}

		try {
			try {
				new CommandLineParser(args).parse();
			} catch (IOException cex) {
				printProperUsageAndExit();
			}
						
			run();
		} catch (IOException e) {
			printErrorMessageAndExit(e.getMessage());
		} catch (LogSyntaxException lex) {
			printLogErrorMessageAndExit(lex.getMessage());
		} catch (OutOfMemoryError oome) {
			printOutOfMemMessageAndExit();
		} catch (Exception ioex) {
			ioex.printStackTrace();
			printErrorMessageAndExit(ioex.getMessage());
		}

		System.exit(0);
	}

	private static void printProperUsageAndExit() {
		System.out.println(
		//max. 80 chars
		//         12345678901234567890123456789012345678901234567890123456789012345678901234567890
				  "Usage: java -jar @JAR@ [options] <logfile> <directory>\n"
				+ "\n"
				+ "Required parameters:\n"
				+ "  <logfile>          path to the cvs logfile of the module\n"
				+ "  <directory>        path to the directory of the checked out module\n"
				+ "\n"
				+ "Some options:\n"
				+ "  -version           print the version information and exit\n"
				+ "  -output-dir <dir>  directory where HTML suite will be saved\n"
				+ "  -include <pattern> include only files matching pattern, e.g. **/*.c;**/*.h\n"
				+ "  -exclude <pattern> exclude matching files, e.g. tests/**;docs/**\n"
				+ "  -title <title>     Project title to be used in reports\n"
				+ "  -weburl <url>      integrate with web repository installation at <url>\n"
				+ "  -verbose           print extra progress information\n"
				+ "  -output-suite [class] use the xml renderer\n"
				+ "  -use-history       use history file for proper loc counts\n"
				+ "\n"
				+ "If statcvs cannot recognize the type of your web repository, please use the\n"
				+ "following switches:\n"
				+ "  -viewcvs <url>     integrate with viewcvs installation at <url>\n"
				+ "  -cvsweb <url>      integrate with cvsweb installation at <url>\n"
				+ "  -chora <url>       integrate with chora installation at <url>\n"
				+ "\n");
				//+ "Full options list: http://statcvs.sf.net/manual");
		System.exit(1);
	}

	private static void printVersionAndExit() {
		System.out.println("Version " + VERSION);
		System.exit(1);
	}

	private static void printOutOfMemMessageAndExit() {
		System.err.println("OutOfMemoryError.");
		System.err.println("Try running java with the -mx option (e.g. -mx128m for 128Mb).");
		System.exit(1);
	}

	private static void printLogErrorMessageAndExit(String message) {
		System.err.println("Logfile parsing failed.");
		System.err.println(message);
		System.exit(1);
	}

	private static void printErrorMessageAndExit(String message) {
		System.err.println(message);
		System.exit(1);
	}

	public static void run() throws Exception
	{
		long memoryUsedOnStart = Runtime.getRuntime().totalMemory();
		long startTime = System.currentTimeMillis();
		
		initLogger();

		String logfile = Settings.getLogFileName();
		
		CvsContent content = readLogFile(logfile, Settings.getUseHistory());
		generateSuite(content);
		
		long endTime = System.currentTimeMillis();
		long memoryUsedOnEnd = Runtime.getRuntime().totalMemory();
		logger.info("runtime: " + (((double) endTime - startTime) / 1000) 
					+ " seconds");
		logger.info("memory usage: "
					+ (((double) memoryUsedOnEnd 
						- memoryUsedOnStart) / 1024) + " kb");
	}

	public static void initLogger() throws LogSyntaxException {
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new LogFormatter());
		ch.setLevel(Settings.getLoggingLevel());
		//LogManager.getLogManager().getLogger("net.sf.statcvs").addHandler(ch);
		logger.addHandler(ch);
		logger.setUseParentHandlers(false);
	}

	/**
	 * @throws IOException if a required ConfigurationOption was 
	 *                                not set 
	 * @throws LogSyntaxException if the logfile contains unexpected syntax
	 * @throws IOException if the log file can not be read
	 */
	public static CvsContent readLogFile(String logfile, boolean filesHaveInitialRevision) 
		throws IOException, IOException, LogSyntaxException, EmptyRepositoryException
	{
		if (Settings.getLogFileName() == null) {
			throw new IOException("Missing logfile name");
		}
		if (Settings.getCheckedOutDirectory() == null) {
			throw new IOException("Missing checked out directory");
		}
		
		Reader logReader = new FileReader(logfile);
		
		logger.info("Parsing CVS log '" + Settings.getLogFileName() + "'");
		RepositoryFileManager repFileMan
			= new RepositoryFileManager
				(Settings.getCheckedOutDirectory());
		Builder builder = new Builder(repFileMan, Settings.getIncludeMatcher(), Settings.getExcludeMatcher());
		projectName = builder.getProjectName();
		new CvsLogfileParser(logReader, builder).parse();
		return builder.createCvsContent(filesHaveInitialRevision);
	}

	/**
	 * Generates HTML report. {@link net.sf.statcvs.output.ConfigurationOptions}
	 * must be initialized before calling this method.
	 * @throws Exception if somethings goes wrong
	 */
	public static void generateSuite(CvsContent content) throws Exception {	
		logger.info("Generating report for " 
					/* + Settings.getProjectName()*/
					+ " into " + Settings.getOutputDir());

//		if (Settings.getOutputSuite() == null) {
//			Settings.setOutputSuite(HTMLRenderer.class.getName());
//		}
		if (Settings.getWebRepository() != null) {
			logger.info("Assuming web repository is "+Settings.getWebRepository().getName());
		}
		logger.info("Reading output settings");
		logger.info("Creating suite using "+Settings.getOutputSuite());
	
//		Class c = Class.forName(Settings.getOutputSuite());
//		Method m = c.getMethod("generate", new Class[] { CvsContent.class });
//		m.invoke(null, new Object[] { content });
		
		DocumentRenderer renderer = XDocRenderer.create(content, new File(Settings.getOutputDir()));
		DocumentSuite suite = new DocumentSuite(FileHelper.getResource(Settings.getDocumentSuite()), content);
		suite.generate(renderer, projectName);
	}

}
