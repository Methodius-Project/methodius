package uk.ac.ed.methodius;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import uk.ac.ed.methodius.Exceptions.DataStoreException;



/**
 *
 * Predicates are stored in the PredicateHandler.
 * It is a HashMap indexed by predicate ID.
 * Each Predicate ID maps to a HashMap which is indexed by arg1Type.
 * Thus mapping (Predicate ID x Arg1Type) -> Predicate
 */

@SuppressWarnings("unchecked")

public class PredicateHandler {

    Set compPreds = null;
    DataStoreRead ds;
    private Log log = null;

    public PredicateHandler(DataStoreRead ds, Configuration config) {
        log = config.getLog();
        this.ds = ds;
        compPreds = new HashSet();
    }


    public void write(Element node, PrintStream out) {
        Util.write(node, "", out);
    }

    /**
     * Retrieve the Predicate for a given pred and fact.
     * 
     * @param predName
     * @param factId
     * @return
     */
    
    public Predicate getPredicate(String predName, String factId) {
        Predicate p = null;
        try {
            p = ds.getPredicate(predName, factId);
        } catch (DataStoreException e) {
            e.printStackTrace();
        }
        return p;
    }
    
    public Set getCompPreds() {
        return compPreds;
    }
}
