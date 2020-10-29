package uk.ac.ed.methodius;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Pretty prints a Content Potential in indented form taking account of back pointers
 * by keeping a list of Entities already printed and not reprinting them.
 * 
 *
 */

@SuppressWarnings("unchecked")

public class CPPrinter {
	
	private static final int DX = 3;
	
	/**
	 * the list of entities already printed.
	 */
	private List printedEntities = null;
	
	private PrintStream out;
	
	private int indent = 0;
	
	/**
	 * create it with somewhere to print to.
	 * @param ps PrintStream
	 */
	
	public CPPrinter(PrintStream ps) {
		out = ps;
		printedEntities = new LinkedList();
		indent = 0;
	}
	
	
	public void print(ContentPotential cp) {
		Entity root = cp.getRoot();
		indent = 0;
		
		/* root is always an Entity so just print that*/
		printEntity(root);
	}
	
	
	private void output(String s) {
		String pad = "";
		for(int i = 0; i < indent; i++) {
			pad = pad + " ";
		}
		out.println(pad + s);
	}
	
	
	private void incIndent() {
		indent += DX;
	}
	
	
	private void decIndent() {
		indent -= DX;
		if( indent < 0 ) {
			indent = 0;
		}
	}
	
	
	/**
	 * prints an entity and its facts.
	 * 
	 * @param ent Entity
	 */
	
	private void printEntity(Entity ent) {
		printedEntities.add(ent.getId());
		output("Entity: " + ent.getId());
		output("Type: " + ent.getType().getName());
		output("name: " + ent.getName());
		output("shortname: " + ent.getShortName());
		output("gender: " + ent.getGender());
		output("generic: " + ent.isGeneric());
		output("Facts {");
		incIndent();
		List facts = ent.getFacts();
		
		/**
		 * iterate over the facts. They've either not been expanded
		 * and are still Strings or have been expanded and are Facts.
		 */
		
		Iterator iter = facts.iterator();
		while( iter.hasNext()) {
			Object f = iter.next();
			if( f.getClass().getName().endsWith("String")) {
				output((String)f);
			} else {
				printFact((Fact)f);
			}
		}
		decIndent();
		output("}");
	}
	
	
	/**
	 * print a fact with its predicate and args. The args are either
	 * undexpanded (Strings) or if expanded either have already been printed
	 * or are new. If already printed just put their name followed by (Entity)
	 * so that we know it's really an Entity and if it's new then print the
	 * whole entity.
	 * 
	 * @param f Fact
	 */
	private void printFact(Fact f) {
		output("Predicate: " + f.getPredicateName());
		Object arg1 = f.getArg1();
		Object arg2 = f.getArg2();
		if( arg1.getClass().getName().endsWith("String") ) {
			output("arg1: " + arg1);
		} else {
			Entity ent = (Entity)arg1;
			String eName = ent.getId();
			if( printedEntities.contains(eName)) {
				output("arg1:" + eName + "(Entity)");
			} else {
				output("arg1:");
				incIndent();
				printEntity(ent);
				decIndent();
			}
		}
		
		if( arg2.getClass().getName().endsWith("String") ) {
			output("arg2: " + arg2);
		} else {
			Entity ent = (Entity)arg2;
			String eName = ent.getId();
			if( printedEntities.contains(eName)) {
				output("arg2:" + eName + "(Entity)");
			} else {
				output("arg2:");
				incIndent();
				printEntity(ent);
				decIndent();
			}
		}
	}

}
