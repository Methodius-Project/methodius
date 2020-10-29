package uk.ac.ed.methodius;

import java.util.List;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.HashSet;
/**
 * Realiser encapsulates a set of logical forms for an object description.
 *
 */

@SuppressWarnings("unchecked")
public class Realiser {

    private DataStoreRead ds = null;
    private Configuration config = null;
    private UserModel um = null;
    private TextPlan tp = null;
    private Log log = null;

    public Realiser(DataStoreRead d, Configuration c) {
        ds = d;
        config = c;
        um = config.getUserModel();
        log = config.getLog();
    }

    /**
     * given an object id return an LFarray containing an array of logical
     * forms for the object description.
     * @param id
     * @return
     * @throws Exception
     */
    public LogicalForms getDescription(String id) throws Exception {
        log.start("Realiser.getDescription");
        ContentPotential cp = null;

        cp = new ContentPotential(ds, id, config);

        Entity focus = cp.getRoot();

//      List previousFocuses = um.getAllFocalHistory();

        um.setPageFocus(focus);
        um.setReferringExpressionHistory(new ReferringExpressionHistory());
        Comparison bestComp = null;
        if (um.doComparisons()) {
            Comparisons comps = new Comparisons(um, cp, log);
            bestComp = comps.getBestComp();
        }

        int tSize = um.getTargetSize();
        log.output("targetsize = " + tSize);
        HashSet<Fact> compFacts = new HashSet<Fact>();
        if (bestComp != null) {
            compFacts.add(bestComp.getFocalFact());
            if (bestComp.hasMultipleFacts()) {
                compFacts.add(bestComp.getOtherComparison().getFocalFact());
            }
        }
        List cl = cp.getNFacts(tSize, false, compFacts);

        if (cl.isEmpty()) {
            return null;
        }
        tp = new TextPlan(cl, bestComp, config, log);


        log.output("TextPlan returned is:" );
        log.output(tp.toString());
        
        
//        log.output("XML plan is");
 //       log.output(xo.outputString(tp.toXML()));

        LogicalForms logForms = new LogicalForms(tp, um, log);

        log.output("Storing focus and facts and comparison in user model" );
        um.storeFactsHistory(focus, cl);

        log.end("Realiser.getDescription");
        return logForms;
    }
    
    public TextPlan getTP() {
        return tp;
    }
}
