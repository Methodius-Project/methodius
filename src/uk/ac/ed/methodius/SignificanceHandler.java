package uk.ac.ed.methodius;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.methodius.Exceptions.DataStoreException;

/**
 * handles the predicate significance table.
 * This is assumed to be small and so is loaded
 * and held in memory the whole time.
 * 
 * Significances are just single integers.
 * 
 * @author Ray
 *
 */

@SuppressWarnings("unchecked")

public class SignificanceHandler {
	
	private static final int defaultPredicateSignificance = 1;
	private static final int defaultExpressionSignificance = 1;
	private static final int defaultNpSignificance = 1;
	
	private Configuration config = null;
	private Log log = null;
	
	/*
	 * three level Map
	 * predicate -> type -> usertype -> sig value.
	 */
	Map predicateSigTable = null;
	
	/*
	 * two level map
	 * expression -> usertype -> value
	 */
	Map expressionSigTable = null;
	
	/*
	 * two level map
	 * np -> usertype -> value
	 */
	Map npSigTable = null;

	
	
	public SignificanceHandler(DataStoreRead ds, Configuration c) throws DataStoreException {
		predicateSigTable = new HashMap();
		expressionSigTable = new HashMap();
		npSigTable = new HashMap();
		
		config = c;
		log = config.getLog();

		Map dsPredTable = ds.getPredicateSignificances();
		Map dsExprTable = ds.getExpressionSignificances();
		Map dsNpTable = ds.getNpSignificances();
		try {
			unpackPredTable(dsPredTable);
			unpackExprTable(dsExprTable);
			unpackNpTable(dsNpTable);
		}
		catch (Exception e) {
			log.output("Have you reloaded the domain?");
		}
		log.output("PredicateSigs = " + predicateSigTable);
		log.output("ExpressionSigs = " + expressionSigTable);
		log.output("NpSigs = " + npSigTable);
	}
	
