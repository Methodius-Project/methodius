package uk.ac.ed.methodius;

import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;

public class LogicalForms {


    private ArrayList<Document> logicalForms = null;
    private UserModel um = null;
    private Log log = null;
    private int counter = 0;

    /** Create a list of XML Documents objects from the TextPlan
     * Each Document will be sent separately to openCCG
     *
     * A Document must contains one top-level Logical Form and may have
     * optional embedded LogicalForms for different types of sentences
     * with multiple facts
     */

    public LogicalForms(TextPlan textPlan, UserModel um, Log log) {
        log.start("LogicalForms");
        this.um = um;
        this.log = log;
        logicalForms = new ArrayList<Document>();

        for (Iterator planIter = textPlan.getPlan().iterator(); planIter.hasNext();) {
            TPFact tpf = (TPFact)planIter.next();
            LogicalForm lf = makeLogicalForm(tpf);
            if (lf != null) {
                Element xmlEl = new Element("xml");
                Document lfDoc = new Document(xmlEl);
                Element lfEl = new Element("lf");
                xmlEl.addContent(lfEl);
                lfEl.addContent(lf.getContent());
                logicalForms.add(lfDoc);
            }
        }
        log.end("LogicalForms");
    }

    /** Create a single LogicalForm using an idref */

    private LogicalForm makeLogicalForm(TPFact f, String idref) {
        log.start("makeLogicalForm");
        log.output("idref is " + idref);
        LogicalForm lf = new LogicalForm((SingleTPFact)f, idref, um, log, counter);
        log.end("makeLogicalForm");
        return lf;
    }

    /** Create a single LogicalForm which may include embedded LogicalForm objects */

    private LogicalForm makeLogicalForm(TPFact f) {
        log.start("makeLogicalForm");
        LogicalForm lf = null;
        if (f.isSingle()) { // single fact
            log.output("logical form for single fact");
            lf = new LogicalForm((SingleTPFact)f, null, um, log, counter);
        }
        else { // multiple facts
            MultipleTPFact fg = (MultipleTPFact)f;
            String type = fg.getType();
            // if it's an elaboration e.g. Attica, which is in Greece
            if (type.equals("type") || type.equals("dep-group")) {
                TPFact tpf = fg.getFact1();
                if (tpf.isSingle()) {
                    lf = new LogicalForm((SingleTPFact)tpf, null, um, log, counter);
                    TPFact tpf2 = fg.getFact2();
                    if (tpf2.isSingle()) {
                        lf.addElabRel((SingleTPFact)tpf2);
                    }
                    else {
                        log.output("can't aggregate elab with more than 2 yet");
                    }
                }
                else {
                    log.output("can't aggregate elab with more than 2 yet");
                }
            }
            // if we have two facts with same verb
            else if (type.equals("same-verb")) {
                TPFact tpf = fg.getFact1();
                TPFact tpf2 = fg.getFact2();
                if (tpf2.isSingle()) {
                    lf = makeLogicalForm(tpf);
                    lf.addSameVerbFact((SingleTPFact)tpf2);
                }
                else if (tpf.isSingle()) {
                    lf = makeLogicalForm(tpf2);
                    lf.addSameVerbFact((SingleTPFact)tpf);
                }
                else {
                    log.output("can't aggregate same-verb with two multiple facts (yet?)");
                }
            }
            // if we're joining facts with and or semicolon (repeated pronoun)
            else if (type.equals("and") || type.equals(";")) {
                TPFact tpf1 = fg.getFact1();
                TPFact tpf2 = fg.getFact2();
                lf = makeLogicalForm(tpf1);
                counter = lf.getCounter();
                LogicalForm lf2 = makeLogicalForm(tpf2);
                lf.addAndFact(lf2, type);
            }
            // if we're joining facts into a list (no repeated pronoun)
            else if (type.equals("list")) {
                TPFact tpf1 = fg.getFact1();
                TPFact tpf2 = fg.getFact2();
                log.output("creating log form with pending");
                lf = makeLogicalForm(tpf1, "PENDING");
                counter = lf.getCounter();
                log.output("doing next log form with idref");
                LogicalForm lf2 = makeLogicalForm(tpf2, lf.getArg1IDRef());
                lf.addListFact(lf2, type);
            }
            // if we're creating a comparison
            else if (type.equals("like") || type.equals("unlike")) {
                TPFact oldFact = fg.getFact1();
                TPFact newFact = fg.getFact2();
                LogicalForm oldFactLF;
                if (type.equals("unlike") && oldFact.isSingle()) {
                    log.output("creating log form with pending");
                    oldFactLF = makeLogicalForm(oldFact, "PENDING");
                }
                else {
                    oldFactLF = makeLogicalForm(oldFact);
                }
                counter = oldFactLF.getCounter();
                lf = makeLogicalForm(newFact);
                log.output("old fact top arg1 is " + oldFact.getTopArg1());

                lf.addComparison(oldFactLF, type, oldFact.getTopArg1().isMostRecent(), oldFact);
            }
            else {
                log.output("not able to deal with this kind of relation yet!");
            }
        }
        log.end("makeLogicalForm");
        return lf;
    }

    /** Returns an ordered list of XML Documents to be sent to the openCCG realizer */

    public ArrayList<Document> getLogicalForms() {
        return logicalForms;
    }
}