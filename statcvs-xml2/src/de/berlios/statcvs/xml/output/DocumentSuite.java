package de.berlios.statcvs.xml.output;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import net.sf.statcvs.model.CvsContent;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Reads the document suite configuration from an XML file and create the reports.
 * 
 * @author Steffen Pingel
 */
public class DocumentSuite {

	private static Logger logger = Logger.getLogger(DocumentSuite.class.getName());
	
	private CvsContent content;
	private Document suite;
	private Properties defaultSettings = new Properties();

	/**
	 * 
	 */
	public DocumentSuite(URL url, CvsContent content) throws IOException
	{
		this.content = content;
		
		try {
			SAXBuilder builder = new SAXBuilder();
			suite = builder.build(url);
		}
		catch (JDOMException e) {
			throw new IOException(e.getMessage());
		}

	}
	
	public StatCvsDocument createDocument(Element root)
	{
		Properties documentSettings = new Properties(defaultSettings);
		readProperties(documentSettings, root);
		
		// generate reports
		StatCvsDocument document = new StatCvsDocument(documentSettings);
		for (Iterator it = root.getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("report".equals(element.getName())) {
				ReportElement report = createReport(document, element);
				if (report != null) {
					document.getRootElement().addContent(report);
				}
			}
		}
		
		return document;
	}
	
	/**
	 * @param element
	 */
	private ReportElement createReport(StatCvsDocument document, Element root) 
	{
		ReportSettings reportSettings = new ReportSettings(document.getSettings());
		readProperties(reportSettings, root);
				
		String className = root.getAttributeValue("class");
		if (className != null) {
			if (className.indexOf(".") == -1) {
				className = "de.berlios.statcvs.xml.report." + className;
			}  

			try {
				Class c = Class.forName(className);
				Method m = c.getMethod("generate", new Class[] { CvsContent.class, ReportSettings.class });
				ReportElement report = (ReportElement)m.invoke(null, new Object[] { content, reportSettings });
				return report;
			}
			catch (Exception e) {
				logger.warning("Could not generate report: " + e);
			}
		}
		
		return null;
	}

	public void generate(DocumentRenderer renderer) 
		throws IOException 
	{
		readProperties(defaultSettings, suite.getRootElement());
		
		// generate documents
		for (Iterator it = suite.getRootElement().getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("document".equals(element.getName())) {
				StatCvsDocument document = createDocument(element);
				renderer.render(document);
			}
		}

		renderer.postRender();
	}

	/**
	 * Reads all setting elements located under root.
	 */ 	
	public void readProperties(Properties properties, Element root)
	{
		for (Iterator it = root.getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("settings".equals(element.getName())) {
				// add childern as key value pairs
				for (Iterator it2 = element.getChildren().iterator(); it2.hasNext();) {
					Element setting = (Element)it2.next();
					properties.put(setting.getName(), setting.getText());
				}
			}
		}
	}
	
}
