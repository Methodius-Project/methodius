package uk.ac.ed.methodius;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
/**
 * The publisher is really the top of the Methodius chain.
 * It calls the Realiser to get a set of logical forms for the objects
 * and then passes these to OpenCCG to turn them into sentences.
 */


@SuppressWarnings("unchecked")
public class Publisher  {
	
	private DataStoreRead ds = null;
	private Configuration config = null;
//	private UserModel um = null;
	private Log log = null;
    private MethodiusRealizer mr = null;

	public Publisher(DataStoreRead d, Configuration c) throws Exception {
		ds = d;
		config = c;
//		um = config.getUserModel();
		log = config.getLog();
		mr = config.getMethodiusRealizer();
	}


	/**
	 * given an object id return an array of sentences describing it.
	 * Passes the if to a Realiser and gets back an array of logical forms.
	 * Each of these gets passed to OpenCCG through the MethodiusRealizer.
	 * @param id the object id as a string e.g. "exhibit44"
	 * @return array of Strings describing the object.
	 * @throws Exception
	 */
    public String[] describe (String id) throws Exception {
		log.start("Publisher.describe");

		String[] sentences = new String[100];
        Realiser r = new Realiser(ds, config);
        
		LogicalForms logForms = r.getDescription(id);
		if (logForms == null) {
		    sentences[0] = config.getDefaultSentence();
		}
		else {
		    ArrayList<Document> docs = logForms.getLogicalForms();
		    int i = 0;
		    for (Iterator<Document> logFormIter = docs.iterator(); logFormIter.hasNext();) {
		        Document lfDoc = logFormIter.next();
		        String sentence = mr.realizeLF(lfDoc);
		        sentences[i] = postProcess(sentence);
		        i++;
		    }
		}
		log.end("Publisher.describe");
		return sentences;
	}
    
    public boolean addExhibitToCorpus(String id, CorpusOutput corpus) {
        log.start("Publisher.addExhibitToCorpus");
        ArrayList<String> sentences = new ArrayList<String>();
        Realiser r = new Realiser(ds, config);
        LogicalForms logForms;
        try {
            logForms = r.getDescription(id);
        }
        catch (Exception e1) {
            return false;
        }
        TextPlan tp = r.getTP();
        if (logForms == null) {
            return false;
        }
        else {
            ArrayList<Document> docs = logForms.getLogicalForms();
            for (Iterator<Document> logFormIter = docs.iterator(); logFormIter.hasNext();) {
                Document lfDoc = logFormIter.next();
                String sentence;
                try {
                    sentence = mr.realizeLF(lfDoc);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    return false;
                }
                sentences.add(postProcess(sentence));
            }
        }
        if (sentences.size() == 0) {
            return false;
        }
        corpus.addExhibit(id, tp, logForms.getLogicalForms(), sentences);
        return true;
    }
    

	private String postProcess(String s) {
		s = capitalize(s);
		s = s.replace("_", " ");
        s = addFinalPunct(s);
		s = closePunct(s);
		s = doublePunct(s);
		s = fixAn(s);
		s = fixA(s);
		return s;
	}
	
	
	private String capitalize(String s) {
		String head = s.substring(0,1);
		String tail = s.substring(1);
		return head.toUpperCase() + tail;
	}
	
	
	private String closePunct(String s) {
	    s = s.replaceAll("  "," ");
	    s = s.replaceAll(" ,",",");
		s = s.replaceAll(" ;",";");
		s = s.replaceAll(" \\.",".");
		return s;
	}
	
	private String doublePunct(String s) {
		s = s.replaceAll("\\,\\.",".");
		s = s.replaceAll("\\.\\.",".");
		s = s.replaceAll("\\,\\,",",");
		s = s.replaceAll("\\,\\;",";");
		return s;
	}

    private String addFinalPunct(String s) {
	if (!s.endsWith(".")) {
	    s = s + ".";
	}
	return s;
    }
	
	/*
	 * replace an with a if not followed by a vowel.
	 */
	
	private String fixA(String s) {
		Pattern p = Pattern.compile("(.*\\W[Aa])( [a,e,i,o,u,A,E,I,O,U].*)");
		Matcher m = p.matcher(s);
		boolean isThere = m.matches();
		while(m.matches()) {
			s = m.replaceFirst(m.group(1) + "n" + m.group(2));
			m = p.matcher(s);
		}
		return s;
	}


	private String fixAn(String s) {
		Pattern p = Pattern.compile("(.*\\W[Aa])n( [^a,e,i,o,u,A,E,I,O,U].*)");
		Matcher m = p.matcher(s);
		boolean isThere = m.matches();
		while(m.matches()) {
			s = m.replaceFirst(m.group(1) + m.group(2));
			m = p.matcher(s);
		}
		return s;
	}
}
