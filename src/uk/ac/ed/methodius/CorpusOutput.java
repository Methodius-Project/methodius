package uk.ac.ed.methodius;

import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class CorpusOutput {
    
    private Document outputDoc;
    private Element rootEl;

    public CorpusOutput() {
        outputDoc = new Document();
        rootEl = new Element("exhibit-descriptions");
        outputDoc.setRootElement(rootEl);
    }
    
    public void addExhibit(String exhibitId, TextPlan contentPlan, ArrayList<Document> logicalForms, ArrayList<String> text) {
        Element exhibitEl = new Element("exhibit");
        exhibitEl.setAttribute("id", exhibitId);
        exhibitEl.addContent(contentPlan.toXML());
        Element lfsEl = new Element("logical-forms");
        exhibitEl.addContent(lfsEl);
        for (Document d : logicalForms) {
            Element rootEl = d.detachRootElement();
            Element lfEl = rootEl.getChild("lf");
            lfEl.detach();
            lfsEl.addContent(lfEl);
        }
        Element textEl = new Element("text");
        exhibitEl.addContent(textEl);
        for (String s : text) { 
            textEl.addContent(s);
            textEl.addContent(" ");
        }
        rootEl.addContent(exhibitEl);
    }
    
    public Document getDoc() {
        return outputDoc;
    }
    
    public String getDocString() {
        Format f = Format.getPrettyFormat();
        f.setLineSeparator("\n");
        XMLOutputter xo = new XMLOutputter(f);
        return xo.outputString(outputDoc);
    }
    
}
