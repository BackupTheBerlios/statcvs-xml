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
    
	$RCSfile: MostRevisionsReport.java,v $
	$Date: 2003/06/24 19:18:59 $ 
*/
package net.sf.statcvs.output.xml.report;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.FilesRevisionCountComparator;

import org.jdom.Element;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class MostRevisionsReport extends ReportElement {

	private CvsContent content;
	
	/**
	 * 
	 */
	public MostRevisionsReport(CvsContent content) 
	{
		super(I18n.tr("Files With Most Revisions"));
		this.content = content;
		createReport();
	}

	private void createReport() {
		Element filesEl = new Element("files");
		
		List files = content.getFiles();
		Collections.sort(files, new FilesRevisionCountComparator());
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			if (file.isBinary() || file.isDead()) {
				continue;
			}

			Element fileEl = new Element("file");
			fileEl.setAttribute("name", file.getFilenameWithPath());
			fileEl.setAttribute("loc", ""+file.getCurrentLinesOfCode());
			fileEl.setAttribute("revisions", ""+file.getRevisions().size());
			filesEl.addContent(fileEl);
		}

		addContent(new Element("mostRecentFiles").addContent(filesEl));
	}
}
