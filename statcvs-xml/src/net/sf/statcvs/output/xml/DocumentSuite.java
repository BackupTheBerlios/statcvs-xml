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
    
	$RCSfile: DocumentSuite.java,v $
	$Date: 2003/06/28 11:12:27 $ 
*/
package net.sf.statcvs.output.xml;

import java.io.IOException;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

/**
 * XMLSuite
 * 
 * @author Tammo van Lessen
 */
public class DocumentSuite {

	/**
	 * 
	 */
	private DocumentSuite()
	{
	}
	
	public static void generate(CvsContent content, DocumentRenderer renderer) 
		throws IOException {
		renderer.render(new AuthorsDocument(content));
		renderer.render(new CommitLogDocument(content));
		renderer.render(new DirectorySizesDocument(content));
		renderer.render(new IndexDocument(content));
		renderer.render(new FileSizesDocument(content));
		
		// author pages
		for (Iterator i = content.getAuthors().iterator(); i.hasNext(); ) {
			Author author = (Author)i.next();
			renderer.render(new AuthorDocument(content, author));
		}

		// module pages
		for (Iterator i = content.getDirectories().iterator(); i.hasNext(); ) {
			Directory dir = (Directory)i.next();
			renderer.render(new ModuleDocument(content, dir));
		}

	}
}
