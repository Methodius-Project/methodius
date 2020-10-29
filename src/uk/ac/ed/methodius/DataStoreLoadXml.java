package uk.ac.ed.methodius;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import uk.ac.ed.methodius.Exceptions.DataStoreException;
import uk.ac.ed.methodius.Exceptions.LoadException;
import uk.ac.ed.methodius.Exceptions.PredicateFormException;

/**
 * handles the database loading and content potential creation
 * The DataStore stores all objects as HashMaps rather than
 * using standard serialization, the mechanism for this uses
 * standard APIs from sleepycat which are more efficient. The
 * storage method requires two DBs:
 * one for the objects and one for the class info to be stored.
 * In addition to the database storing objects, there is also a
 * <code>definitionDB<code> used to store additional information
 * such as object definitions and predicate significances.
 *
 */

@SuppressWarnings("unchecked")

public class DataStoreLoadXml extends DataStore {
    
	/**
	 * create a DataStore from a configuration which
	 * contains all necessary info. Values are taken from the configuration
	 * for the log object, name of the directory to store the databases in,
	 * the name to use for the database, the class database (used for flattening)
	 * and the name for the definitions database.
	 *
	 * @param c Configuration Object
	 */
    
    public DataStoreLoadXml(Configuration c, boolean ro) {
        super(c, ro);
    }

	private String nextObject(BufferedReader br) throws IOException {
		String line = br.readLine();
		while(line != null && !line.contains("defobject")){
			line = br.readLine();
		}
		
		if(line == null) return null;
		if(line.contains("/>")) return line;
		
		String object = line;
		while(line != null && !line.contains("/defobject")){
			line=br.readLine();
			object = object + "\n" + line;
		}
		return object;
	}


