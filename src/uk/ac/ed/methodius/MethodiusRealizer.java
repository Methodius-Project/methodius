package uk.ac.ed.methodius;

//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import opennlp.ccg.grammar.Grammar;
import opennlp.ccg.realize.Realizer;
import opennlp.ccg.synsem.LF;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/*
 * The interface to OpenCCG for Methodius.
 * Takes a logical form to pass to OpenCCG and returns the resultant string.
 */

@SuppressWarnings("unchecked")

public class MethodiusRealizer {
	
//	private URL grammarURL;
	private Grammar grammar;
	private Realizer realizer;
	
	private String openCCGHome = null;
	private Configuration config = null;
	private Log log = null;
	
	
	public MethodiusRealizer(Configuration conf) throws IOException, TransformerConfigurationException, TransformerException   {
		config = conf;
		log = config.getLog();
		openCCGHome= config.getOpenCCGDomain();
		String grammarString = "grammar.xml";
        String fullGrammarString = openCCGHome + "/" + grammarString;
//		grammarURL = new File(openCCGHome, grammarfile).toURL();
		
//		log.output("Loading grammar from URL: " + grammarURL);
        log.output("Loading grammar from URL: " + fullGrammarString);
		//grammar = new Grammar(grammarURL);
        grammar = new Grammar(fullGrammarString);
	}
	

	public String realizeLF(String exp) throws Exception {
		log.start("MethodiusRealizer.realizeLF");
		Document lfDoc = createLogicalFormFromString(exp);
		log.output("Logical Form is:");
		Util.write(lfDoc, log.getIndentString());
		realizer = new Realizer(grammar);
		LF lf = grammar.loadLF(lfDoc);
		String sentence = realizer.realize(lf).getSign().getOrthography();
		log.end("MethodiusRealizer.realizeLF");
		return sentence;
	}


	public String realizeLF(Document lfDoc) throws Exception {
		log.start("MethodiusRealizer.realizeLF");
		log.output("Logical Form is:\n");
		if(!log.isSilent()){
			Util.write(lfDoc, log.getIndentString(), log.getPrintStream());
		}
		realizer = new Realizer(grammar);
		LF lf = grammar.loadLF(lfDoc);
		String sentence = realizer.realize(lf).getSign().getOrthography();
		log.end("MethodiusRealizer.realizeLF");
		return sentence;
	}


	
	
	private Document createLogicalFormFromString(String exp) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(exp));
		return doc;
	}
	
	
	class MyResolver implements URIResolver {
		public Source resolve(String href,String base) {
			log.output("MyResolver href = " + href + " base = " + base);
			URL thisURL = ClassLoader.getSystemResource("uk/ac/ed/hcrc/methodius/" + href);
			if (thisURL == null) {
				return null;
			}
			InputStream URLStream = null;
			try {
				URLStream = thisURL.openStream();
			}
			catch (IOException e) {
				System.err.println(e);
				return null;
			}
			return new StreamSource(URLStream);
		}
	}
	
	
}
