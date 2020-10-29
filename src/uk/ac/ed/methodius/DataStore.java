package uk.ac.ed.methodius;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;

import java.io.File;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.methodius.Exceptions.DataStoreException;
import uk.ac.ed.methodius.Exceptions.LoadException;

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

public class DataStore {

	Environment dbEnv = null;

	Database db = null;
	Database classDb = null;
	Database definitionDb = null;

	/* root dir for databases */
	String databaseDir = null;

	/* the DB for objects */
	String databaseName = null;

	/* class DB used for serialization API */
	String classDatabaseName = null;

	/* DB for object definitions */
	String definitionDatabaseName = null;

	/* used by serialization API */
	EntryBinding dataBinding = null;

	/* do we want to open the DB read only (for normal runs) or not (for loads) */
	boolean readOnly = false;

	/* is the DB opened and ready to access? */
	boolean initialised = false;
    
	Configuration config = null;

	Log log = null;

	/**
	 * create a DataStore from a configuration which
	 * contains all necessary info. Values are taken from the configuration
	 * for the log object, name of the directory to store the databases in,
	 * the name to use for the database, the class database (used for flattening)
	 * and the name for the definitions database.
	 *
	 * @param c Configuration Object
	 */

	public DataStore(Configuration c, boolean ro) {
		config = c;
		readOnly = ro;
		log = config.getLog();
		log.start("DataStore.");
		databaseDir = config.getDatabaseDir();
		databaseName = config.getDatabaseName();
		classDatabaseName = config.getClassDatabaseName();
		definitionDatabaseName = config.getDefinitionDatabaseName();
		log.end("DataStore");

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
	
	protected void addToDatabase(String key, Map dataMap, Database db) {
        log.output( "\nAdding entry " + key + " with attributes:" + dataMap);
	    try {
            DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
            DatabaseEntry theData = new DatabaseEntry();
            dataBinding.objectToEntry(dataMap, theData);
            db.put(null, theKey, theData);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (DatabaseException e1) {
            e1.printStackTrace();
        }
	}
	
	protected void checkDatabases() throws LoadException {
        log.output("Load Finished: close and reopen DB.");
        try{
            close();
            init();
        } catch(Exception e) {
            throw new LoadException("Problem synching DB.", e);
        }
	}
	
}
