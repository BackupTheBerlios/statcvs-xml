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
    
	$RCSfile: OutputSettings.java,v $
	$Date: 2003/07/05 20:12:32 $ 
*/
package net.sf.statcvs.output.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Stores the output settings.
 *  
 * @author Steffen Pingel
 */
public class OutputSettings extends Properties {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.OutputSettings");

	private static OutputSettings singleton = new OutputSettings();

	private OutputSettings() {
	}

	public static OutputSettings getInstance() {
		return singleton;
	}

	public int get(String key, int defaultValue)
	{
		String value = getProperty(key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			}
			catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}

	public static String getCustomCss()
	{
		return getInstance().getProperty("customCss", null);
	}

	public void read(String filename) throws IOException
	{
		FileInputStream in = new FileInputStream(filename);
		try {
			load(in);
		}
		finally {
			in.close();
		}
	}
	
}
