/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: ModulesTreeReport.java,v $
	$Date: 2003/06/27 01:05:34 $ 
*/
package net.sf.statcvs.output.xml.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.statcvs.I18n;
import net.sf.statcvs.Main;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.util.FileUtils;

import org.jdom.Element;

/**
 * ModulesTreeReport
 * 
 * @author Tammo van Lessen
 */
public class ModulesTreeReport extends ReportElement {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLOutput");
	private CvsContent content;
	/**
	 * 
	 */
	public ModulesTreeReport(CvsContent content) {
		super(I18n.tr("Repository Tree"));
		this.content = content;
		createReport();
		// copy dir icon
		try {
			FileUtils.copyFile(
					Main.class.getResourceAsStream("web-files/" + "folder.png"),
					new File(ConfigurationOptions.getOutputDir() + "folder.png"));
		} catch (FileNotFoundException e) {
			logger.warning(e.getMessage());
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

	}
	
	/**
	 * 
	 */
	private void createReport() {
		Element modules = new Element("modulesTree");
		
		Iterator it = content.getDirectories().iterator();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			Element module = new Element("module");
			if (dir.isRoot()) {
				module.setAttribute("name", I18n.tr("[root]"));
			} else {
				module.setAttribute("name", dir.getName());
			}
			module.setAttribute("depth", ""+dir.getDepth());
			module.setAttribute("files", ""+dir.getCurrentFileCount());
			module.setAttribute("loc", ""+dir.getCurrentLOC());

			module.setAttribute("url",getModulePageUrl(dir.getPath()));
			modules.addContent(module);
		}
		addContent(modules);
	}

	public static String getModulePageUrl(String module) {
		if (!module.startsWith("/")) {
			module = "/" + module;
		}
		return "module_"+module.substring(0, module.length() - 1).replaceAll("/", "_");
	}
}