	/**
	 * loads the DB from the source XML file.
	 * The XML file has a root tag <defobjects> which encloses
	 * a series of <defobject> tags each of which defines one object.
	 * The defobject tag has two attributes:
	 *      is - defines the id for the object
	 *      type - defines a type for the object
	 * the type places the object within the type hierarchy defined by
	 * the types file (typically types.xml).
	 *
	 * @param root
	 * @throws LoadException
	 */
	private void loadInstances() throws LoadException {
	    log.start("loadInstances");
	    String fname = config.getObjectsFileName();
        log.output("loading from file: [" + fname + "]");
		String key;
		
		if( !initialised ) {
			throw new LoadException("Cannot load uninitialised database.");
		}
		try {
			Map generics = new HashMap();
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fname)));
			String object = nextObject(br);
			while(object != null) {
				SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(new StringReader(object));
                
				Element child = doc.getRootElement();
				
				Map dataMap = new HashMap();
				String name = child.getName();
				if( name.equals("defobject")){    // TODO: handle errors in file structure
					key = child.getAttributeValue("is");
					String type = child.getAttributeValue("type");
					dataMap.put("type", type);
					List roles = child.getChildren();
					Iterator roleIter = roles.iterator();
					while( roleIter.hasNext() ) {
						Element roleElem = (Element)roleIter.next();
						name = roleElem.getName();
						if(name.equals("role")){     // TODO: and if it's not?
							String attr = roleElem.getAttributeValue("slot");
							String value = roleElem.getAttributeValue("filler");
							if (value == null) {
							    Element fillersElem = roleElem.getChild("fillers");
							    TreeSet<String> sortedFillers = new TreeSet<String>();
							    for (Iterator fillerIter = fillersElem.getChildren("filler").iterator(); fillerIter.hasNext();) {
							        Element fillerElem = (Element)fillerIter.next();
							        String fillerVal =  fillerElem.getAttributeValue("filler");
							        String fillerOrder = fillerElem.getAttributeValue("order");
							        if (fillerOrder == null) {
							            fillerOrder = "1";
							        }
							        String orderedFiller = fillerOrder + "+" + fillerVal;
							        sortedFillers.add(orderedFiller);
							    }
							    for (Iterator<String> sortedFillersIter = sortedFillers.iterator(); sortedFillersIter.hasNext();) {
							        String thisFiller = sortedFillersIter.next();
							        int plusIndex = thisFiller.indexOf("+");
							        String realFiller = thisFiller.substring(plusIndex + 1, thisFiller.length());
							        if (value == null) {
							            value = realFiller;
							        }
							        else {
							            value += ":" + realFiller;
							        }
							    }
							}
							if(attr.equals("generic")) {
								/* store generic object separately */
								generics.put(type, key);
							}
							dataMap.put(attr, value);
						}
					}
					addToDatabase(key, dataMap, db);
				}
				object = nextObject(br);
			}

			//System.out.println("generics = " + generics);
			/* store the generics table */
			key = "generics";
			addToDatabase(key, generics, definitionDb);
		} catch(Exception e) {
			throw new LoadException(e);
		}
		log.output("Load Finished: close and reopen DB.");
        try{
            close();
            init();
        } catch(Exception e) {
            throw new LoadException("Problem synching DB.", e);
        }
        log.end("loadInstances");
	}

	private void loadTypesXML(Element root) throws LoadException {

	    Map typesDataMap = new HashMap();
	    for (Iterator<Element> typeIter = root.getChildren().iterator(); typeIter.hasNext();){
	        Map dataMap = new HashMap();
	        Element typeEl = typeIter.next();
	        String name = typeEl.getName();
	        String typeName;
	        log.output("name is " + name);
	        if( name.equals("type") ) {
	            typeName = typeEl.getAttributeValue("name");
	            log.output("type is " + typeName);
	            Element parentsEl = typeEl.getChild("parents");
	            String parents = null;
	            for (Iterator<Element> parentIter = parentsEl.getChildren().iterator(); parentIter.hasNext();) {
	                Element parentEl = parentIter.next();
	                String parentName = parentEl.getAttributeValue("name");
	                if (parents == null) {
	                    parents = parentName;
	                }
	                else {
	                    parents += ":" + parentName;
	                }
	            }
	            dataMap.put("parents", parents);

	            String nps = null;
	            Element npsEl = typeEl.getChild("nps");
	            for (Iterator<Element> npIter = npsEl.getChildren().iterator(); npIter.hasNext();) {
	                Element npEl = npIter.next();
	                String npName = npEl.getAttributeValue("id");
	                if (nps == null) {
	                    nps = npName;
	                }
	                else {
	                    nps += ":" + npName;
	                }
	            }
	            dataMap.put("nps", nps);


	        } else {
	            throw new LoadException("Badly formed types file: " + name);
	        }
	        typesDataMap.put(typeName, dataMap);
	    }
	    String key = "typesDataMap";
	    addToDatabase(key, typesDataMap, definitionDb);
	    checkDatabases();
	}



	/**
	 * load the type hierarchy into the DB.
	 * They are stored under the object type name.
	 * Loaded from an XML file.
	 * @throws LoadException
	 */
	
	public void loadTypes() throws LoadException {
		log.start("loadTypes");
		try {
			log.output("Starting load of type hierarchy");
			SAXBuilder builder = new SAXBuilder();
			String fname = config.getTypesFileName();
			log.output("loading from file: [" + fname + "]");
            Document doc = null;
            doc = builder.build(getClass().getResourceAsStream(fname));
			Element root = doc.getRootElement();
			loadTypesXML(root);
		} catch( JDOMException je ) {
			throw new LoadException(je);
		} catch( IOException ioe ) {
			throw new LoadException(ioe);
		}

		log.end("loadTypes");

	}

	/**
	 * parse the object definition XML into the DB. The XML structure looks like:
	 * <p>
	 * <code>
	 * <significances>
	 * <predicate-significances>
	 *    <predicate-significance for-predicate="PREDICATE NAME">
	 *       <for-type name="TYPENAME">
	 *          <significance user-type="USERTYPE" val="SIGVALUE"/>*
	 *       </for-type>
	 *    </predicate-significance>
	 * </predicate-significances>
	 * <expression-significances>
	 * </expression-significances>
	 * <np-significances>
	 * </np-significances>
	 * @param root
	 * @throws LoadException
	 */
	private void loadSigXML(Element root) throws LoadException {
		log.start("loadSigXML");
		if( !initialised ) {
			throw new LoadException("Cannot load uninitialised database.");
		}
		try {
			Map predicateSigMap = new HashMap();
			Map expressionSigMap = new HashMap();
			Map npSigMap = new HashMap();

			List sigClassEl = root.getChildren();
			Iterator sigClassIter = sigClassEl.iterator();
			while(sigClassIter.hasNext()){
				/* one of predicate, expression or np-significances */
				Element sigClass = (Element)sigClassIter.next();
				String sigClassName = sigClass.getName();
				if(sigClassName.equals("predicate-significances") ) {
					List pSigEls = sigClass.getChildren();
					Iterator pSigIter = pSigEls.iterator();
					while(pSigIter.hasNext()) {
						Element pSigEl = (Element)pSigIter.next();
						String forPredicate = pSigEl.getAttributeValue("for-predicate");
						List typeEls = pSigEl.getChildren();
						Iterator typeIter = typeEls.iterator();
						while(typeIter.hasNext()){
							String sigPairs = null;
							Element typeEl = (Element)typeIter.next();
							String typeName = typeEl.getAttributeValue("name");
							List sigEls = typeEl.getChildren();
							Iterator sigIter = sigEls.iterator();
							while(sigIter.hasNext()){
								Element sigEl = (Element)sigIter.next();
								String userType = sigEl.getAttributeValue("user-type");
								String sigValue = sigEl.getAttributeValue("val");
								if(sigPairs == null) {
									sigPairs = userType + "=" + sigValue;
								} else {
									sigPairs = sigPairs + ":" + userType + "=" + sigValue;
								}
							}
							String k = forPredicate + ":" + typeName;
							predicateSigMap.put(k, sigPairs);
						}
					}
				} else if(sigClassName.equals("expression-significances")){
					List exprSigEls = sigClass.getChildren();
					Iterator exprSigIter = exprSigEls.iterator();
					while(exprSigIter.hasNext()){
						String sigPairs = null;
						Element exprSigEl = (Element)exprSigIter.next();
						String forExpression = exprSigEl.getAttributeValue("for-expression");
						List sigEls = exprSigEl.getChildren();
						Iterator sigIter = sigEls.iterator();
						while(sigIter.hasNext()) {
							Element sigEl = (Element)sigIter.next();
							String userType = sigEl.getAttributeValue("user-type");
							String sigValue = sigEl.getAttributeValue("val");
							if(sigPairs == null) {
								sigPairs = userType + "=" + sigValue;
							} else {
								sigPairs = sigPairs + ":" + userType + "=" + sigValue;
							}
						}
						expressionSigMap.put(forExpression,sigPairs);
					}
				} else if(sigClassName.equals("np-significances")){
				    /*
				      <np-significance for-np="vessel-np">
				      <significance user-type="adult" val="1"/>
				      <significance user-type="expert" val="1"/>
				      <significance user-type="child" val="1"/>
				    </np-significance>
				    */
					List npSigEls = sigClass.getChildren();
					Iterator npSigIter = npSigEls.iterator();
					while(npSigIter.hasNext()){
						String sigPairs = null;
						Element npSigEl = (Element)npSigIter.next();
						String forNp = npSigEl.getAttributeValue("for-np");
						List sigEls = npSigEl.getChildren();
						Iterator sigIter = sigEls.iterator();
						while(sigIter.hasNext()) {
							Element sigEl = (Element)sigIter.next();
							String userType = sigEl.getAttributeValue("user-type");
							String sigValue = sigEl.getAttributeValue("val");
							if(sigPairs == null) {
								sigPairs = userType + "=" + sigValue;
							} else {
								sigPairs = sigPairs + ":" + userType + "=" + sigValue;
							}
						}
						npSigMap.put(forNp,sigPairs);
					}

				} else {
					//TODO: dae sumthin'
				}
			}
			log.output("Predicate Significance Table = \n" + predicateSigMap);
			log.output("Expression Significance Table = \n" + expressionSigMap);
			log.output("NP Significance Table = \n" + npSigMap);

			/*
			 * store each of the significance tables.
			 */
			
			addToDatabase("predicateSigMap", predicateSigMap, definitionDb);
			addToDatabase("expressionSigMap", expressionSigMap, definitionDb);
			addToDatabase("npSigMap", npSigMap, definitionDb);

			
			
			
		} catch(Exception e) {
			throw new LoadException(e);
		}
		
		try {
		    String name = "predicateSigMap";
            DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));
            DatabaseEntry value = new DatabaseEntry();
            OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);
            log.output("status is " + status);
            if (status == OperationStatus.SUCCESS) {
                Map thisMap = (Map)dataBinding.entryToObject(value);
                log.output("thisMap is " + thisMap);
            }
        } catch(Exception e) {
            try {
                throw new DataStoreException(e);
            } catch (DataStoreException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
		
		log.end("loadSigXML");
	}



	/**
	 * load the significance table into the DB
	 * from XML file.
	 * @throws LoadException
	 */
	public void loadSigs() throws LoadException {
		log.start("loadSigs");
		try {
			log.output("Starting load of significance values");
			SAXBuilder builder = new SAXBuilder();
			String fname = config.getSignificancesFileName();
            log.output("loading from file: [" + fname + "]");
            Document doc = null;
            doc = builder.build(getClass().getResourceAsStream(fname));
			Element root = doc.getRootElement();
			loadSigXML(root);
		} catch( JDOMException je ) {
			throw new LoadException(je);
		} catch( IOException ioe ) {
			throw new LoadException(ioe);
		}

		checkDatabases();
		log.end("loadSigs");

	}

	/**
	 * parse the user types info from XML.
	 * @param root the root Element of the XML
	 * @throws LoadException
	 */
	private void loadUserTypesXML(Element root) throws LoadException {
		log.start("loadUserTypesXML");
		if( !initialised ) {
			throw new LoadException("Cannot load uninitialised database.");
		}
		try {
			Map dataMap = new HashMap();
			List children = root.getChildren();
			Iterator iter = children.iterator();
			while(iter.hasNext()){
				Element child = (Element)iter.next();
				String name = child.getName();
				if( name.equals("user")){
					String factsPerPage = "9";
					String factsPerSentence = "2";
					String cpDepth = "5";
					String userName = child.getAttributeValue("name");
					String description = "";
					List attrs = child.getChildren();
					Iterator attrIter = attrs.iterator();
					while(attrIter.hasNext()) {
						Element attr = (Element)attrIter.next();
						String attrName = attr.getName();
						if(attrName.equals("maximumfacts")) {
							factsPerPage = attr.getAttributeValue("page");
							factsPerSentence = attr.getAttributeValue("sentence");
						} else if(attrName.equals("contentpotential")) {
							cpDepth = attr.getAttributeValue("depth");
						} else if(attrName.equals("desc")) {
							description = attr.getText();
						}
					}
					String storageStr = factsPerPage + ":" + factsPerSentence + ":" + cpDepth + ":" + description;
					dataMap.put(userName,storageStr);
				}

			}
			log.output("Storing UserTypes Table as: " + dataMap);
			String key = "userTypes";
			addToDatabase(key, dataMap, definitionDb);
		} catch(Exception e) {
			throw new LoadException(e);
		}
		
		log.end("loadUserTypesXML");
	}

	/**
	 * load the user types into the DB
	 * from XML file.
	 * @throws LoadException
	 */
	public void loadUserTypes() throws LoadException {
		log.start("loadUserTypes");
		try {
			log.output("Starting load of user types");
			SAXBuilder builder = new SAXBuilder();
			String fname = config.getUserTypesFileName();
			log.output("loading from file: [" + fname + "]");
            Document doc  = null;
            doc = builder.build(getClass().getResourceAsStream(fname));
			Element root = doc.getRootElement();
			loadUserTypesXML(root);
		} catch( JDOMException je ) {
			throw new LoadException(je);
		} catch( IOException ioe ) {
			throw new LoadException(ioe);
		}

		checkDatabases();
		log.end("loadUserTypes");

	}

	/**
     * load the predicates and expressions into the DB
     * from XML file.
     * @throws LoadException
	 * @throws PredicateFormException 
     */
    public void loadPredicates() throws LoadException, PredicateFormException {
        log.start("loadPredicates");
        try {
            log.output("Starting load of predicates");
            SAXBuilder builder = new SAXBuilder();
            String fname = config.getPredicatesFileName();
            log.output("loading from file: [" + fname + "]");
            Document doc  = null;
            doc = builder.build(getClass().getResourceAsStream(fname));
            Element root = doc.getRootElement();
            loadPredicatesXML(root);
        } catch( JDOMException je ) {
            throw new LoadException(je);
        } catch( IOException ioe ) {
            throw new LoadException(ioe);
        }

        /* synch the DB after the load */
        checkDatabases();   
    }
    
    private void loadPredicatesXML(Element root) throws PredicateFormException {
        List children = root.getChildren();
        Iterator iter = children.iterator();
        while(iter.hasNext()){
            Element pred = (Element)iter.next();
            String predElName = pred.getName();  //the predicate tag
            if( !predElName.equals("predicate")) {
                throw new PredicateFormException(pred, "predicate");
            }
            Map dataMap = new HashMap();
            String predName = pred.getAttributeValue("fact");
            log.output("processing predicate " + predName);
            String canBeCompared = pred.getAttributeValue("comparison");
            dataMap.put("comparison", canBeCompared);
            // do expressions here
            List exprTags = pred.getChildren();
            if (exprTags.isEmpty()) {
                log.output("No expressions for " + predName);
            }
            Iterator exprIter = exprTags.iterator();
            String expList = null;
            while(exprIter.hasNext()) {
                Element exp = (Element)exprIter.next();
                String expName = loadExpressionsXML(exp);
                if (expList == null) {
                    expList = expName;
                }
                else {
                expList += ":" + expName;
                }
            }
            dataMap.put("expressions", expList);
            String key = predName + "-pred";
            log.output("key is " + key);
            log.output("data map is " + dataMap);
            addToDatabase(key, dataMap, definitionDb);
        }
    }
    
    private String loadExpressionsXML(Element exprEl) throws PredicateFormException {
        String key = exprEl.getAttributeValue("id");
        log.output("processing expression " + key);
        Map dataMap = new HashMap();
        String aggStr = exprEl.getAttributeValue("aggregation-allowed");
        if (aggStr.equals("true") || aggStr.equals("before")) {
            dataMap.put("aggregationAllowedBefore", "true");
        }
        if (aggStr.equals("true") || aggStr.equals("after")) {
            dataMap.put("aggregationAllowedAfter", "true");
        }
        dataMap.put("generation", exprEl.getAttributeValue("generation"));
        List children = exprEl.getChildren(); // the various attribute tags
        Iterator iter = children.iterator();
        while(iter.hasNext()) {
            Element child = (Element)iter.next();
            String childName = child.getName();
            if( childName.equals("arg-one")) {
                dataMap.put("arg1Type", child.getAttributeValue("type"));
                dataMap.put("arg1RefExp", child.getAttributeValue("refexp"));
                List arg1Children = child.getChildren(); // the children of arg1
                Iterator arg1Iter = arg1Children.iterator();
                while(arg1Iter.hasNext()) {
                    Element arg1Child = (Element)arg1Iter.next();
                    String arg1ChildName = arg1Child.getName();
                    if (arg1ChildName.equals("owner-of")) {
                        dataMap.put("arg1OwnerType", arg1Child.getAttributeValue("type"));
                        dataMap.put("arg1OwnerRefExp", arg1Child.getAttributeValue("refexp"));
                        dataMap.put("arg1OwnerNum", arg1Child.getAttributeValue("num"));
                    }
                }
            } else if( childName.equals("arg-two")) {
                dataMap.put("arg2Type", child.getAttributeValue("type"));
                dataMap.put("arg2RefExp", child.getAttributeValue("refexp"));
                List arg2Children = child.getChildren(); // the children of arg2
                Iterator arg2Iter = arg2Children.iterator();
                while(arg2Iter.hasNext()) {
                    Element arg2Child = (Element)arg2Iter.next();
                    String arg2ChildName = arg2Child.getName();
                    if (arg2ChildName.equals("owner-of")) {
                        dataMap.put("arg2OwnerType", arg2Child.getAttributeValue("type"));
                        dataMap.put("arg2OwnerRefExp", arg2Child.getAttributeValue("refexp"));
                        dataMap.put("arg2OwnerNum", arg2Child.getAttributeValue("num"));
                    }
                }
            } else if( childName.equals("verb")) {
                dataMap.put("tense", child.getAttributeValue("tense"));
                dataMap.put("voice", child.getAttributeValue("voice"));
                dataMap.put("verb", child.getAttributeValue("pred"));
            } else if( childName.equals("text")) {
                dataMap.put("text", child.getTextTrim());
                dataMap.put("textPosition", child.getAttributeValue("position"));
            } else if( childName.equals("preposition")) {
                dataMap.put("preposition", child.getAttributeValue("id"));
            } else if( childName.equals("adverb")) {
                dataMap.put("adverb", child.getAttributeValue("id"));
            } else {
                throw new PredicateFormException(exprEl, childName);
            }
        }
        log.output("exp data map is " + dataMap);
        addToDatabase(key, dataMap, definitionDb);
        return key;
    }

	/**
	 * load the DB from the various XML files. First load the object structure
	 * definitions, then the predicate significance table
	 * then the objects themselves.
	 *
	 * @throws LoadException
	 * @throws PredicateFormException 
	 */

	public void load() throws LoadException, PredicateFormException {
		log.start("load");
		log.output("classic load");
		loadTypes();
		loadSigs();
		loadUserTypes();
		loadInstances();
		loadPredicates();
		close();
		log.end("load");
	}
}
