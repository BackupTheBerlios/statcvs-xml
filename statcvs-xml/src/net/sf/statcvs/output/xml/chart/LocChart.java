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
    
	$RCSfile: LocChart.java,v $
	$Date: 2003/06/28 01:34:55 $ 
*/
package net.sf.statcvs.output.xml.chart;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.reportmodel.TimeLine;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 */
public class LocChart extends TimeLineChart {

	public LocChart(CvsContent content) {
		super("loc.png", I18n.tr("Line Of Code"));
		TimeLine locTL = new TimeLine(null, I18n.tr("Lines"));
		
		RevisionIterator it
			= new RevisionSortIterator(content.getRevisionIterator());
		int loc = 0;
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			if (!rev.getFile().isBinary()) {
				loc += rev.getLinesOfCodeChange();	
			}
			locTL.addTimePoint(rev.getDate(), loc);
		}
		setRangeLabel(locTL.getRangeLabel());
		addTimeLine(locTL);
		getChart().setLegend(null);
		placeTitle();
	}

}
