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
    
	$RCSfile: RevisionLimitIterator.java,v $ 
	Created on $Date: 2003/06/17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.NoSuchElementException;

/**
 * Returns only the first X entries from a {@link RevisionIterator}.
 * 
 * @author Richard Cyganiak
 * @version $Id: RevisionLimitIterator.java,v 1.1 2003/06/17 16:43:02 vanto Exp $
 */
public class RevisionLimitIterator implements RevisionIterator {

	private RevisionIterator source;
	private int limit;
	private int current = 0;

	/**
	 * Creates a new {@link RevisionIterator} which contains only
	 * the first <code>limit</code> entries of <code>source</code>.
	 * 
	 * @param source the source iterator
	 * @param limit the number of entries to keep
	 */
	public RevisionLimitIterator(RevisionIterator source, int limit) {
		this.source = source;
		this.limit = limit;
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#hasNext()
	 */
	public boolean hasNext() {
		return (current < limit && source.hasNext());
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#next()
	 */
	public CvsRevision next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		current++;
		return source.next();
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#reset()
	 */
	public void reset() {
		source.reset();
	}
}