package uk.ac.ed.methodius;

import com.sleepycat.je.Cursor;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class DataStoreCP extends DataStore {

    /* used for creating artifical fact names, for example fact_1322*/
    private int nameIndex = 0;


    // TODO: rethink this whole specials thing to try to generalise it
    // see DataStore.getEntity, Entity etc

    private List specials = Arrays.asList(
            new String[]{"type",
                    "name",
                    "shortname",
                    "gender",
                    "generic",
                    "number"
            });
    
    /*
     * This is a bit crap. Not all predicates are defined in the object
     * definitions. There seem to be a set of them which can occur
     * for any object but which aren't defined anywhere. Horrible!
     * TODO: change to explicit attribute definitions.
     */
    
    public DataStoreCP(Configuration c, boolean ro) {
        super(c, ro);
    }

    /**
     * create the content potential after the raw data has been loaded.
     * Takes an object which has an attribute A with value V and creates
     * a new fact with predicate A, arg1 takes the name of the original object
     * and arg2 has the value V. The pairing (A,V) in the original object gets
     * replaced by the name of the new fact. For example,
     * <p>
     * <code>
     * exhibit23 <br>
     * original-location: attica
     * current-location: getty-california
     * </code>
     * <p>
     * becomes
     * <p>
     * <code>
     * Exhibit23<br>
     * Facts: fact_232,fact_233
     * <p>
     * fact_232
     * predicate: original-location
     * arg1: exhibit23
     * arg2: attica
     * <p>
     * fact_233
     * predicate: current-location
     * arg1: exhibit23
     * arg2: getty-california
     * <p>
     * The treatment of predicates gives special meaning to five predicates:
     * type, name, shortname, gender, number and generic which are assumed to be strings.
     * Anything else is either the name of an entity in the DB or it's the id
     * of a string stored in the OpenCCG grammar.
     *
     * This is the mechanism used for proper names and canned text.
     *
     * @throws LoadException
     */

    public void createCP() throws LoadException {
        log.start("createCP");
        Cursor cursor = null;
        try {
            cursor = db.openCursor(null, null);
            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();
            nameIndex = 0;

            /*
             * Create the CP by iterating over the entire DB
             */

            OperationStatus status = cursor.getNext(foundKey, foundData, LockMode.DEFAULT);
            while(status == OperationStatus.SUCCESS) {
                String keyString = new String(foundKey.getData(), "UTF-8");

                /*
                 * but ignore anything called fact-XXXXX because that's
                 * a new fact we've just created and added to the DB
                 */

                if( !keyString.startsWith("fact")) {
                    log.output("Processing object: " + keyString);
                    Map dataMap = (Map)dataBinding.entryToObject(foundData);

                    /*
                     * Get the map of key->value bindings which are as they
                     * were defined in the instance file. We're going to iterate
                     * over these and break them out into facts to create the CP.
                     * As we do this ...
                     * original-location -> attica gets removed from the Entity's
                     * attribute map. We create a new fact object which points to
                     * the current Entity and to the Entity with id "attica".
                     * As we go ...
                     * keep a list of keys to remove from the map because
                     * we can't update and iterate over the attribute map at
                     * the same time
                     */

                    List keysToRemove = new LinkedList();
                    String facts = null;     /* the 'list' of new facts like fact-1234:fact-232:fact-478 */

                    /* iterate over the map of attributes and create a new fact for each */

                    Iterator iter = dataMap.keySet().iterator();
                    while(iter.hasNext()) {
                        String key = (String)iter.next();
                        log.output("Looking at key: " + key);

                        /*
                         * leave type, name, shortname, number and gender as strings
                         * everything else becomes a fact.
                         */

                        String arg2 = (String)dataMap.get(key);

                        if( !specials.contains(key)) {
                            String name = nextName();
                            if( facts == null ) {
                                facts = name;
                            } else {
                                facts = facts + ":" + name;
                            }

                            /*
                             * the new fact has a name and is a map with fields
                             * predicate, arg1 and arg2.
                             */
                            Map objMap = new HashMap();
                            objMap.put("predicate", key);
                            objMap.put("arg1", keyString);
                            objMap.put("arg2",arg2);
                            addToDatabase(name, objMap, db);
                            keysToRemove.add(key);
                        } else {

                        }
                    }

                    /*
                     * remove the facts from the original object
                     * and add the list of new fact names.
                     */

                    iter = keysToRemove.iterator();
                    while(iter.hasNext()){
                        String k = (String)iter.next();
                        dataMap.remove(k);
                    }
                    log.output("facts is " + facts);
                    dataMap.put("facts", facts);
                    //log.output("put the original object back.");
                    dataBinding.objectToEntry(dataMap, foundData);
                    db.put(null,foundKey, foundData);
                }
                status = cursor.getNext(foundKey, foundData, LockMode.DEFAULT);
            }
        } catch (Exception e) {
            throw new LoadException(e);
        } finally {
            try {
                cursor.close();
            } catch(Exception e) {
                System.out.println("Error closing cursor" + e);
                e.printStackTrace();
            }
        }
        log.output("CP created ... now synching DB.");
        checkDatabases();
        log.end("createCP: Creation of Content Potential Finished.");
        close();
    }

    
    /**
     * generate the next name for a fact when forming the CP.
     * @return
     */

    private String nextName() {
        nameIndex++;
        return "fact_" + nameIndex;
    }
    
}
