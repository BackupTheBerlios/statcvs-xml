package de.berlios.statcvs.xml.output;

import java.util.Hashtable;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;
import de.berlios.statcvs.xml.I18n;

/**
 * @author Steffen Pingel
 */
public class ReportSettings extends Hashtable {

	protected ReportSettings defaults;

	/**
	 * @param defaults
	 */
	public ReportSettings(ReportSettings defaults) 
	{
		this.defaults = defaults;
	}

	public ReportSettings()
	{
	}

	public Object get(Object key, Object defaultValue)
	{
		Object o = super.get(key);
		return (o != null) ? o : (defaults != null) ? defaults.get(key, defaultValue) : defaultValue;
	}

	public Object get(Object key)
	{
		return this.get(key, null);
	}

	public String getProjectName()
	{
		return this.getString("projectName", "");
	}

	public Iterator getRevisionIterator(CvsContent content)
	{
		Object o = get("foreach");
		if (o instanceof Author) {
			return ((Author)o).getRevisions().iterator();
		}
		else if (o instanceof Directory) {
			return ((Directory)o).getRevisions().iterator();
		}
		return content.getRevisions().iterator();
	}

	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	public String getString(Object key, String defaultValue)
	{
		return (String)this.get(key, defaultValue);
	}

	/**
	 * @param string
	 * @param i
	 * @return
	 */
	public int getInt(Object key, int defaultValue) 
	{
		try {
			return Integer.parseInt(getString(key, defaultValue + ""));
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 
	 */
	public String getPostfix() 
	{
		Object o = get("foreach");
		if (o instanceof Author) {
			return ((Author)o).getName();
		}
		else if (o instanceof Directory) {
			return ((Directory)o).getPath();
		}
		else {
			return null;
		}
	}

	/**
	 * 
	 */
	public String getFilenamePostfix() 
	{
		String postfix = getPostfix();
		return (postfix == null) ? "" : "_" + postfix;
	}

	/**
	 * 
	 */
	public String getSubtitlePostfix() 
	{
		String postfix = getPostfix();
		return (postfix == null) ? "" : I18n.tr(" for {0}", postfix);
	}

}