	/*
	 * predicate:type -> user1=val1:user2=val2:...
	 */
	private void unpackPredTable(Map dsTab) {
	    log.start("unpackPredTable");
	    log.output("map is " + dsTab);
		Set keys = dsTab.keySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext()) {
			Map userTypeMap = new HashMap();
			String key = (String)iter.next();
			String val = (String)dsTab.get(key);
			String[] split = key.split(":");
			String predicate = split[0];
			String type = "ANYTYPE";
			if( split.length > 1 ) {   // could be no type specified
				type = split[1];
			}
			split = val.split(":");
			for(int i = 0; i < split.length; i++) {
				String userValPair = split[i];
				String userval[] = userValPair.split("=");
				String userType = userval[0];
				String value = userval[1];
				Integer intVal = new Integer(value);
				userTypeMap.put(userType,intVal);
			}
			Map predTypeMap = (Map)predicateSigTable.get(predicate);
			if(predTypeMap == null) {
				predTypeMap = new HashMap();
				predicateSigTable.put(predicate,predTypeMap);
			}
			predTypeMap.put(type,userTypeMap);
		}
	}
	
	private void unpackExprTable(Map dsTab) {
		Set keys = dsTab.keySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext()) {
			Map userTypeMap = new HashMap();
			String expr = (String)iter.next();
			String userTypeValues = (String)dsTab.get(expr);
			String userTypes[] = userTypeValues.split(":");
			for(int i = 0; i < userTypes.length; i++) {
				String valPair = userTypes[i];
				String pair[] = valPair.split("=");
				String userType = pair[0];
				String userValStr = pair[1];
				Integer userVal = new Integer(userValStr);
				userTypeMap.put(userType,userVal);
			}
			expressionSigTable.put(expr,userTypeMap);
		}
	}
	
	private void unpackNpTable(Map dsTab) {
		Set keys = dsTab.keySet();
		Iterator iter = keys.iterator();
		while(iter.hasNext()) {
			Map userTypeMap = new HashMap();
			String np = (String)iter.next();
			String userTypeValues = (String)dsTab.get(np);
			String userTypes[] = userTypeValues.split(":");
			for(int i = 0; i < userTypes.length; i++) {
				String valPair = userTypes[i];
				String pair[] = valPair.split("=");
				String userType = pair[0];
				String userValStr = pair[1];
				Integer userVal = new Integer(userValStr);
				userTypeMap.put(userType,userVal);
			}
			npSigTable.put(np,userTypeMap);
		}
		
	}
	
	/**
	 * returns the significance value for a combination of predicate,
	 * arg1 type and user type. If the arg1Type isn't found then we
	 * progress up the type hierarchy looking for a significance for
	 * a parent type.
	 * 
	 * @param predicate the predicate name
	 * @param type arg1 type name
	 * @param userType user type name
	 * @return significance
	 * @throws Exception
	 */
	public int getPredicateSignificance(String predicate, Type type, String userType){
		log.start("getPredicateSignificance: predicate " + predicate + " and type " + type);
		int ret = 0;
		Map typeMap = (Map)predicateSigTable.get(predicate);
		if(typeMap == null || userType == null) {
			ret = defaultPredicateSignificance;
		} else {
			String typeName = type.getName();
			log.output("looking for " + typeName);
			Map userMap = (Map)typeMap.get(typeName);
			if(userMap == null) {
				log.output("didn't find it, looking for ANYTYPE");
				userMap = (Map)typeMap.get("ANYTYPE");
				if(userMap == null) { // start searching up the type hierarchy
					log.output("didn't find that - search up the type hierarchy");
					List typesToTry = new LinkedList();
					typesToTry.addAll(type.getParents());
					log.output("types to try: " + Util.listOfTypesToString(typesToTry));
					while(!typesToTry.isEmpty() && userMap == null) {
						/*
						 * failed to find a UserMap for this arg1Type
						 * so look for one for its parents.....
						 */
						
						Type tryNext = (Type)typesToTry.get(0);
						typesToTry.remove(0);
						String tryNextName = tryNext.getName();
						log.output("looking at: " + tryNextName);
						typesToTry.addAll(tryNext.getParents());
						userMap = (Map)typeMap.get(tryNext.getName());
						if(userMap == null) {
							log.output("didn't find that");
							log.output("types to try: " + Util.listOfTypesToString(typesToTry));
						} else {
							log.output("found that");
						}
					}
				}
			}
			if(userMap == null) { // we failed to find anything!
				log.output("completely failed to find anything - return default sig");
				ret = defaultPredicateSignificance;
			} else {
				Integer sigIntVal = (Integer)userMap.get(userType);
				if(sigIntVal == null) {
					ret = defaultPredicateSignificance;
				} else {
					ret = sigIntVal.intValue();
				}
			}
		}
		log.end("getPredicateSignificance: " + predicate);
		return ret;
	}
	
	
	/**
	 * returns the significanc evalue for a combination of
	 * expression and user type
	 * @param expression the expression id
	 * @param userType the user type name
	 * @return the significance value
	 * @throws Exception
	 */
	public int getExpressionSignificance(String expression, String userType){

		Map userMap = (Map)expressionSigTable.get(expression);
		if(userMap == null) {
			return defaultExpressionSignificance;
		}
		Integer sigIntVal = (Integer)userMap.get(userType);
		if(sigIntVal == null) {
			return defaultExpressionSignificance;
		}
		
		return sigIntVal.intValue();
	}
	
	/**
	 * returns the significance evalue for a combination of
	 * np and user type
	 * @param np the np
	 * @param userType the user type name
	 * @return the significance value
	 * @throws Exception
	 */
	public int getNpSignificance(String np, String userType){
		Map userMap = (Map)predicateSigTable.get(np);
		if(userMap == null) {
			return defaultNpSignificance;
		}
		Integer sigIntVal = (Integer)userMap.get(userType);
		if(sigIntVal == null) {
			return defaultNpSignificance;
		}
		
		return sigIntVal.intValue();
	}
}
