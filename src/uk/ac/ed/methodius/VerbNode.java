package uk.ac.ed.methodius;

import org.jdom.Element;
@SuppressWarnings("unchecked")
public class VerbNode {
//  private Log log = null;
    private Element predNode = null;

    /** Create a verb node for a LogicalForm */

    public VerbNode(String predName, Expression e, Log log, int counter) {
//      this.log = log;
        log.start("VerbNode");
        predNode = new Element("node");
        predNode.setAttribute("id", predName + counter + ":situation");
        String verb = e.getAnything("verb");
        predNode.setAttribute("pred", verb);

        String mood = e.getAnything("mood");
        if (mood != null) {
            predNode.setAttribute("mood", mood);
        }
        String tense = e.getAnything("tense");
        if (tense != null) {
            predNode.setAttribute("tense", tense);
        }
        String voice = e.getAnything("voice");
        if (voice != null) {
            predNode.setAttribute("voice", voice);
        }
        log.end("VerbNode");
    }

    public VerbNode(String predName, String mood, String tense, String voice, Log log, int counter) {
//      this.log = log;
        log.start("VerbNode");
        predNode = new Element("node");
        predNode.setAttribute("id", predName + counter + ":situation");
        predNode.setAttribute("pred", predName);
        if (mood != null) {
            predNode.setAttribute("mood", mood);
        }
        if (tense != null) {
            predNode.setAttribute("tense", tense);
        }
        if (voice != null) {
            predNode.setAttribute("voice", voice);
        }
        log.end("VerbNode");
    }

    public Element getNode() {
        return predNode;
    }
}