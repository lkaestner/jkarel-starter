import java.io.*;
import java.nio.file.*;
import java.util.logging.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generate Eclipse RunConfigurations for all Karel classes for convenience.
 * Quick and dirty, but it works.
 * 
 * @author lkaestner
 */
public class GenerateRunConfigurations {

	static final String cl = GenerateRunConfigurations.class.getName();
	static final Logger log =Logger.getLogger(cl);
	static final String lcPath = ".metadata/.plugins/org.eclipse.debug.core/.launches";
	
	public static void main(String[] args) throws Exception {
		new GenerateRunConfigurations().run();
	}
	
	void run() throws Exception {
		log.info("START");
		deleteRunConfigurations();
		createRunConfigurations();
		log.info("DONE");
	}
	
	void deleteRunConfigurations() throws Exception {
		log.entering(cl, "deleteRunConfigurations");
		Files.newDirectoryStream(
				Paths.get("../" + lcPath), "*.launch")		
				.forEach(this::deleteFile);
	}
	
	void deleteFile(Path path) {
		log.entering(cl, "deleteFile");
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {}
	}
	
	void createRunConfigurations() throws Exception {
		log.entering(cl, "createRunConfigurations");
		Files.newDirectoryStream(
				Paths.get("."), "*.java")
				.forEach(this::createRunConfiguration);
	}
	
	void createRunConfiguration(Path path) {
		
		String className = path.getFileName().toString();
		log.fine("Creating LaunchConfiguration for: " + className);
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    Document doc = docBuilder.newDocument();
		    
		    Element root = doc.createElement("launchConfiguration");
		    root.setAttribute("type", "org.eclipse.jdt.launching.localJavaApplication");
		    doc.appendChild(root);
		    
		    Element e1 = doc.createElement("listAttribute");
		    e1.setAttribute("key", "org.eclipse.debug.core.MAPPED_RESOURCE_PATHS");
		    root.appendChild(e1);
		    
		    Element e1a = doc.createElement("listEntry");
		    e1a.setAttribute("value", "/KarelRobot/" + className);
		    e1.appendChild(e1a);
		    
		    Element e2 = doc.createElement("listAttribute");
		    e2.setAttribute("key", "org.eclipse.debug.core.MAPPED_RESOURCE_TYPES");
		    root.appendChild(e2);
		    
		    Element e2a = doc.createElement("listEntry");
		    e2a.setAttribute("value", "1");
		    e2.appendChild(e2a);
		    
		    Element e3 = doc.createElement("booleanAttribute");
		    e3.setAttribute("key", "org.eclipse.jdt.launching.ATTR_USE_START_ON_FIRST_THREAD");
		    e3.setAttribute("value", "true");
		    root.appendChild(e3);
		    
		    Element e4 = doc.createElement("stringAttribute");
		    e4.setAttribute("key", "org.eclipse.jdt.launching.MAIN_TYPE");
		    e4.setAttribute("value", className.replace(".java", ""));
		    root.appendChild(e4);
		    
		    Element e5 = doc.createElement("stringAttribute");
		    e5.setAttribute("key", "org.eclipse.jdt.launching.PROJECT_ATTR");
		    e5.setAttribute("value", "KarelRobot");
		    root.appendChild(e5);

		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    DOMSource source = new DOMSource(doc);
		    
		    String fileName = className.replace(".java", ".launch");
		    StreamResult result = new StreamResult(new File("../" + lcPath + "/" + fileName));
		    transformer.transform(source, result);
		    
		} catch (Exception e) {
			log.severe("Error: " + e);
		}
		
	}

}
