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


import java.util.List;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.output.WebRepositoryIntegration;

import org.jdom.Element;

import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.model.Module;
import de.berlios.statcvs.xml.util.Formatter;

/**
 * @author Steffen Pingel
 */
public class TableElement extends Element 
						   implements Separable {

	ReportSettings settings;
	String[] headers;
	
	/**
	 * @param name the name of the report
	 */
	public TableElement(ReportSettings settings, String[] headers) 
	{
		super("table");
		this.settings = settings;
		this.headers = headers;
		
		Element row = new Element("tr");
		for (int i = 0; i < headers.length; i++) {
			row.addContent(new Element("th").addContent(headers[i]));
		}
		addContent(row);
	}
	
	public RowElement addRow()
	{
		RowElement row = new RowElement();
		addContent(row);
		return row;
	}
	
	public void addRow(RowElement row)
	{
		addContent(row);
	}

	/**
	 * @see de.berlios.statcvs.xml.output.Separable#getReportPage(int)
	 */
	public Element getPage(int page) {
		if (!settings.isPaging()) {
			return (Element)this.clone();	
		} else {

			int lower = page * settings.getItemsPerPage();
			int upper = lower + settings.getItemsPerPage();
			if (upper > getContent().size() - 1) {
				upper = getContent().size() - 1;
			}

			List items = getContent().subList(lower + 1, upper + 1);
			TableElement tablePart = new TableElement(settings, headers);
			for (int i = 0; i < items.size(); i++) {
				tablePart.addContent((Element)((Element)items.get(i)).clone());
			}
			return tablePart;
		}
	}

	/**
	 * @see de.berlios.statcvs.xml.output.Separable#getPageCount()
	 */
	public int getPageCount() {
		int items = getContent().size() - 1; // -1 == header
		return settings.isPaging()?(int) Math.ceil((double)items / (double)settings.getItemsPerPage()):1;
	}

	public class RowElement extends Element
	{
		public RowElement()
		{
			super("row");
		}
		
		public RowElement addInteger(String key, int value, double percentage)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + value);
			number.setAttribute("percentage", "" + Formatter.formatPercent(percentage) );
			
			addContent(number);
			return this;
		}

		public RowElement addInteger(String key, int value)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + value);
			
			addContent(number);
			return this;
		}
		
		public RowElement addDouble(String key, double value, double percentage)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + Formatter.formatNumber(value, 2));
			number.setAttribute("percentage", "" + Formatter.formatPercent(percentage) );
			
			addContent(number);
			return this;
		}

		public RowElement addDouble(String key, double value)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + Formatter.formatNumber(value, 2));
			
			addContent(number);
			return this;
		}
		
		public RowElement addString(String key, String value) 
		{
			addContent(new Element("string").setAttribute("key", key).setAttribute("value", value));
			return this;
		}
		
		public RowElement addLink(String key, String value, String url)
		{
			addContent(new Element("link")
							.setAttribute("key", key)
							.setAttribute("value", value)
							.setAttribute("url", url));
			return this;
		}
		
		public RowElement addDirectory(Directory dir) 
		{
			addContent(new Element("directory").setAttribute("name", dir.getPath()));
			return this;
		}
		
		public RowElement addModule(Module module) 
		{
			addContent(new Element("module").setAttribute("name", module.getName()));
			return this;
		}
		
		public RowElement addAuthor(Author author) 
		{
			addContent(new Element("author").setAttribute("name", author.getName()));
			return this;
		}
		
		public RowElement addFile(CvsFile file)
		{
			WebRepositoryIntegration webRepository = settings.getWebRepository(); 
								
			return addLink("file", file.getFilenameWithPath(),
					(webRepository == null) ? "" : webRepository.getFileViewUrl(file));
		}
		
		public RowElement addGroup(Grouper grouper, Object group)
		{
			grouper.addElement(group, this);
			return this;
		}
	}
}
