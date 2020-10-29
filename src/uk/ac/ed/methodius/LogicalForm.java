package uk.ac.ed.methodius;

import org.jdom.Element;
//import org.jdom.Attribute;
//import java.util.Iterator;


@SuppressWarnings("unchecked")

public class LogicalForm {
    private Log log;
    private UserModel um;

    private SingleTPFact fact;
    private Element predNode;
    private NPNode arg1Node;
    private Element arg1NPNode;
    private Element arg2NPNode;
    private Element arg3NPNode;
    private NPNode fullArg1Node;
    private String arg1IDRef;
    private String arg1Num;

    private int idCounter = 0;


    /** Create a logical form for a single fact from the text plan which may contain an idref instead of arg1
     * build a logical form for a single fact
     * This consists of a VerbNode with the verb information, an arg1 NPNode and an arg2 NPNode
     * if we've been passed an idref, use that for arg1
     * otherwise create a new NP for arg1
     */

    public LogicalForm(SingleTPFact f, String idref, UserModel um, Log log, int counter) {
        this.log = log;
        log.start("LogicalForm");
        this.um = um;
        idCounter = counter;
        log.output("idref is " + idref);
        boolean aggAfter = f.isAggregatableAfter();
        Expression exp = f.getExpression();
        log.output("exp is " + exp);
        fact = f;
        // create arg1 first so that referring expressions are in right order
        // arg1Rel may be null if the expression doesn't use it
        TPEntity arg1 = f.getArg1();
        log.output("arg1 is " + arg1);
        arg1NPNode = createArg1NPNode(arg1, idref, exp, aggAfter);        
        Element arg1Rel = null;
        if (arg1Node != null) {
            arg1Num = arg1Node.getNum();


            arg1Rel = createRel("ArgOne");
            Element ownerNode1 = createOwnerNode(exp, 1);
            if (ownerNode1 != null) {
                Element ownerRel = createRel("GenOwner");
                ownerRel.addContent(arg1NPNode);
                ownerNode1.addContent(ownerRel);
                arg1Rel.addContent(ownerNode1);
            }
            else {
                arg1Rel.addContent(arg1NPNode);
            }
        }

        
        // there's always an arg2
        log.output("creating arg2 node");
        TPEntity arg2 = f.getArg2();
        arg2NPNode = createArg2NPNode(arg2, idref, exp, aggAfter);

        // if there's no verb 
        if (exp.getAnything("verb") == null) {
            log.output("no verb");
            predNode = arg2NPNode;
            if (arg1Rel != null) {
                predNode.addContent(arg1Rel);
            }
        }

        // if there is a verb
        else {
            log.output("making verb node");
            predNode = createVerbNode(f, idref, exp);
            predNode.addContent(arg1Rel);

            Element arg2Rel = createRel("ArgTwo");
            predNode.addContent(arg2Rel);

            // if there's a preposition it gets attached to the ArgTwo NP node
            String prep = exp.getAnything("preposition");
            if (prep != null) {
                arg2NPNode.setAttribute("prep", prep);
            }

            // if there's a possessive
            Element ownerNode2 = createOwnerNode(exp, 2);
            if (ownerNode2 != null) {
                Element ownerRel = createRel("GenOwner");
                ownerRel.addContent(arg2NPNode);
                ownerNode2.addContent(ownerRel);
                arg2Rel.addContent(ownerNode2);
            }
            else { // default case
                arg2Rel.addContent(arg2NPNode);
            }

            // if there's an adverb
            String adverb = exp.getAnything("adverb");
            if (adverb != null) {
                Element advRel = createPropRel(adverb);
                predNode.addContent(advRel);
            }
            
            // if there's text
            String text = exp.getAnything("text");
            if (text != null) {
                String textPosition = exp.getAnything("textPosition");
                if (textPosition.equalsIgnoreCase("last") || textPosition.equalsIgnoreCase("first")) {
                    Element textRel = createPropRel(text);
                    predNode.addContent(textRel);
                }
            }
        }

        log.end("LogicalForm");
    }

    /** Create the arg1 part of the logical form
     * Three cases:
     * a) create a full arg1 Node
     * b) create a full arg1 Node for use later but use the associated idref now
     * c) use an idref which has been passed in
     */

