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
    
	$RCSfile$
	$Date$
*/
package net.sf.statcvs.model;

/**
 * A concrete directory in a directory tree. Implementation of the
 * {@link Directory} interface.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id$
 */
public class DirectoryImpl extends Directory {

	private Directory parent;
	private String name;

	/**
	 * Creates a new <tt>Directory</tt> with the given parent and name
	 * @param parent the parent directory
	 * @param name the directory's name without path or slashes
	 */
	public DirectoryImpl(Directory parent, String name) {
		this.parent = parent;
		this.name = name;
		if (!parent.getSubdirectories().contains(this)) {
			parent.addSubdirectory(this);
		}
	}

	/**
	 * @see net.sf.statcvs.model.Directory#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see net.sf.statcvs.model.Directory#getPath()
	 */
	public String getPath() {
		return parent.getPath() + name + "/";
	}

	/**
	 * @see net.sf.statcvs.model.Directory#getParent()
	 */
	public Directory getParent() {
		return parent;
	}

	/**
	 * @see net.sf.statcvs.model.Directory#isRoot()
	 */
	public boolean isRoot() {
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "directory " + getPath();
	}

	/**
	 * @see net.sf.statcvs.model.Directory#getDepth()
	 */
	public int getDepth() {
		return parent.getDepth() + 1;
	}
}