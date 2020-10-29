package uk.ac.ed.methodius;

import org.jdom.Element;
import java.util.List;
import java.util.Iterator;
import uk.ac.ed.methodius.ReferringExpression.Single;

@SuppressWarnings("unchecked")

public class NPNode {
    private Log log;
    private Element npNode;
    private String idref;
    private String topIdref;
    private String argForm;
    private String typeName;
    private String nodeNum;
    private TPEntity e;
    private int counter;
    private boolean previous = false;
    private boolean names = false;

    /** Create an NPNode using an idref which has been created elsewhere */

    public NPNode(String idref, Log log) {
        this.log = log;
        log.start("NPNode");
        log.output("using idref " + idref);
        npNode = new Element("node");
        npNode.setAttribute("idref", idref);
        topIdref = idref;
        log.end("NPNode");
    }

    /** Create an NPNode for a special noun which isn't an entity
     * Create a ReferringExpression, but don't add it to the history
     * because it's not an entity or string from the database
     */

    public NPNode(String owner, String ownerForm, String ownerNum, Log log, int counter) {
        this.log = log;
        this.counter = counter;
        log.start("NPNode");
        npNode = new Element("node");
        idref = owner + "_" + ownerForm + counter + ":sem-obj";
        npNode.setAttribute("id", idref);
        ReferringExpression refExp = new ReferringExpression(owner, ownerForm, ownerNum, log);
        List singles = refExp.getSingles();

        for (Iterator singleIter = singles.iterator(); singleIter.hasNext();) {
            Single s = (Single)singleIter.next();

            // don't add this one to the history as it's not relevant
            npNode.setAttribute("pred", owner);
            npNode.setAttribute("det", s.getDet());
            npNode.setAttribute("num", s.getNum());
            log.end("NPNode");
        }
        topIdref = idref;
    }

    /** make a normal NPNode which will refer to something from the
     * database, either entity or string Create a ReferringExpression
     * which does all the work and add that to a history list to use
     * in creating future ReferringExpressions
     */

    public NPNode(TPEntity e, String argForm, boolean agg, boolean focal, int counter, UserModel um,  Log log) {
        this.log = log;
        log.start("NPNode");
        this.counter = counter;
        this.e = e;
        Type type = e.getType();
        //	String typeName;
        if (type == null) {
            typeName = "sem-obj";
        }
        else {
            typeName = type.getName();
        }

        this.argForm = argForm;
        log.output("entity is " + e);
        log.output("arg form for " + e.getId() + " is " + argForm);
        ReferringExpressionHistory previousExpressions = um.getReferringExpressionHistory();
        ReferringExpression refExp = new ReferringExpression(e, argForm, agg, focal, um, previousExpressions, log);
        previousExpressions.add(refExp);
        log.output("adding expression to history");

        npNode = new Element("node");

        List singles = refExp.getSingles();
        names = refExp.isNames();
        if (singles.size() > 1 && names) {
            npNode.setAttribute("pred", "and");
            topIdref = "and" + counter + ":sem-obj";
            counter++;
            npNode.setAttribute("id", topIdref);
            Element relNode = new Element("rel");
            relNode.setAttribute("name", "List");
            npNode.addContent(relNode);
            for (Iterator singleIter = singles.iterator(); singleIter.hasNext();) {
                Single s = (Single)singleIter.next();
                Element singleNode = makeSingleNode(s);
                relNode.addContent(singleNode);
            }
        }
        else {
            for (Iterator singleIter = singles.iterator(); singleIter.hasNext();) {
                Single s = (Single)singleIter.next();
                npNode = makeSingleNode(s);
                nodeNum = npNode.getAttributeValue("num");
            }
            topIdref = idref;
        }
        previous = refExp.getPrevious();
        log.output("previous is " + previous);
        log.end("NPNode");
    }

    private Element makeSingleNode(Single s) {
        Element nodeToReturn = new Element("node");

        idref = e.getId() + "_" + argForm + counter + ":" + typeName;
//        idref = e.getId() + "_" + argForm + counter + ":sem-obj";
        counter++;

        log.output("idref is " + idref);
        nodeToReturn.setAttribute("id", idref);

        String pred = s.getPred();
        if (pred != null) {
            nodeToReturn.setAttribute("pred", pred);
        }
        String det = s.getDet();
        if (det != null) {
            nodeToReturn.setAttribute("det", det);
        }
        String num = s.getNum();
        if (num != null) {
            nodeToReturn.setAttribute("num", num);
        }
        return nodeToReturn;
    }

    /** return the XML element to be used for this NPNode */

    public Element getNode() {
        return npNode;
    }


    /** the idref used for this NP or which can be used in future */

    public String getIDRef() {
        return topIdref;
    }

    /** can we refer to this NP as "the previous X" */

    public boolean getPrevious() {
        return previous;
    }

    public boolean getNames() {
        return names;
    }
    
    public String getNum() {
        return nodeNum;
    }

    public void setNum(String newNum) {
        nodeNum = newNum;
    }

    public String toString() {
        return "idref " + idref + " argForm " + argForm + " typeName " + typeName + " previous " + previous + " names " + names + " num " + nodeNum;
    }

}