    private Element createArg1NPNode(TPEntity arg1, String idref, Expression exp, boolean agg) {
        if (exp.getAnything("arg1Type") == null) {
            log.output("NO ARG ONE");
            return null;
        }
        String arg1Form = arg1.getForm();
        if (arg1Form == null) {
            arg1Form = exp.getAnything("arg1RefExp");
        }
        log.output("arg1Form " + arg1Form);
        if (arg1Form == null) {
            return null;
        }

        log.output("creating arg1 node");
        if (idref == null) {
            // build an NP node for arg1
            arg1Node = new NPNode(arg1, arg1Form, agg, true, idCounter, um, log);
            idCounter++;
        }
        else if (idref.equals("PENDING")) {
            // create an NP node for use later, but use the idref now
            log.output("creating full arg1 node");
            fullArg1Node = new NPNode(arg1, arg1Form, agg, true, idCounter, um, log);
            idCounter++;
            arg1IDRef = fullArg1Node.getIDRef();
            arg1Node = new NPNode(arg1IDRef, log);
        }
        else {
            // use the idref for arg1
            arg1Node = new NPNode(idref, log);
        }
        Element nodeToReturn = arg1Node.getNode();
        return nodeToReturn;
    }
    /** Create the arg2 part of the logical form */

    private Element createArg2NPNode(TPEntity arg2, String idref, Expression exp, boolean aggAfter) {
        String arg2Form = arg2.getForm();
        if (arg2Form == null) {
            arg2Form = exp.getAnything("arg2RefExp");
        }
        if (arg2Form.equals("another")) {
            fact.setAnother(true);
        }
        log.output("arg2Form " + arg2Form);
        NPNode nodeToReturn = new NPNode(arg2, arg2Form, aggAfter, false, idCounter, um, log);
        if (arg1Num != null) {  
            nodeToReturn.setNum(arg1Num);
        }
        Element elToReturn = nodeToReturn.getNode();
        idCounter++;
        return elToReturn;
    }

    /** Create the verb part of the logical form */

    private Element createVerbNode(SingleTPFact f, String idref, Expression exp) {
        String predName = f.getPredicateName();
        Element elToReturn = new VerbNode(predName, exp, log, idCounter).getNode();
        idCounter++;
        if (idref != null) {
            log.output("removing mood attr because of idref");
            elToReturn.removeAttribute("mood");
        }
        return elToReturn;
    }

    /** Create a special node for a noun which "owns" another noun e.g. Macedonia's ruler */
    private Element createOwnerNode(Expression e, int node) {
        Element elToReturn = null;
        if (node == 2) {
            String arg2Owner = e.getAnything("arg2OwnerType");
            if (arg2Owner != null) {
                log.output("arg2 owns " + arg2Owner);
                String arg2OwnerForm = e.getAnything("arg2OwnerRefExp");
                String arg2OwnerNum = e.getAnything("arg2OwnerNum");
                Element ownerNode = new NPNode(arg2Owner, arg2OwnerForm, arg2OwnerNum, log, idCounter).getNode();
                idCounter++;
                elToReturn = ownerNode;
            }
        }
        else if (node == 1) {
            String arg1Owner = e.getAnything("arg1OwnerType");
            if (arg1Owner != null) {
                log.output("arg1 owns " + arg1Owner);
                String arg1OwnerForm = e.getAnything("arg1OwnerRefExp");
                String arg1OwnerNum = e.getAnything("arg1OwnerNum");
                Element ownerNode = new NPNode(arg1Owner, arg1OwnerForm, arg1OwnerNum, log, idCounter).getNode();
                idCounter++;
                elToReturn = ownerNode;
            }
        }
        return elToReturn;
    }


    /** Adds an elaboration fact to an existing logical form e.g.
     * existing fact: "it originates from Attica"
     * new fact: "Attica is in Greece"
     * result: "it originates from Attica, which is in Greece."
     * creates a logical form for the new fact, passing the idref from the existing logical form
     * Then adds the new logical form to the old one
     * The coreInvRel and Trib stuff is how openccg does this
     * @param f new fact to be added to existing LogicalForm
     */

    public void addElabRel(SingleTPFact f) {
        Element coreInvRel = createRel("CoreInv");
        arg2NPNode.addContent(coreInvRel);
        Element elabNode = createNode("elab-rel");
        coreInvRel.addContent(elabNode);
        Element tribRel = createRel("Trib");
        elabNode.addContent(tribRel);
        String coreArg2Id = arg2NPNode.getAttributeValue("id");
        LogicalForm elabLF = new LogicalForm(f, coreArg2Id, um, log, idCounter);
        idCounter++;
        Element elabContent = elabLF.getContent();
        /*
         // reduced relatives for be-verb
          // which don't work at the moment with pps, need fixing in openccg
           if (elabContent.getAttributeValue("pred").equals("be-verb")) {
           elabContent.removeAttribute("tense");
           elabContent.removeAttribute("voice");
           }
         */

        if (f.getPredicateName().equals("type")) {
            elabContent.removeAttribute("tense");
            elabContent.removeAttribute("voice");
        }

        tribRel.addContent(elabContent);
    }

