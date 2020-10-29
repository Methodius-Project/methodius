package uk.ac.ed.methodius;

import java.util.Hashtable;

import uk.ac.ed.methodius.Exceptions.DataStoreException;

public class MethodiusEngine {

    private Log log = null;
    private Publisher publisher;
    private Configuration config;
    private static String configFile;
    private DataStoreRead dataStoreRead;
    private Hashtable<String, UserModel> models;
    private Hashtable<String, String> types;

    public MethodiusEngine(String configString) {
        this(configString, true);
    }
    
    public MethodiusEngine(String configString, boolean comps) {
        System.out.println("INITIALIZING METHODIUS");
        /* first set up Methodius */

        log = new Log(System.out);
        Util.setLog(log);
        log.setNoOutput();
        configFile = configString;
        try {
            System.out.println("Using configuration: " + configFile );
            config = new Configuration(configFile, comps);

            config.setLog(log);
            // not read only since we want to do load and createCP with the appropriate data 
            dataStoreRead = new DataStoreRead(config, false);
            dataStoreRead.init();
//            dataStore.load();
//            dataStore.createCP();

            SignificanceHandler sh = new SignificanceHandler(dataStoreRead, config);
            config.setSignificanceHandler(sh);
            GenericFactHandler gfh = new GenericFactHandler(dataStoreRead,config);
            config.setGenericFactHandler(gfh);
            PredicateHandler ph = new PredicateHandler(dataStoreRead, config);
            config.setPredicateHandler(ph);
            AdverbHandler ah = new AdverbHandler(config);
            config.setAdverbHandler(ah);
            TypeHandler th = new TypeHandler(dataStoreRead, config, log);
            config.setTypeHandler(th);
            MethodiusRealizer mr = new MethodiusRealizer(config);
            config.setMethodiusRealizer(mr);
            models = new Hashtable<String, UserModel>();
            types = new Hashtable<String, String>();

            publisher = new Publisher(dataStoreRead, config);
        } catch(Exception e) {
            System.out.println("Exception thrown\n" + e);
            e.printStackTrace();
        }

    }

    private void createNewUser(String id, String type) {
        try {
            UserModel um = dataStoreRead.getUserModel("adult");

            models.put(id, um);
            types.put(id, type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String generateDescription(String userId, String exhibitId, String userType) {
        int exhibitHash = exhibitId.indexOf("#");
        String realExhibitId = exhibitId.substring(exhibitHash +1, exhibitId.length());
        
        try {
            if (!dataStoreRead.isInDb(realExhibitId)) {
                return "Could not generate description because " + realExhibitId + "is not in the database";
            }
        } catch (DataStoreException e1) {
            e1.printStackTrace();
        }
        String desc = null;
        int userTypeHash = userType.indexOf("#");
        String realUserType = userType.substring(userTypeHash +1, userType.length());
        // create a new user if we haven't seen this userId before
        if (!models.containsKey(userId)) {
            createNewUser(userId, realUserType);
        }
        UserModel um = models.get(userId);
        config.setUserModel(um);
        um.setSearchWidth(config.getMaxCPDepth());
        int nFacts = um.getFactsPerPage();
        um.setTargetSize(nFacts);
        System.out.println("trying to do description for " + realExhibitId + " user " + userId + " type " + realUserType);
        /* get the description and display it */
        String[] sentences = null;
        String exc = null;

        try {
            sentences = publisher.describe(realExhibitId);
        }
        catch (Exception e) {
            e.printStackTrace();
            exc = e.toString();
        }

        if (sentences == null) {
            desc = "Failed to generate a description because: " + exc;
        }
        else {
            for(int i = 0; i < sentences.length && sentences[i] != null; i++) {
                if(desc == null) {
                    desc = sentences[i];
                } else {
                    desc = desc + " " + sentences[i];
                }
            }
        }
        return desc;
    }
    
    // set facts per page for a specific user
    // returns true if this user exists, false otherwise
    
    public boolean setFactsPerPage(String userId, int num) {
        if (models.containsKey(userId)) {
            UserModel um = models.get(userId);
            um.setFactsPerPage(num);
            return true;
        }
        return false;
    }
    
    // set facts per sentence for a specific user
    // returns true if this user exists, false otherwise
    
    public boolean setFactsPerSentence(String userId, int num) {
        if (models.containsKey(userId)) {
            UserModel um = models.get(userId);
            um.setFactsPerPage(num);
            return true;
        }
        return false;
    }
}


