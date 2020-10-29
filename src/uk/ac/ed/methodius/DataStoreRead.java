package uk.ac.ed.methodius;



import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;

import uk.ac.ed.methodius.Exceptions.DataStoreException;
import uk.ac.ed.methodius.Exceptions.PredicateFormException;
import uk.ac.ed.methodius.Exceptions.UnknownUserTypeException;

/**
 * handles the 
 * retrieval of objects and limited content potential webs.
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

public class DataStoreRead {

	private Environment dbEnv = null;

	private Database db = null;
	private Database classDb = null;
	private Database definitionDb = null;

	/* root dir for databases */
	private String databaseDir = null;

	/* the DB for objects */
	private String databaseName = null;

	/* class DB used for serialization API */
	private String classDatabaseName = null;

	/* DB for object definitions */
	private String definitionDatabaseName = null;

	/* used by serialization API */
	private EntryBinding dataBinding = null;

	/* do we want to open the DB read only (for normal runs) or not (for loads) */
	private boolean readOnly = false;

	/* is the DB opened and ready to access? */
	private boolean initialised = false;
    
    /* Are we using an XML config file and data files in the main jar? */
    
	private Configuration config = null;

	private Log log = null;

	private List expressionStrings = Arrays.asList(
	        new String[]{"verb",
	                "tense",
	                "arg1Type",
	                "arg2Type",
	                "arg1OwnerType",
	                "arg2OwnerType",
	                "voice",
	                "adverb",
	                "preposition",
	                "arg1RefExp",
	                "arg2RefExp",
	                "arg1OwnerRefExp",
	                "arg2OwnerRefExp",
	                "arg1OwnerNum",
	                "arg2OwnerNum",
	                "text",
	                "textPosition",
	                "mood",
	                "generation",
	                "reversible",
	                "aggregationAllowedBefore",
                    "aggregationAllowedAfter"
	        });
	

	/**
	 * create a DataStore from a configuration which
	 * contains all necessary info. Values are taken from the configuration
	 * for the log object, name of the directory to store the databases in,
	 * the name to use for the database, the class database (used for flattening)
	 * and the name for the definitions database.
	 *
	 * @param c Configuration Object
	 */

	public DataStoreRead(Configuration c, boolean ro) {
	    
		config = c;
		readOnly = ro;
		log = config.getLog();
		log.start("DataStoreRead");
		databaseDir = config.getDatabaseDir();
		databaseName = config.getDatabaseName();
		classDatabaseName = config.getClassDatabaseName();
		definitionDatabaseName = config.getDefinitionDatabaseName();
		log.end("DataStoreRead");

	}


	/**
	 * set up a database environment, open the main and class databases
	 * and set up the data binding for doing serialization.
	 * Must be called before DB is accessed and can only be called once.
	 *
	 * @throws DataStoreException
	 */

	public void init() throws DataStoreException {
		log.start("init");
		//System.out.println("read only is " + readOnly);
		log.output("read only is " + readOnly);
		if( initialised ) {
			throw new DataStoreException("Cannot call setup twice - datastore already initialised.");
		}
		try {
			// Open the environment. Create it if it does not already exist.
			log.output("Creating environment." );
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(!readOnly);
			envConfig.setReadOnly(readOnly);
			dbEnv = new Environment(new File(databaseDir), envConfig);

			// Open the database. Create it if it does not already exist.
			log.output("Opening database." );
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(!readOnly);
			dbConfig.setReadOnly(readOnly);

			db = dbEnv.openDatabase(null, databaseName, dbConfig);
			
			log.output("Open class database.");
			classDb = dbEnv.openDatabase(null, classDatabaseName, dbConfig);
			
			log.output("Open definition database.");
			definitionDb = dbEnv.openDatabase(null, definitionDatabaseName, dbConfig);

			// Instantiate the class catalog
			StoredClassCatalog classCatalog = new StoredClassCatalog(classDb);

			// Create the binding
			dataBinding = new SerialBinding(classCatalog, HashMap.class);

		} catch (Exception e) {
			throw new DataStoreException(e);
		}
		initialised = true;
		log.end("init");
	}


	public void close(){
		log.start("close");
		try {
			if (db != null) {
				db.close();
			}

			if (classDb != null) {
				classDb.close();
			}

			if (definitionDb != null) {
				definitionDb.close();
			}

			if (dbEnv != null) {
				dbEnv.close();
			}
		} catch (DatabaseException dbe) {
			log.output("Cannot close Database because: " + dbe);
		}
		initialised = false;
		log.end("close");
	}

	/**
	 * check is a named object exists in the DB.
	 * @param name
	 * @return true if named object exists in DB
	 * @throws DataStoreException
	 */
	
	public boolean isInDb(String name) throws DataStoreException {
	    try {
	        DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));

	        DatabaseEntry value = new DatabaseEntry();
	        OperationStatus status = db.get(null, key, value, LockMode.DEFAULT);

	        return status == OperationStatus.SUCCESS;
	    } catch(Exception e) {
	        throw new DataStoreException(e);
	    }
	}

	public Predicate getPredicate(String predName, String factId) throws DataStoreException {
	    log.start("getPredicate for " + predName);
	    Map objectMap = null;
	    String exprList = null;
	    boolean comp = false;
	    List theExpressions = null;
	    Predicate p = null;
	    
	    try {
	        String keyString = predName + "-pred";
	        DatabaseEntry key = new DatabaseEntry(keyString.getBytes("UTF-8"));
	        DatabaseEntry value = new DatabaseEntry();
	        OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);
	        if (status == OperationStatus.SUCCESS) {
	            objectMap = (Map)dataBinding.entryToObject(value);
	            log.output("pred objectmap = " + objectMap);
	            exprList = (String)objectMap.get("expressions");
	            String compString = (String)objectMap.get("comparison");
	            if (compString == null) {
	                comp = false;
	            }
	            else {
	                comp = compString.equals("true");
	            }
	            p = new Predicate(factId, predName, log);
	            if (comp) {
	                log.output("adding fact to comps " + predName);
	                config.addCompPred(predName);
	            }
	            if (exprList != null) {
	                String[] strArr = exprList.split(":");
	                theExpressions = new LinkedList(Arrays.asList(strArr));
	                for (Iterator<String> expIter = theExpressions.iterator(); expIter.hasNext();) {
	                    String exp = expIter.next();
	                    Expression e = getExpression(exp);
	                    p.addExpression(e);
	                }
	                log.output("expr list string = " + exprList);
	            }

	        }
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (DatabaseException e) {
	        e.printStackTrace();
	    }
	    log.end("getPredicate for " + predName);
	    return p;
	}
	
	private Expression getExpression(String exprName) {
	    log.start("getExpression for " + exprName);
	    Map objectMap = null;
	    Expression e = null;
	    
	    try {
            DatabaseEntry key = new DatabaseEntry(exprName.getBytes("UTF-8"));
            DatabaseEntry value = new DatabaseEntry();
            OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);
            if (status == OperationStatus.SUCCESS) {
                objectMap = (Map)dataBinding.entryToObject(value);
                log.output("objectmap = " + objectMap);
                e = new Expression(exprName);   
                for (Iterator<String> fieldIter = expressionStrings.iterator(); fieldIter.hasNext();) {
                    String field = fieldIter.next();
                    String val = (String)objectMap.get(field);
                    if (val != null) {
                        e.setAnything(field, val);
                    }
                }
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (DatabaseException e1) {
            e1.printStackTrace();
        }
        log.end("getExpression");
	    return e;
	}


	/**
	 * get a single entity by name. After retrieval the facts are
	 * just a list of Strings.
	 *
	 * @param id the database id of the object
	 * @return entity or null if not found.
	 * @throws DataStoreException
	 */

	public Entity getEntity(String id) throws DataStoreException {
		log.start("getEntity for " + id);
		Map objectMap = null;
		String typeName = null;
		String name = null;
		String shortname = null;
		String gender = null;
		String generic = null;
		String number = null;
		List theFacts = null;
		Entity ent = null;
//		Type type = null;

		try {
			DatabaseEntry key = new DatabaseEntry(id.getBytes("UTF-8"));

			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = db.get(null, key, value, LockMode.DEFAULT);

			if (status == OperationStatus.SUCCESS) {
				objectMap = (Map)dataBinding.entryToObject(value);
				log.output("objectmap = " + objectMap);
				typeName = (String)objectMap.get("type");
				name = (String)objectMap.get("name");
				shortname = (String)objectMap.get("shortname");
				gender = (String)objectMap.get("gender");
				generic = (String)objectMap.get("generic");
				number = (String)objectMap.get("number");
//				type = getType(typeName);
				String factStr = (String)objectMap.get("facts");
				if(factStr != null){
					String[] strArr = factStr.split(":");
					theFacts = new LinkedList(Arrays.asList(strArr));
					log.output("fact string = " + factStr);
				}
				log.output("creating entity " + name + " with type " + typeName);
				ent = new Entity(id, typeName, name, shortname, gender, number, generic, theFacts);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		log.end("getEntity for " + id);
		return ent;
	}

	
	public Map getTypes() throws DataStoreException {
	    Map typesMap = null;
        String name = "typesDataMap";
        try {
            DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));
            DatabaseEntry value = new DatabaseEntry();
            OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);

            if (status == OperationStatus.SUCCESS) {
                typesMap = (Map)dataBinding.entryToObject(value);
            }
        } catch(Exception e) {
            throw new DataStoreException(e);
        }
        return typesMap;
	}


	/**
	 * retrieve the predicate significance table from the DB.
	 * @return Map of predicate:type -> signifcances.
	 * @throws DataStoreException
	 */

	public Map getPredicateSignificances() throws DataStoreException {
		Map sigMap = null;
		String name = "predicateSigMap";
		log.output("definition db is " + definitionDb);

		try {
			DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);
			log.output("status is " + status);
			if (status == OperationStatus.SUCCESS) {
				sigMap = (Map)dataBinding.entryToObject(value);
				log.output("sigMap is " + sigMap);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		return sigMap;
	}

	/**
	 * retrieve the expression significance table from the DB.
	 * @return Map of expression -> signifcances.
	 * @throws DataStoreException
	 */

	public Map getExpressionSignificances() throws DataStoreException {
		Map sigMap = null;
		String name = "expressionSigMap";

		try {
			DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);

			if (status == OperationStatus.SUCCESS) {
				sigMap = (Map)dataBinding.entryToObject(value);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		return sigMap;
	}

	/**
	 * retrieve the np significance table from the DB.
	 * @return Map of np -> signifcances.
	 * @throws DataStoreException
	 */

	public Map getNpSignificances() throws DataStoreException {
		Map sigMap = null;
		String name = "npSigMap";

		try {
			DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);

			if (status == OperationStatus.SUCCESS) {
				sigMap = (Map)dataBinding.entryToObject(value);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		return sigMap;
	}


	/**
	 * return the generics map which maps from type name to name of
	 * generic entity. It is stored in the definitionDB. Used by
	 * GenericFactHandler.
	 * @return Map of type name -> entity name
	 * @throws DataStoreException
	 */
	public Map getGenerics() throws DataStoreException {
		Map genMap = null;
		String name = "generics";

		try {
			DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));
			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);

			if (status == OperationStatus.SUCCESS) {
				genMap = (Map)dataBinding.entryToObject(value);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		return genMap;
	}



	/**
	 * get a single Fact. After retrieval the args are just Strings.
	 *
	 * @param name of fact
	 * @return Fact or null is not found.
	 * @throws DataStoreException
	 */

	public Fact getFact(String name) throws DataStoreException {
		Map objectMap = null;
		String arg1 = null;
		String arg2 = null;
		String predicate = null;
		Fact fact = null;

		try {
			DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));

			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = db.get(null, key, value, LockMode.DEFAULT);

			if (status == OperationStatus.SUCCESS) {
				objectMap = (Map)dataBinding.entryToObject(value);
				predicate = (String)objectMap.get("predicate");
				arg1 = (String)objectMap.get("arg1");
				arg2 = (String)objectMap.get("arg2");
				fact = new Fact(name, predicate, arg1, arg2, log);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		return fact;
	}



	/**
	 * print all objects in the DB
	 *
	 * @param out
	 * @throws DataStoreException
	 */
	public void listDB(String dbName, PrintStream out) throws DataStoreException{
		Database d = null;

		if(dbName.equalsIgnoreCase("db")) {
			d = db;
		} else if(dbName.equalsIgnoreCase("definitiondb")) {
			d = definitionDb;
		}

		Cursor cursor = null;
		try{
			cursor = d.openCursor(null, null);
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();


			while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String keyString = new String(foundKey.getData(), "UTF-8");
				Map dataMap = (Map)dataBinding.entryToObject(foundData);
				out.println(keyString);
				out.println(dataMap);
				out.println("-------------------------------");
			}
		} catch (Exception e) {
			throw new DataStoreException(e);
		} finally {
			try {
				cursor.close();
			} catch(Exception e) {
				System.out.println("Error closing cursor" + e);
				e.printStackTrace();
			}
		}
	}

	public UserModel getUserModel(String userTypeName) throws DataStoreException, UnknownUserTypeException, JDOMException, IOException, PredicateFormException {
		Map userMap = null;
		String name = "userTypes";

		try {
			DatabaseEntry key = new DatabaseEntry(name.getBytes("UTF-8"));

			DatabaseEntry value = new DatabaseEntry();
			OperationStatus status = definitionDb.get(null, key, value, LockMode.DEFAULT);

			if (status == OperationStatus.SUCCESS) {
				userMap = (Map)dataBinding.entryToObject(value);
			}
		} catch(Exception e) {
			throw new DataStoreException(e);
		}
		String userSpec = (String)userMap.get(userTypeName);
		if(userSpec == null ) {
			throw new UnknownUserTypeException(userTypeName);
		}
		String[] userSpecs = userSpec.split(":");
		String factsPerPage = userSpecs[0];
		String factsPerSentence = userSpecs[1];
		String cpDepth = userSpecs[2];
		String description = userSpecs.length > 3 ? userSpecs[3] : "";
		int fpp = Integer.parseInt(factsPerPage);
		int fps = Integer.parseInt(factsPerSentence);
		int cpd = Integer.parseInt(cpDepth);
		return new UserModel(config,userTypeName,fpp,fps,cpd,description);
	}

}