    /** Adds a same verb fact to an existing logical form e.g.
     * existing fact: "it was created during the hellenistic period"
     * new fact: "it was created in 320 B.C."
     * result: "it was created in 320 B.C. during the hellenistic period"
     * adds an extra ArgTwo node to the existing LogicalForm
     * @param f new fact to be added to existing LogicalForm
     */

    public void addSameVerbFact(SingleTPFact f) {
        TPEntity arg3 = f.getArg2();
        Expression exp = f.getExpression();
        String arg3Form = exp.getAnything("arg2RefExp");
        Element arg3El = createRel("ArgTwo");
        predNode.addContent(arg3El);
        arg3NPNode = new NPNode(arg3, arg3Form, true, false, idCounter, um, log).getNode();
        idCounter++;
        String prep = exp.getAnything("preposition");
        if (prep != null) {
            arg3NPNode.setAttribute("prep", prep);
        }
        arg3El.addContent(arg3NPNode);
        
        String adverb = exp.getAnything("adverb");
        if (adverb != null) {
            Element advRel = createPropRel(adverb);
            predNode.addContent(advRel);
        }
        
        // ADD THE OWNER STUFF!!!!!
        
        // if there's text
        String text = exp.getAnything("text");
        if (text != null) {
            String textPosition = exp.getAnything("textPosition");
            if (textPosition.equalsIgnoreCase("last") || textPosition.equalsIgnoreCase("first")) {
                Element textRel = createPropRel(text);
                predNode.addContent(textRel);
            }
        }
    }

    /** Add another fact to an existing logical form using "and" with repeated pronoun 
     * e.g. it was created in 320 B.C. and it was painted with the red-figure technique
     */

    public void addAndFact(LogicalForm secondForm, String type) {
        Element secondVerbNode = secondForm.getContent();
        secondVerbNode.removeAttribute("mood");

        predNode.removeAttribute("mood");

        Element newPred = createNode(type);
        newPred.setAttribute("mood", "dcl");
        Element firstAnd = createRel("Arg1");
        firstAnd.addContent((Element)predNode.clone());
        Element secondAnd = createRel("Arg2");
        secondAnd.addContent(secondVerbNode);

        newPred.addContent(firstAnd);
        newPred.addContent(secondAnd);
        predNode = newPred;
    }

    /** Add another fact to an exising logical form using "and" without repeated pronoun 
     * e.g. it was created in 320 B.C. and was painted with the red-figure technique
     */


    public void addListFact(LogicalForm secondForm, String type) {
        log.output("adding a list fact to form which has arg1idref " + arg1IDRef);
        Element secondVerbNode = secondForm.getContent();
        secondVerbNode.removeAttribute("mood");

        predNode.removeAttribute("mood");

        Element newPred = createNode("and");
        newPred.setAttribute("mood", "dcl");
        Element firstAnd = createRel("Arg1");
        firstAnd.addContent((Element)predNode.clone());
        Element secondAnd = createRel("Arg2");
        secondAnd.addContent(secondVerbNode);

        newPred.addContent(firstAnd);
        newPred.addContent(secondAnd);
        predNode = newPred;
    }

    /** Add a comparison to an existing logical form
     * can be a "like" or "unlike" e.g.
     * Like the previous vessel, this amphora was created during the archaic period.
     * Unlike the previous vessels, which were created during the hellenistic period, this amphora was created during the archaic period.
     */

