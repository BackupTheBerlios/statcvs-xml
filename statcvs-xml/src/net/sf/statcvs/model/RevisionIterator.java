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
    
	$RCSfile: RevisionIterator.java,v $ 
	Created on $Date: 2003/06/17 16:43:02 $ 
*/
package net.sf.statcvs.model;



/**
 * An interface which provides an iterative access to the underlying
 * model structure.
 * 
 * @author Manuel Schulze
 * @version $Id: RevisionIterator.java,v 1.1 2003/06/17 16:43:02 vanto Exp $
 */
public interface RevisionIterator {
	/**
	 * Returns true if this iteration has more elements.
	 * 
	 * @return <code>True</code>, if there are more revisions
	 */
	boolean hasNext();
	
	/**
	 * Returns the next cvs revision or otherwise a
	 * <code>NoSuchElementException</code> is thrown.
	 * 
	 * @return The next revision
	 */
	CvsRevision next();

	/**
	 * Resets this iteration to the first element.
	 */
	void reset();
}
