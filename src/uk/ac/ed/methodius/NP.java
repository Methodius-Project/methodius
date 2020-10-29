package uk.ac.ed.methodius;

public class NP {
	
	private String np;
	private int significance = 1;

	public NP(String s) {
		np = s;
	}
	
	public NP(String s, int sig) {
		np = s;
		significance = sig;
	}
	
	public String getNp() {
		return np;
	}
	
	public void setSignificance(int i) {
		significance = i;
	}
	
	public int getSignificance() {
		return significance;
	}

}