    public void addComparison(LogicalForm oldForm, String type, boolean mostRecent, TPFact oldFact) {
        log.start("add comparison");

        NPNode oldArg1Form = oldForm.getArg1Node();
        String oldIDRef = oldArg1Form.getIDRef();
        boolean previous = oldArg1Form.getPrevious();
        boolean names = oldArg1Form.getNames();
        log.output("oldArg1Form is " + oldArg1Form);
        log.output("previous of old form is " + previous);
        log.output("most recent is " + mostRecent);
        log.output("names of old form is " + names);
        Element oldFormNP = (Element)oldForm.getArg1NPNode().clone();

        // "unlike" comparison - add info about things we're comparing with since it's different from current object 
        if (type.equals("unlike")) {
            log.output("old form is " + oldForm);
            NPNode oldFormNPNode = oldForm.getFullArg1Node();
            oldIDRef = oldFormNPNode.getIDRef();
            log.output("old form np node is " + oldFormNPNode);
            if (oldFormNPNode != null) {
                oldFormNP = oldFormNPNode.getNode();
                log.output("old form np is " + oldFormNP.toString());
                previous = oldFormNPNode.getPrevious();
                names = oldFormNPNode.getNames();
                log.output("previous is " + previous);
            }
            Element oldFormPred = (Element)oldForm.getContent();
            oldFormPred.removeAttribute("mood");
            Element coreInvRel = createRel("CoreInv");
            oldFormNP.addContent(coreInvRel);
            Element elabNode = createNode("elab-rel");
            coreInvRel.addContent(elabNode);
            Element tribRel = createRel("Trib");
            elabNode.addContent(tribRel);
            tribRel.addContent(oldFormPred);
        }

        if (!names && !mostRecent) {
            log.output("creating you saw rel");
            // add "you saw"
            Element youSawEl = createYouSaw(oldIDRef);
            oldFormNP.addContent(youSawEl);

            // if we can refer to the other object as the previous X
            if (previous) {
                oldFact.setAnother(true);
                Element prevRel = createPropRel("other");
                oldFormNP.addContent(prevRel);
                Element recentRel = createPropRel("recently");
                youSawEl.addContent(recentRel);

            }
            /*	    else if (mostRecent) {
		Element justRel = createPropRel("just");
		youSawEl.addContent(justRel);
		}*/
            else {
                Element recentRel = createPropRel("recently");
                youSawEl.addContent(recentRel);
            }
        }

        else if (mostRecent && previous) {
            Element prevRel = createPropRel("previous");
            oldFormNP.addContent(prevRel);
        }

        Element comparatorRel = createRel("Comparator");
        comparatorRel.addContent(oldFormNP);

        Element focusRel = createRel("Focus");
        predNode.removeAttribute("mood");
        focusRel.addContent((Element)predNode.clone());

        Element newPred = createNode(type);
        newPred.setAttribute("mood", "dcl");
        newPred.addContent(comparatorRel);
        newPred.addContent(focusRel);
        predNode = newPred;
        log.end("add comparison");
    }

    /** creates a logical form which will add "you saw" to an object.  e.g.
     *	" like the previous amphora you saw, this amphora ..."
     */

    private Element createYouSaw(String oldIDRef) {
        Element genRelEl = createRel("RedGenRel");
        Element sawVerb = new VerbNode("see-verb", null, "past", "active", log, idCounter).getNode();
        idCounter++;
        genRelEl.addContent(sawVerb);
        Element arg1Rel = createRel("ArgOne");
        sawVerb.addContent(arg1Rel);
        Element youNode = createNode("pro2", "animate-being");
        arg1Rel.addContent(youNode);
        Element arg2Rel = createRel("ArgTwo");
        Element itNode = new Element("node");
        itNode.setAttribute("idref", oldIDRef);
        sawVerb.addContent(arg2Rel);
        arg2Rel.addContent(itNode);
        return genRelEl;
    }

    /** helper method to create an openccg HasProp rel */

    private Element createPropRel(String prop) {
        Element rel = createRel("HasProp");
        Element propNode = createNode(prop);
        rel.addContent(propNode);
        return rel;

    }

    /** helper method to create an openccg Name rel */

    private Element createRel(String name) {
        Element rel = new Element("rel");
        rel.setAttribute("name", name);
        return rel;
    }

    /** helper method to create an openccg node with a given pred attribute and create a unique id */

    private Element createNode(String pred) {
        Element node = new Element("node");
        node.setAttribute("pred", pred);
        node.setAttribute("id", pred + idCounter++);
        idCounter++;
        return node;
    }

    private Element createNode(String pred, String type) {
        Element node = new Element("node");
        node.setAttribute("pred", pred);
        node.setAttribute("id", pred + idCounter++ + ":" + type);
        idCounter++;
        return node;
    }

    /** returns the top level node of the logical form */

    public Element getContent() {
        return predNode;
    }

    /** returns the arg1 NP node of the logical form */

    public NPNode getArg1Node() {
        return arg1Node;
    }

    /** returns the arg1 node of the logical form */

    public Element getArg1NPNode() {
        return arg1NPNode;
    }

    /** returns the arg2 node of the logical form */

    public Element getArg2NPNode() {
        return arg2NPNode;
    }

    /** returns the arg1 idref of an NP which has already been used */

    public String getArg1IDRef() {
        return arg1IDRef;
    }

    /** returns the arg1 idref of an NP which has already been used */

    public NPNode getFullArg1Node() {
        return fullArg1Node;
    }

    public int getCounter() {
        return idCounter;
    }

}

