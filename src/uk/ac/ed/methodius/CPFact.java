package uk.ac.ed.methodius;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements a Fact in the content selection process.
 * 
 *
 */
@SuppressWarnings("unchecked")

public class CPFact {
	
	private Fact theFact;
	private List<CPFact> dependentFacts;
	private int level = 0;
	private CPFact parent;
	private List<CPFact> chosen;  /* list of facts selected which are dependent on this one */
	
	public CPFact(Fact f, CPFact p, List l) {
		theFact = f;
		parent = p;
		dependentFacts = l;
		chosen = null;
	}
	
	public CPFact(Fact f, CPFact p) {
		this(f,p,null);
	}
	
	public Fact getFact() {
		return theFact;
	}
	
	public CPFact getParent() {
		return parent;
	}
	
	public void setDependents(List l){
		dependentFacts = l;
	}
	
	public List getDependents() {
	    if (dependentFacts == null) {
		return new LinkedList();
	    }
		return dependentFacts;
	}
	
	public void addChosen(CPFact cpf) {
		if(chosen == null) {
			chosen = new LinkedList();
		}
		chosen.add(cpf);
	}
	
	public List getChosen() {
	    if (chosen == null) {
		return new LinkedList();
	    }
	    return chosen;
	}
	
	public void setLevel(int l) {
		level = l;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String toString() {
		return "\n{CPFact:\n" + theFact + "\ndependents:" + dependentFacts + "\nchosen:" + chosen + "}\n";
	}
	
	
	private static class CPFactComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			CPFact cpf1 = (CPFact)arg0;
			CPFact cpf2 = (CPFact)arg1;
			Fact f1 = cpf1.getFact();
			Fact f2 = cpf2.getFact();
			int sig1 = f1.getEffectiveSignificance();
			int sig2 = f2.getEffectiveSignificance();
			return sig2 - sig1;
		}
		
	}
	
	
	public static Comparator getComparator() {
		return new CPFactComparator();
	}
}
