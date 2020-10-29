package uk.ac.ed.methodius;

import java.util.LinkedList;
import java.util.List;

/**
 * Class to store a history of what referring expressions have been
 * used. The history only exists for the one object description and
 * so does not persist.
 *
 */

@SuppressWarnings("unchecked")

public class ReferringExpressionHistory {
	
    LinkedList reHistory = null;
    ReferringExpression mostRecent = null;
    ReferringExpression mostRecentFocal = null;
    public ReferringExpressionHistory() {
	reHistory = new LinkedList();
    }
    
    public void add(ReferringExpression ref) {
	reHistory.addFirst(ref);
	mostRecent = ref;
	if (ref.isFocal()) {
	    mostRecentFocal = ref;
	}
    }
    
    public List getHistory() {
	return reHistory;
    }
    
    public ReferringExpression getMostRecent() {
	return mostRecent;
    }
    
    public ReferringExpression getMostRecentFocal() {
	return mostRecentFocal;
    }
    
    public int getSize() {
	if (reHistory == null) {
	    return 0;
	}
	else {
	    return reHistory.size();
	}
    }
    
}
