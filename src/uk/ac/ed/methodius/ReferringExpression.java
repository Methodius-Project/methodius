package uk.ac.ed.methodius;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;


/** Class to create a Referring Expression for an entity or piece of
 * canned text based on the previous referring expressions in this
 * description and focus information.
 */

public class ReferringExpression {

    /* constants */

    private static final int DEF = 1; // the amphora(s)
    private static final int INDEF = 2; // an amphora /some amphoras
    private static final int ANOTHER = 3; // another amphora
    private static final int NILDET = 4; // amphoras
    private static final int PROX = 5; // this/these amphora(s)
    private static final int NONPROX = 6; // that/those amphora(s)
    private static final int PROXPRON = 7; // this/these
    private static final int PRON = 8; // it/he/she/they
    private static final int NAME = 9; // Alexander the Great (and his friend)
    private static final int SHORTNAME = 10; // Alexander (and friend)
    private static final int CANNED = 11; // canned text
    private static final int FAILED = 13; // in case anything went wrong (for testing)

    private static final int MAXPRONOUNS = 10;

    /* instance variables */

    private TPEntity thisEntity;
    private ReferringExpressionHistory previousExpressions;
    private boolean focal = false;
    private boolean aggregatable = false;
    private UserModel um;
    private int refType = FAILED;
    private List<Single> singles = new LinkedList<Single>();;
    private boolean usePrevious = false;
    private Log log;

    /** <p>Creates a referring expression for something which isn't
     * an entity - it comes from the predicates file as a noun -
     * currently can only be something that is owned by an arg2</p>
     * @param noun the noun
     * @param nounForm definite, indefinite etc
     * @param nounNum the number (sg/pl)
     * @param log the log file
     **/

    public ReferringExpression(String noun, String nounForm, String nounNum, Log log) {
        this.log = log;
        log.start("ReferringExpression");
        previousExpressions = new ReferringExpressionHistory();
        log.output("expression for genowner");
        refType = convertRefType(nounForm);
        Single e = createExpression(null, refType, null, noun, nounNum, null);
        singles.add(e);
        log.end("ReferringExpression");
    }

    /** <p>Creates a fully-specified referring expression which may use
     * a specific referring expression type (e.g. pronoun) and entity
     * type (e.g. exhibit)</p>
     * <p>If the sort of expression has not been
     * specified, selects it, then creates the referring expression</p>
     * @param ent the Entity(s) we want to refer to
     * @param foc whether this entity is currently the focal entity
     * @param aggAfter can we aggregate after this entity
     * @param hist an ordered list of all the previous
     * referring expressions already created for this description.
     * @param ref the sort of referring expression
     * @param type the Type
     */


    public ReferringExpression(TPEntity ent, String form, boolean aggAfter, boolean foc, UserModel um, ReferringExpressionHistory hist, Log log) {
        this.log = log;
        log.start("ReferringExpression");
        thisEntity = ent;
        this.um = um;
        previousExpressions = hist;
        focal = foc;
        log.output("entity focal is " + focal);
        aggregatable = aggAfter;
        log.output("this exp can be aggregated after " + aggregatable);

        // Use the type which has been assigned to the TPEntity (may be null)
        Type entType = thisEntity.getType();
        log.output("entity type is " + entType);

        
        // If the Referring Expression type hasn't been assigned in advance, choose it
        if (form == null || form.equals("default")) {
            refType = selectRefType(ent);
        }
        else {
            refType = convertRefType(form);
        }
        log.output("reftype " + refType);


        // Create the expressions
        List<Entity> entities = ent.getEntities();
        //	if (refType == NAME && entities.size() > 1) {
        if (refType == NAME) {
            log.output("expressions for names");
            for (Iterator<Entity> entIter = entities.iterator(); entIter.hasNext();) {
                Entity e = entIter.next();
                log.output("making an expression for " + e);
                String number = "sg";
                entType = e.getType();
                log.output("this single entity type is " + entType);
                Single se = createExpression(e, refType, entType, null, number, e.getGender());
                log.output("expression is " + se);
                singles.add(se);
            }
        }

        else {
            log.output("expression for type or pref");
            Entity e = entities.get(0);
            String number = e.getNumber();
            if (number == null) {
                number = "sg";
            }
            if (entities.size() > 1) {
                number = "pl";
            }
            if (e.isGeneric()) {
                number = "pl";
            }
            if (refType == CANNED) {
                number = null;
            }
            Single se = createExpression(e, refType, entType, null, number, e.getGender());
            singles.add(se);
        }
        log.output("singles are " + singles);
        log.end("ReferringExpression");
    }


    /** Chooses appropriate referring expression type, based on the
     * previous expressions used, and focal information
     @return a constant ref type
     */

