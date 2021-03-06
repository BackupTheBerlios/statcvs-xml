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
package de.berlios.statcvs.xml.output;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom.output.Format;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.util.FileUtils;
import de.berlios.statcvs.xml.util.FileHelper;


/**
 * HTMLRenderer
 * 
 * @author Tammo van Lessen
 */
public class HTMLRenderer extends XMLRenderer {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLRenderer");
	private ReportSettings settings;

	public HTMLRenderer(ReportSettings settings, Transformer transformer, File outputPath) 
	{
		super(transformer, outputPath);

		this.settings = settings;

		String ext = (String)transformer.getParameter("ext");
		setExtension((ext != null) ? ext : ".html");
		
		Format format = createDefaultFormat();
		format.setOmitDeclaration(true);
		format.setOmitEncoding(true);
		
		XMLOutputter xout = new HTMLOutputter(format);
		setOutputter(xout);
	}

	/**
	 * Copies the required resources.
	 */
	public void postRender()
	{
		super.postRender();
		
		String filename = settings.getString("customCss");
		if (filename != null) {
			FileHelper.copyResource(filename, getOutputPath());
		}
		FileHelper.copyResource("resources/statcvs.css", getOutputPath());
	}

	/**
	 * Invoked by Main.
	 */
	public static DocumentRenderer create(CvsContent content, ReportSettings settings) throws IOException 
	{
		String stylesheet = settings.getString("html-xsl", "resources/statcvs2html.xsl"); 
	    logger.info("Using " + stylesheet + " as stylesheet");
	    
	    StreamSource source = new StreamSource
			(FileHelper.getResource(stylesheet).toString());
		Transformer transformer;

		try {
            transformer	= TransformerFactory.newInstance().newTransformer(source);
        } catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}			

		// set stylesheet parameters
		transformer.setParameter("ext", ".html");
		String filename = settings.getString("customCss");
		if (filename != null) {
			transformer.setParameter
				("customCss", 
				 FileUtils.getFilenameWithoutPath(filename));
		}
		
		return new HTMLRenderer(settings, transformer, settings.getOutputPath());		
	}

}
