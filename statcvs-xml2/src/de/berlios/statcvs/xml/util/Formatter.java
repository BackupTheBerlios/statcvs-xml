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

package de.berlios.statcvs.xml.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Helper class providing static methods for formatting different number and
 * date formats.
 */
public class Formatter
{

    //--- Constant(s) ---

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    public static final String[] SIZES = { "B", "KB", "MB", "GB", "TB" };

    //--- Data field(s) ---

    //--- Constructor(s) ---

    //--- Method(s) ---

    /**
     * Formats double as decimal number.
     *
     * @param number number to be formatted
     * @param decimal the number of digits allowed in the fraction portion of
     * a number 
     * @return formatted number as string
     */
    public static String formatNumber(double number, int decimal)
    {
	    NumberFormat n = NumberFormat.getNumberInstance();
		
	    if (decimal > 0) {
			n.setMinimumFractionDigits(decimal);
			n.setMaximumFractionDigits(decimal);
	    }
		
	    return n.format(number);
    }

    /**
     * Formats a number without any digits in the fraction portion.
     *
     * @param number number to be formatted
     * @return formatted number as string
     */
    public static String formatNumber(double number) 
    {
		return formatNumber(number, 0);
    }

	/**
	 * Assumes that values is between 0 and 100.
	 */
    public static String formatPercent(double number)
	{
	    NumberFormat n = NumberFormat.getPercentInstance();
		n.setMinimumFractionDigits(1);
		n.setMaximumFractionDigits(1);
		return n.format(number);
	}

    /**
     * Returns <code>value</code> (byte) formatted as a file size.
     * For example value=2048 returns "2 kb".
     * 
     * @param size filesize to be formatted
     * @return formatted number as string
     */
    public static String formatSize(double size)
    {
		int i = 0;
		for (; i < SIZES.length - 1 && size >= 1024; i++) {
			size /= 1024;
		}

		return formatNumber(size, 1) + " " + SIZES[i];
    }

    /**
     * Formats number of seconds in appropriate time unit
     *
     * @param i number of seconds
     * @return formatted duration as string
     */
    public static String formatLength(long i)
    {
		StringBuffer s = new StringBuffer();
	
		long x = (i / 3600);
		if (x > 0) {
			s.append(x);
			s.append(":");
		}
		x = (i % 3600) / 60;
		if (x < 10) {
			s.append("0");
		}
		s.append(x);
		s.append(":");
		x = (i % 60);
		if (x < 10) {
			s.append("0");
		}
		s.append(x);

		return s.toString();
    }

	public static String formatDate(Date date)
	{
		return DateFormat.getInstance().format(date);
	}
	
}
