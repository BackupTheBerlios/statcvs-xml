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
    
	$RCSfile: CvsLocHistory.java,v $ 
	Created on $Date: 2003/07/24 00:40:06 $ 
*/
package net.sf.statcvs.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.statcvs.Main;
import net.sf.statcvs.Settings;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.util.FileUtils;

/**
 * CvsLibrary
 * 
 * @author Tammo van Lessen
 */
public class CvsLocHistory {

	private static final int CURR_HIST_VERSION = 3;
	private static Logger logger = Logger.getLogger(CvsLocHistory.class.getName());
	
	private static CvsLocHistory singleton = new CvsLocHistory();
	
	private Map fileLocMap = new HashMap();	
	private List directories = new ArrayList();
	private File workingDir;
	private boolean loaded = false;
	private boolean changed = false;
		
	private CvsLocHistory() {
		workingDir = new File(Settings.getCheckedOutDirectory());
	}
	
	public static CvsLocHistory getInstance() {
		return singleton;
	}
	
	public void load(String module) {
		try {
			String filename = Main.getSettingsPath()+module+".hist";
			FileInputStream in = new FileInputStream(filename);
			try {
				ObjectInputStream ois = new ObjectInputStream(in);
				int version = ois.readInt();
				if (version != CURR_HIST_VERSION) {
					logger.warning("wrong history file found. creating a new one");
					generate();
					save(module);
					return;
				}
				fileLocMap = (Map)ois.readObject();
				directories = (List)ois.readObject();
				loaded = true;
				logger.info("History file '"+module+".hist' loaded.");
			} catch (ClassNotFoundException e) {
				logger.warning("strange error, wrong history file, creating a new one");
				generate();
				save(module);
				return;
			}
			finally {
				in.close();
			}
		} catch (IOException e) {
			logger.info("No history file found");
			generate();
			save(module);
		}
	}
	
	public void save(String module) {
		if (!loaded) {return;}
		FileOutputStream out;
		try {
			String filename = Main.getSettingsPath()+module+".hist";
			out = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeInt(CURR_HIST_VERSION);
			oos.writeObject(fileLocMap);
			oos.writeObject(directories);
			oos.flush();
			out.close();
		} catch (IOException e) {
			logger.warning("Could not save history.");
		}
	}

	public void generate() {
		logger.info("Generating history file...");
		fileLocMap.clear();
		directories.clear();
		try {
			char fs = File.separatorChar;
			File tmpdir = new File(System.getProperty("java.io.tmpdir")+fs+"statcvs"+ Integer.toHexString(this.hashCode()) +"history");
			File cvsdir = new File(tmpdir, "CVS");
			cvsdir.mkdirs();
			FileUtils.copyFile(Settings.getCheckedOutDirectory()+fs+"CVS"+fs+"Repository",
				 cvsdir.getAbsolutePath()+fs+"Repository");
			FileUtils.copyFile(Settings.getCheckedOutDirectory()+fs+"CVS"+fs+"Entries",
				 cvsdir.getAbsolutePath()+fs+"Entries");
			FileUtils.copyFile(Settings.getCheckedOutDirectory()+fs+"CVS"+fs+"Root",
				 cvsdir.getAbsolutePath()+fs+"Root");

			String[] cmd = {"cvs", "-Q", "update","-d", "-r","1.1"};
			try {
				Runtime rt = Runtime.getRuntime();
				final Process p = rt.exec(cmd, null, tmpdir);
		 		p.waitFor();
		 		/*if (p.exitValue() != 0) {
		 			logger.warning("CVS query failed, aborting... History disabled");
		 			Settings.setUseHistory(false);
		 			return;
		 		}*/
			} catch (Exception e) {
				logger.warning("Could not query cvs, aborting...");
				return;
			}
		   
			String[] dirs = FileUtils.getDirectories(tmpdir.getAbsolutePath());
			logger.info("Indexing...");
			File[] files = tmpdir.listFiles(new NoDirFileFilter());
			
			for (int i=0; i<dirs.length; i++) {
				File dir = new File(dirs[i]);
				String relativePathname = dirs[i].substring(tmpdir.getAbsolutePath().length()+1);
				files = dir.listFiles(new NoDirFileFilter());
				for (int j=0; j<files.length; j++) {
					int lines = getLinesOfCode(files[j]);
					files[j].getName();
					String relativeFilename = (relativePathname+files[j].getName()).replace(fs, '/');
					fileLocMap.put(relativeFilename, new Integer(lines));
					logger.finer("indexing first revision of "+relativeFilename+": "+lines+" Lines");
				}
				directories.add(relativePathname.replace(fs, '/'));
			}
			
			logger.info("Index done");
			if (!FileUtils.deleteDir(tmpdir)) {
				logger.info("Could not clean up temp directory.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		loaded = true;
	}
	
	/**
	 * Returns the lines of code for a repository file.
	 * 
	 * @param filename absolute filename
	 * @return the lines of code for a repository file
	 * @throws RepositoryException when the line count could not be retrieved,
	 * for example when the file was not found.
	 */
	public int getLinesOfCode(final File file) {
		int linecount = 0;
		try {
			BufferedReader reader =
					new BufferedReader(new FileReader(file));
			
			while (reader.readLine() != null) {
				linecount++;
			}
			logger.finer("line count for '" + file.getName()
					+ "': " + linecount);
		} catch (IOException e) {
			logger.warning("could not get line count for '"
					+ file.getName() + "': " + e);
		}
		return linecount;
	}

	public int getLinesOfCode(CvsFile file) {
		if (!loaded) {return 0;} 
		// return 0 because we need a second run after hist generation
		if (Settings.getGenerateHistory()) {return 0;}
		int lineCount = 0;
		Integer loc = (Integer)fileLocMap.get(file.getFilenameWithPath());
		if (loc == null) {
			/*if (file.getLatestRevision().isInitialRevision()) {
				// TODO: Get local loc count... oder auch nicht
			}*/
			try {
				logger.info("History: LOC count unknown, asking cvs: "+file.getFilenameWithPath());
				Process cvs = Runtime.getRuntime().exec("cvs -q update -p -r 1.1 "+file.getFilenameWithPath(), 
						null, workingDir);
				BufferedReader cvsIn = new BufferedReader(new InputStreamReader(cvs.getInputStream()));
				while (cvsIn.readLine() != null) {
					lineCount++;
				}
				cvsIn.close();
				cvs.destroy();
				fileLocMap.put(file.getFilenameWithPath(), new Integer(lineCount));
				changed = true;
			} catch (IOException e) {
				logger.info("Could not get linecount of "+file.getFilenameWithPath());
			} 
		} else {
			lineCount = loc.intValue();
			logger.finer("History: loc for file '"+file.getFilenameWithPath()+"': "+lineCount);
		}
		return lineCount;
	}
	
	public boolean isEmpty() {
		return (!loaded || fileLocMap.isEmpty()); 
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public String[] getDirectories() {
		return (String[])directories.toArray(new String[0]);
	}
	
	/**
	 * NoDirFileFilter
	 * 
	 */
	public class NoDirFileFilter implements FileFilter {

		/**
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			return pathname.isFile();
		}

	}
}