    private int selectRefType(TPEntity tpe) {
        log.output("selecting ref type");
        int newType = FAILED;
        int histSize;

        if (previousExpressions == null) {
            histSize = 0;
        }
        else {
            histSize = previousExpressions.getSize();
        }
        log.output(histSize + " previous expressions");
        // canned text string
        
        if (tpe.isCanned()) {
            newType = CANNED;
        }
        // no previous expressions
        else if (histSize == 0) {
            newType = getNameOrProx();
        }
        else {
            // sentences with too many pronouns in a row sound weird, check how many consecutive ones we have had
            int prons = 0;
            boolean tooManyPronounsAlready = false;
            if (focal) {
                for (Iterator histIter = previousExpressions.getHistory().iterator(); histIter.hasNext();) {
                    ReferringExpression re = (ReferringExpression)histIter.next();
                    if (re.getEntity().equals(thisEntity)) {
                        if (re.getRefType() == PRON) {
                            prons++;
                            if (prons == MAXPRONOUNS) {
                                tooManyPronounsAlready = true;
                                break;
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                log.output("previous consecutive focal pronouns: " + prons);
            }
            ReferringExpression prevExp = previousExpressions.getMostRecent();
            // find out what the last focal thing was
            ReferringExpression focRef = previousExpressions.getMostRecentFocal();
            if (focRef == null) {
                newType = getNameOrProx();
            }
            else {
                TPEntity lastFocalEntity = focRef.getEntity();
                log.output("last focal was " + lastFocalEntity.getId());
                boolean sameEntity = prevExp.getEntity().equals(thisEntity);
                boolean sameEntityAsLastFocus = lastFocalEntity.equals(thisEntity);

                // previous expression was canned text				
                if (prevExp.isCannedText()){
                    log.output("previous was canned text");

                    if (prevExp.isAggregatable() && sameEntityAsLastFocus && !tooManyPronounsAlready) {
                        newType = PRON;
                    }
                    // previous expression was canned text and might have several sentences
                    else {
                        newType = getShortNameOrProx(); // AI 12/07/18 why not getNameOrProx()?
                    }
                }

                else if (sameEntity) {
                    log.output("same entity as previous");
                    // we have same entity as previous, and both are focal
                    if (focal) {
                        // we have same entity as last focus
                        if (sameEntityAsLastFocus && !tooManyPronounsAlready) {
                            newType = PRON;
                        }
                        // we have different entity from last focus
                        else {
                            newType = getShortNameOrProx();
                        }
                    }
                    // we have same entity as previous and at least one of them not focal
                    else {
                        newType = getShortNameOrProx();
                    }
                }
                // default
                else {
                    // if last focal entity was the same as this one
                    if (focal && lastFocalEntity.equals(thisEntity) && !tooManyPronounsAlready) {
                        newType = PRON;
                    }
                    else if (focal) {
                        newType = getNameOrProx();
                    }
                    else {
                        newType = getNameOrIndef();
                    }
                }
            }
        }
        return newType;
    }

    /** Create an expression given the entity and type of expression.
     * Sets pred and det as appropriate, one or other may remain null,
     * but never both */

    private Single createExpression(Entity theEnt, int ref, Type type, String noun, String number, String gender) {
        String det = null;
        String pred = null;

        switch(ref) {
        case DEF :
            pred = chooseNP(type, noun);
            det = "def";
            break;
        case INDEF :
            pred = chooseNP(type, noun);
            det = "indef";
            break;
        case ANOTHER :
            pred = chooseNP(type, noun);
            det = "another";
            break;
        case NILDET :
            pred = chooseNP(type, noun);
            det = "nil";
            break;
        case PROX :
            pred =  chooseNP(type, noun);
            det = "dem-prox";
            break;
        case NONPROX :
            pred =  chooseNP(type, noun);
            det = "dem-nonprox";
            break;
        case PROXPRON :
            pred = "this";
            break;
        case PRON :
            if (gender == null) {
                pred = "pro3n";
            }
            else if (gender.equals("male")) {
                pred = "pro3m";
            }
            else if (gender.equals("female")) {
                pred = "pro3f";
            }
            else {
                pred = "pro3n"; // just in case
            }
            break;
        case NAME :
            pred = theEnt.getName();
            if (pred == null) {
                pred = chooseNP(type, noun);
                det = "indef";
            }
            break;
        case SHORTNAME :
            log.output("now the ent is " + theEnt);
            log.output("short name is " + theEnt.getShortName());
            pred = theEnt.getShortName();
            break;
        case CANNED :
//            pred = theEnt.getId();
            pred = theEnt.getName();
            break;
        default :
            pred = "FAILED:"+ theEnt.getId();
        }
        return new Single(pred, det, number, ref);
    }

    /** converts an incoming string which expresses a sort of
     * referring expression into the right representation for this
     * class.
     * @param incoming reference String
     * @return constant int representation for the type
     */

    private int convertRefType(String ref) {
        int convertedType = FAILED;
        if (ref.equals("def")) {
            convertedType = DEF;
        }
        else if (ref.equals("indef")) {
            convertedType = INDEF;
        }
        else if (ref.equals("another")) {
            convertedType = ANOTHER;
        }
        else if (ref.equals("proximal-np")) {
            convertedType = PROX;
        }
        else if (ref.equals("non-proximal")) {
            convertedType = NONPROX;
        }
        else if (ref.equals("proximal-pronoun")) {
            convertedType = PROXPRON;
        }
        else if (ref.equals("refer-by-pronoun")) {
            convertedType = PRON;
        }
        else if (ref.equals("name")) {
            convertedType = NAME;
        }
        else if (ref.equals("shortname")) {
            convertedType = SHORTNAME;
        }
        else if (ref.equals("generic")) {
            convertedType = NILDET;
        }
        return convertedType;
    }

    private String chooseNP(Type entType, String pred) {
        String bestNp = pred;
        if (bestNp == null) {
            LinkedList nps = (LinkedList)entType.getNps();
            int topSig = 0;
            for (Iterator npIter = nps.iterator(); npIter.hasNext();) {
//                String np = ((NP)npIter.next()).getNp();
                String np = (String)npIter.next();
                int sig = um.getNpSignificance(np);
                if (sig > topSig) {
                    topSig = sig;
                    bestNp = np;
                }
            }
        }
        if (bestNp == null) {
            bestNp = "thing-np";
        }
        // if we referred to the ent(s) by type, check whether we want to add "previous"
        usePrevious = checkPrevious(entType);
        return bestNp;
    }

    private boolean checkPrevious(Type entTypeUsed) {
        log.start("checkPrevious");
        boolean prev = false;
        if (entTypeUsed != null) {
            Entity pageFocus = um.getPageFocus();
            log.output("page focus is " + pageFocus.getId());
            HashSet<Type> ancestors = pageFocus.getType().getAllAncestors();
            ancestors.add(pageFocus.getType());
            log.output("ancestors are " + ancestors);
            log.output("ent type used is " + entTypeUsed);
            if (ancestors.contains(entTypeUsed)) {
                prev = true;
                log.output("using previous");
            }
        }
        log.end("checkPrevious");
        return prev;
    }

    /** is this referring expression focal
     * @return boolean focal
     */

    public boolean isFocal() {
        return focal;
    }

    /** does this referring expression contain canned text
     * @return boolean canned
     */

    public boolean isCannedText() {
        for (Iterator<Single> singleIter = singles.iterator(); singleIter.hasNext();) {
            if (singleIter.next().isCannedText()) {
                return true;
            }
        }
        return false;
    }

    /** can this referring expression be aggregated?
     * @return boolean aggregatable
     */

    public boolean isAggregatable() {
        return aggregatable;
    }

    /** returns the entity which this expression refers to
     * @return Entity
     */

    public TPEntity getEntity() {
        return thisEntity;
    }

    /** returns the constant int representation of the referring expression type.
     * @return constant int
     */

    public int getRefType() {
        return refType;
    }

    public boolean isNames() {
        return refType == NAME || refType == SHORTNAME;
    }

    public boolean getPrevious() {
        return usePrevious;
    }

    
    private int getNameOrIndef() {
        int newType;
        if (thisEntity.getName() != null && !thisEntity.getName().equals("")) {
            log.output("choosing name");
            newType = NAME;
        }
        else {
            log.output("choosing indef");
            newType = INDEF;
        }
        return newType;
    }
    
    private int getNameOrProx() {
        int newType;
        if (thisEntity.getName() != null && !thisEntity.getName().equals("")) {
            log.output("choosing name");
            newType = NAME;
        }
        else {
            log.output("choosing prox");
            newType = PROX;
        }
        return newType;
    }

    private int getShortNameOrProx() {
        int newType;
        if (thisEntity.getShortName() != null && !thisEntity.getShortName().equals("")) {
            log.output("this entity is " + thisEntity);
            log.output("the short name is " + thisEntity.getShortName());
            log.output("choosing short name");
            newType = SHORTNAME;
        }
        else {
            log.output("passing on");
            newType = getNameOrProx();
        }
        return newType;
    }

    public int size() {
        return singles.size();
    }

    public List<Single> getSingles() {
        return singles;
    }

    public class Single {
        private String pred = null;
        private String det = null;
        private String number = null;
        private boolean canned = false;

        public Single(String p, String d, String n, int ref) {
            pred = p;
            det = d;
            number = n;
            if (ref == CANNED) {
                canned = true;
            }
        }

        /** returns the logical form predicate which has been chosen for
         * this expression
         * @return String pred
         */

        public String getPred() {
            return pred;
        }

        /** returns the logical form determiner which has been chosen for
         * this expression
         * @return String determiner
         */

        public String getDet() {
            return det;
        }

        /** returns the number for this expression (sg or pl)
         * @return String number
         */

        public String getNum() {
            return number;
        }

        public boolean isCannedText() {
            return canned;
        }

        public String toString() {
            String me = "pred: " + pred;
            me += " det: " + det;
            me += " num: " + number;
            me += " canned: " + canned;
            return me;
        }
    }
}