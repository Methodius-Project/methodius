package uk.ac.ed.methodius;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdf.model.impl.RDFListImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import uk.ac.ed.methodius.Exceptions.DataStoreException;
import uk.ac.ed.methodius.Exceptions.LoadException;
import uk.ac.ed.methodius.Exceptions.PredicateFormException;

/**
 * handles the RDF/OWL database loading
 */

@SuppressWarnings("unchecked")

public class DataStoreLoadRdf extends DataStore {

    private OntModel owlModel;
    private OntModel lexiconModel;
    private OntModel microplanModel;
    private OntModel userModelModel;

    private static String ONTOLOGY_BASE;
    private static String OWLNL_BASE;

    private static OntClass CANNED_TEXT_ONTOLOGY_CLASS;

    private static Property RANGE_PROP = new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#range");
    private static Property DOMAIN_PROP = new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#domain");
    private static Property TYPE_PROP = new PropertyImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static Property UNION_PROP = new PropertyImpl("http://www.w3.org/2002/07/owl#unionOf");
    private static Property HAS_NP_PROP;
    private static Property HAS_CT_PROP;
    private static Property VAL_PROP;
    private static Property OWLNL_PROPERTY_PROP;

    private static Property ENGLISH_MICROPLANS_PROP;
    private static Property COMPARISONS_PROP;
    private static Property USED_PROP;
    private static Property AGGR_PROP;
    private static Property SLOTS_PROP;
    private static Property VERB_PROP;
    private static Property OWNER_PROP;
    private static Property PREP_PROP;
    private static Property FILLER_PROP;
    private static Property TEXT_PROP;
    private static Property ADVERB_PROP;
    private static Property TENSE_PROP;
    private static Property VOICE_PROP;
    private static Property REFEXP_TYPE_PROP;
    private static Property REFEXP_CASE_PROP;

    private static Property USER_TYPE_PROP;
    private static Property FACTS_PAGE_PROP;
    private static Property MAX_FACTS_SENTENCE_PROP;

    // NP properties
    private static Property LANGUAGES_NP_PROP;
    private static Property ENGLISH_NP_PROP;
    private static Property GREEK_NP_PROP;
    private static Property SINGULAR_PROP;
    private static Property GENDER_PROP;
    private static Property PLURAL_PROP;
    private static Property NOMINATIVE_PROP;
    private static Property ACCUSATIVE_PROP;
    private static Property GENITIVE_PROP;

    private static Property SINGULAR_FORMS_PROP;
    private static Property PLURAL_FORMS_PROP;

    private static String MISSING_NOUN = "missingNoun";

    private static OntClass NP_CLASS;
    private static OntClass ENGLISH_NP_CLASS;
    private static OntClass GREEK_NP_CLASS;
    private static OntClass CANNED_TEXT_CLASS;
    private static OntClass OWLNL_PROPERTY_CLASS;

    private static String SHORT_ENGLISH_NP_STRING = "EnglishNP";
    private static String SHORT_GREEK_NP_STRING = "GreekNP";
    private static String ENGLISH_NP_STRING;
    private static String GREEK_NP_STRING;
    private static String SHORT_CANNED_TEXT_STRING = "CannedText";
    private static String CANNED_TEXT_STRING;
    private static String ENGLISH_LANG = "en";
    private static String GREEK_LANG = "el";

    private HashSet<String> classes;
    private HashMap<String,String> classNPs;
    private HashMap<String,String> instanceNPs;
    private HashMap<String,String> instanceCTs;
    


    private OpenCCG openCCG;

    //   private HashMap<String, OpenCCGEntry> newEntriesEnglish;
    //   private HashMap<String, OpenCCGEntry> newEntriesGreek;

    private OpenCCGEntries newEntriesEnglish;
    private OpenCCGEntries newEntriesGreek;
    private HashSet<String> microplanVerbs;
    
    private HashMap<String,String> typesForOpenCCG;

    //    private HashMap<String, HashMap<String,String>> instancesForOntologyEdit;


    public DataStoreLoadRdf(Configuration c, boolean ro) {
        super(c, ro);

    }

    /**
     * load the DB from the various RDF/OWL files.
     *
     * @throws LoadException
     * @throws PredicateFormException 
     */

    public void load() throws LoadException {
        log.start("loadOwlRdf");

        owlModel = ModelFactory.createOntologyModel();
        lexiconModel = ModelFactory.createOntologyModel();
        microplanModel = ModelFactory.createOntologyModel();
        userModelModel = ModelFactory.createOntologyModel();

        try {
            owlModel.read(new BufferedReader(new java.io.FileReader(config.getOwlFileName())), "");
            lexiconModel.read(new BufferedReader(new java.io.FileReader(config.getLexiconFileName())), "");
            microplanModel.read(new BufferedReader(new java.io.FileReader(config.getMicroplansFileName())), "");
            userModelModel.read(new BufferedReader(new java.io.FileReader(config.getUserModellingFileName())), "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream("/group/ltg/users/amyi/methodius/software/methodius/test/tripltest4");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bos = new BufferedOutputStream(fos);
        microplanModel.write(bos, "TURTLE");
        ONTOLOGY_BASE = owlModel.getNsPrefixURI("");
        OWLNL_BASE = lexiconModel.getNsPrefixURI("owlnl");
        assignStaticFields();

        classes = new HashSet<String>();
        classNPs = new HashMap<String,String>();
        instanceNPs = new HashMap<String,String>();
        instanceCTs = new HashMap<String,String>();
        microplanVerbs = new HashSet<String>();
        typesForOpenCCG = new HashMap<String,String>();

        //        instancesForOntologyEdit = new HashMap<String, HashMap<String,String>>();

        loadClasses();
        storeTypes();
        loadSigs();
        loadUserTypes();
        loadLexicon();
        storeInstances();
        storeLexicon();
        loadMicroplans();

        openCCG = new OpenCCG(config);
        openCCG.readDict();
        //       HashSet<OpenCCGEntry> oldEntries = openCCG.getEntries();
        OpenCCGEntries oldEntries = openCCG.getEntries();
        for (String microVerb : microplanVerbs) {
            boolean exists = oldEntries.verbFormExists(microVerb);
            log.output("verb form " + microVerb + " " + exists);
            if (exists) {
                HashSet<OpenCCGWord> oldWords = oldEntries.getWordsWithForm(microVerb);
                for (OpenCCGWord oldWord : oldWords) {
                    log.output(oldWord.toString());
                }
            }
        }
        //        for (OpenCCGEntry englishNoun : newEntriesEnglish.values()) {
        for (OpenCCGEntry englishNoun : newEntriesEnglish.values()) {
            if (oldEntries.contains(englishNoun)) {
                log.output("EXISTS " + englishNoun);
            }
            else if (oldEntries.containsStem(englishNoun.getStem())) {
                log.output("EXISTS STEM " + englishNoun);
                log.output("OTHER IS " + oldEntries.getStemEntry(englishNoun.getStem()));
            }
            else {
                log.output("NOT THERE " + englishNoun);
            }
        }

        //        log.output("classNPS is " + classNPs);
        //        log.output("instanceNPs is " + instanceNPs);
        //        log.output("instanceCTs is " + instanceCTs);
        //        log.output("new English nouns is " + newEntriesEnglish);
        //        log.output("new Greek nouns is " + newEntriesGreek);


        close();
        log.end("loadOwlRdf");   
    }


    private void loadClasses() {
        log.start("loadClasses");
        ExtendedIterator it = null;
        try {
            it = owlModel.listNamedClasses();
            while (it.hasNext()) {
                //	    for (Iterator<OntClass> it = owlModel.listNamedClasses(); it.hasNext();) {
                OntClass cls = (OntClass) it.next();
                classes.add(cls.toString());
            }
        }
        finally {
            if (it != null) {
                it.close();
                it = null;
            }
        }
        log.end("loadClasses");
    }

    private void storeLexicon() {
        log.start("storeLexicon");
        newEntriesEnglish = new OpenCCGEntries();
        newEntriesGreek = new OpenCCGEntries();

        log.output("NP_CLASS is " + NP_CLASS);

        ExtendedIterator extIt = null;
        try {
            extIt = lexiconModel.listIndividuals(NP_CLASS);
            while (extIt.hasNext()) {
                //	              for (Iterator extIt = lexiconModel.listIndividuals(NP_CLASS); it.hasNext();) {
                Resource multiNp = (Resource)extIt.next();

                String stem = multiNp.getLocalName().toLowerCase();
                log.output("found np " + stem);
                List npList = getNpList(multiNp);

                for (Iterator npIter = npList.iterator(); npIter.hasNext();) {
                    Resource np = (Resource)npIter.next();
                    String thisType = np.getProperty(TYPE_PROP).getObject().toString();
                    if (thisType.equals(ENGLISH_NP_STRING)) {
                        log.output("doing English np");
                        String singString = getOwlnlVal(np, SINGULAR_PROP);
                        String plurString = getOwlnlVal(np, PLURAL_PROP);
                        boolean sameString = singString.equals(plurString);
                        OpenCCGEntry newEntryEnglish = new OpenCCGEntry();
                        String openCCGClass = typesForOpenCCG.get(stem);
                        if (openCCGClass != null) {
                            newEntryEnglish.setOpenCCGClass(openCCGClass);
                        }
                        //	                      log.output(newEntryEnglish.toString());
                        newEntryEnglish.setPos("NNP");
                        //	                      log.output(newEntryEnglish.toString());
                        newEntryEnglish.setStem(stem);
                        //	                      log.output(newEntryEnglish.toString());
                        if (!singString.equals("")) {
                            OpenCCGWord singWord = new OpenCCGWord(singString.replace(" ", "_").replace("__", "_"));
                            singWord.addMacro(OpenCCG.SING_NOUN_MACRO);
                            newEntryEnglish.addWord(singWord);
                            //	                          log.output(newEntryEnglish.toString());
                            if (!sameString && !plurString.equals("")) {
                                OpenCCGWord plurWord = new OpenCCGWord(plurString.replace(" ", "_").replace("__", "_"));
                                plurWord.addMacro(OpenCCG.PLUR_NOUN_MACRO);
                                newEntryEnglish.addWord(plurWord);
                                //	                              log.output(newEntryEnglish.toString());
                            }
                        }
                        else {
                            if (!sameString && !plurString.equals("")) {
                                OpenCCGWord plurWord = new OpenCCGWord(plurString.replace(" ", "_").replace("__", "_"));
                                plurWord.addMacro(OpenCCG.PLUR_NOUN_MACRO);
                                newEntryEnglish.addWord(plurWord);
                                //	                              log.output(newEntryEnglish.toString());
                            }
                            else {
                                OpenCCGWord singWord = new OpenCCGWord(MISSING_NOUN);
                                singWord.addMacro(OpenCCG.SING_NOUN_MACRO);
                                newEntryEnglish.addWord(singWord);
                                //	                              log.output(newEntryEnglish.toString());
                            }
                        }
                        //	                      newEntriesEnglish.put(stem, newEntryEnglish);
                        log.output("ADD TO NEW LIST " + newEntryEnglish.toString());
                        newEntriesEnglish.addEntry(newEntryEnglish);
                    }
                    else if (thisType.equals(GREEK_NP_STRING)) {
                        log.output("doing Greek np");
                        String wordString = null;
                        StatementImpl singObj = (StatementImpl)np.getProperty(SINGULAR_PROP);
                        if (singObj != null) {
                            HashMap<String,String> singNouns = getNounCases(singObj);
                            wordString = singNouns.get("nom");
                            if (wordString == null) {
                                wordString = singNouns.get("acc");
                            }
                            if (wordString == null) {
                                wordString = singNouns.get("gen");
                            }
                            if (wordString == null) {
                                StatementImpl plurObj = (StatementImpl)np.getProperty(PLURAL_PROP);
                                if (plurObj != null) {
                                    HashMap<String,String> plurNouns = getNounCases(plurObj);
                                    wordString = plurNouns.get("nom");
                                    if (wordString == null) {
                                        wordString = plurNouns.get("acc");
                                    }
                                    if (wordString == null) {
                                        wordString = plurNouns.get("gen");
                                    }
                                }
                            }
                        }
                        /*
	                    if (wordString == null) {
	                        wordString = missingNoun;
	                    }
                         */
                        if (wordString != null) {
                            log.output("adding new greek entry with word " + wordString);
                            OpenCCGEntry newEntryGreek = new OpenCCGEntry();
                            newEntryGreek.setPos("NNP");
                            newEntryGreek.setStem(stem);
                            OpenCCGWord greekWord = new OpenCCGWord(wordString.replace(" ", "_").replace("__", "_"));
                            greekWord.addMacro(OpenCCG.SING_NOUN_MACRO);
                            newEntryGreek.addWord(greekWord);
                            //	                          newEntriesGreek.put(stem, newEntryGreek);
                            newEntriesGreek.addEntry(newEntryGreek);
                        }
                    }
                }
            }
        }
        finally {
            if (extIt != null) {
                extIt.close();
                extIt = null;
            }
        }

        try {
            extIt = lexiconModel.listIndividuals(CANNED_TEXT_CLASS);

            while (extIt.hasNext()) {
                //      for (Iterator it = lexiconModel.listIndividuals(CANNED_TEXT_CLASS); it.hasNext();) {
                Resource cannedText = (Resource)extIt.next();
                log.output("found ct " + cannedText);
                String stem = cannedText.getLocalName().toLowerCase();
                for (Iterator cannedIter = cannedText.listProperties(VAL_PROP); cannedIter.hasNext();) {
                    StatementImpl cannedObj = (StatementImpl)cannedIter.next();
                    if (cannedObj != null) {
                        String cannedString = cannedObj.getString();
                        String cannedLang = cannedObj.getLanguage();
                        if (cannedLang.equals(ENGLISH_LANG) && !cannedString.trim().equals("")) {
                            log.output("adding entry for english ct");
                            OpenCCGEntry newEntry = new OpenCCGEntry();
                            //	                          log.output(newEntry.toString());
                            //                        newEntry.setStem(cannedString.replace(" ", "_").replace("__", "_"));
                            newEntry.setStem(stem);
                            //	                          log.output(newEntry.toString());
                            newEntry.setOpenCCGClass("canned-text");
                            //	                          log.output(newEntry.toString());
                            newEntry.setPos("canned-text");
                            //	                          log.output(newEntry.toString());
                            // we don't know the family here, will get it from the canned text in the
                            // .owl file (or maybe deduce from the microplan??) so set it to null for now
                            //	                          newEntry.addMemberOf(stem, null);
                            //	                          newEntry.addMemberOf(null)
                            OpenCCGWord newWord = new OpenCCGWord(cannedString.replace(" ", "_").replace("__", "_"));
                            newEntry.addWord(newWord);
                            //	                          log.output(newEntry.toString());
                            //	                          newEntriesEnglish.put(stem, newEntry);
                            log.output("ADD TO NEW LIST " + newEntry.toString());
                            newEntriesEnglish.addEntry(newEntry);
                        }
                        else if (cannedLang.equals(GREEK_LANG) && !cannedString.trim().equals("")) {
                            log.output("adding entry for greek ct");
                            OpenCCGEntry newEntry = new OpenCCGEntry();
                            //                      newEntry.setStem(cannedString.replace(" ", "_").replace("__", "_"));
                            newEntry.setStem(stem);
                            newEntry.setOpenCCGClass("canned-text");
                            newEntry.setPos("canned-text");
                            // we don't know the family here, will get it from the canned text in the
                            // .owl file (or maybe deduce from the microplan??) so set it to null for now
                            //	                          newEntry.addMemberOf(stem, null);
                            //	                          newEntry.addMemberOf(null);
                            OpenCCGWord newWord = new OpenCCGWord(cannedString.replace(" ", "_").replace("__", "_"));
                            newEntry.addWord(newWord);
                            newEntriesGreek.addEntry(newEntry);
                            //	                          newEntriesGreek.put(stem, newEntry);
                        }
                    }
                }
            }
        }
        finally {
            if (extIt != null) {
                extIt.close();
                extIt = null;
            }
        }

        log.end("storeLexicon");
    }


    private void loadLexicon() {
        log.start("loadLexicon");
        ResIterator it = null;
        try {
            it = lexiconModel.listResourcesWithProperty(HAS_NP_PROP);
            while (it.hasNext()) {
                //	    for (ResIterator it = lexiconModel.listResourcesWithProperty(HAS_NP_PROP); it.hasNext();) {
                Resource res = (Resource)it.next();
                String shortRes = res.getLocalName().toLowerCase();
                StatementImpl st = ((StatementImpl)res.getProperty(HAS_NP_PROP));
                String newString = ((Resource)st.getObject()).getLocalName().toLowerCase();
                if (classes.contains(res.toString())) {
                    String newNP;
                    String existingNP = classNPs.get(shortRes);
                    if (existingNP == null) {
                        newNP = newString;
                    }
                    else {
                        newNP = existingNP + ":" + newString;
                    }
                    classNPs.put(shortRes, newNP);
                }
                else {
                    instanceNPs.put(shortRes, newString);
                }
            }
        }
        finally {
            if (it != null) {
                it.close();
                it = null;
            }
        }

        Map cannedTextDataMap = new HashMap();
        
        try {
            it = lexiconModel.listResourcesWithProperty(HAS_CT_PROP);
            Map dataMap = new HashMap();
            while (it.hasNext()) {
                //	    for (ResIterator it = lexiconModel.listResourcesWithProperty(HAS_CT_PROP); it.hasNext();)  {
                Resource res = (Resource)it.next();
                String shortRes = res.getLocalName().toLowerCase();
                log.output("ct is " + res);
 //               StatementImpl st = ((StatementImpl)res.getProperty(HAS_CT_PROP));

                for (StmtIterator ctIter = res.listProperties(HAS_CT_PROP); ctIter.hasNext();) {
                    StatementImpl st = (StatementImpl)ctIter.next();
                    Resource ct = (Resource)st.getObject();
                    for (StmtIterator otherIter = ct.listProperties(); otherIter.hasNext();) {
                        log.output(otherIter.next().toString());
                    }

                    log.output("ct is " + ((Resource)st.getObject()).listProperties());
                    String newString = ((Resource)st.getObject()).getLocalName().toLowerCase();
                    instanceCTs.put(shortRes, newString);
                }
            }
        }
        finally {
            if (it != null) {
                it.close();
                it = null;
            }
        }
        //	    newEntriesEnglish = new HashMap<String, OpenCCGEntry>();
        //	    newEntriesGreek = new HashMap<String, OpenCCGEntry>();

        log.end("loadLexicon");
    }

    private void loadMicroplans() {
        log.start("loadMicroplans");
        ExtendedIterator extIt = null;
        try {
            extIt = microplanModel.listIndividuals(OWLNL_PROPERTY_PROP);
            while (extIt.hasNext()) {
                ///	    for (Iterator extIt = microplanModel.listIndividuals(OWLNL_PROPERTY_PROP); it.hasNext();) {
                Individual pred = (Individual)extIt.next();
                String longPredName = pred.toString();
                String predName = pred.getLocalName().toLowerCase();
                log.output("processing predicate " + predName);
                ObjectProperty op = owlModel.getObjectProperty(longPredName);
                if (op == null) {
                    continue;
                }
                HashSet<String> arg1Set = new HashSet<String>();
                HashSet<String> arg2Set = new HashSet<String>();
                Resource arg1Res = op.getDomain();
                log.output("arg1 is " + arg1Res);
                if (arg1Res.isAnon()) {
                    StatementImpl arg1s = (StatementImpl)arg1Res.getProperty(UNION_PROP);
                    List arg1List = ((RDFListImpl)arg1s.getObject().as(RDFList.class)).asJavaList();
                    log.output(arg1List.toString());
                    for (Object longArg : arg1List) {
                        arg1Set.add(((ResourceImpl)longArg).getLocalName().toLowerCase());
                    }
                }
                else {
                    arg1Set.add(arg1Res.getLocalName().toLowerCase());
                }

                Resource arg2Res = op.getRange();
                log.output("arg2 is " + arg2Res);
                if (arg2Res.isAnon()) {
                    StatementImpl arg2s = (StatementImpl)arg2Res.getProperty(UNION_PROP);
                    List arg2List = ((RDFListImpl)arg2s.getObject().as(RDFList.class)).asJavaList();
                    log.output(arg2List.toString());
                    for (Object longArg : arg2List) {
                        arg2Set.add(((ResourceImpl)longArg).getLocalName().toLowerCase());
                    }
                }
                else {
                    arg2Set.add(arg2Res.getLocalName().toLowerCase());
                }

                Map dataMap = new HashMap();
                String comps = ((StatementImpl)pred.getProperty(COMPARISONS_PROP)).getString();
                dataMap.put("comp", comps);
                StatementImpl englishMicroplans = (StatementImpl)pred.getProperty(ENGLISH_MICROPLANS_PROP);
                List microplanList = ((RDFListImpl)englishMicroplans.getObject().as(RDFList.class)).asJavaList();
                String expList = "";
                for (Iterator microplanIter = microplanList.iterator(); microplanIter.hasNext();) {
                    ResourceImpl microplan = (ResourceImpl)microplanIter.next();
                    String used = microplan.getProperty(USED_PROP).getString().toLowerCase();
                    if (used.equalsIgnoreCase("true")) {
                        String expName = microplan.getLocalName().toLowerCase();
                        HashSet<String> names = storeExpression(microplan, expName, arg1Set, arg2Set);
                        for (String newExpName : names) {
                            if (!expList.equals("")) {
                                expList += ":";
                            }
                            expList += newExpName;
                        }
                    }
                }
                log.output("English expressions are " + expList);
                dataMap.put("expressions", expList);
                addToDatabase(predName + "-pred", dataMap, definitionDb);
            }
        }
        finally {
            if (extIt != null) {
                extIt.close();
                extIt = null;
            }
        }
        log.end("loadMicroplans");
    }

    private void loadUserTypes() throws LoadException {
        log.start("loadUserTypes");
        String cpDepth = "5";
        String description = "";
        Map dataMap = new HashMap();
        for (ExtendedIterator userTypeIt = userModelModel.listIndividuals(USER_TYPE_PROP); userTypeIt.hasNext();) {
            Individual userType = (Individual) userTypeIt.next();
            String userTypeString = userType.getLocalName().toLowerCase();
            log.output("user type is " + userTypeString);
            String sentenceFacts = userType.getProperty(MAX_FACTS_SENTENCE_PROP).getString();
            log.output("sentence facts " + sentenceFacts);
            String pageFacts = userType.getProperty(FACTS_PAGE_PROP).getString();
            log.output("page facts " + pageFacts);
            String storageStr = pageFacts + ":" + sentenceFacts + ":" + cpDepth + ":" + description;
            dataMap.put(userTypeString, storageStr);
        }
        log.output("Storing UserTypes Table as: " + dataMap);
        String key = "userTypes";
        addToDatabase(key, dataMap, definitionDb);
        checkDatabases();
        log.end("loadUserTypes");
    }

    private void loadSigs() throws LoadException {
        log.start("loadSigs");
        for (ExtendedIterator sigIt = userModelModel.listIndividuals(OWLNL_PROPERTY_PROP); sigIt.hasNext();) {
            Individual sigIndividual = (Individual) sigIt.next();
            String sigName = sigIndividual.getLocalName().toLowerCase();
            //			log.output("sig name is " + sigName);
            //			String comps = ((StatementImpl)sigIndividual.getProperty(COMPARISONS_PROP)).getString();


        }

        Map predicateSigMap = new HashMap();
        Map expressionSigMap = new HashMap();
        Map npSigMap = new HashMap();
        addToDatabase("predicateSigMap", predicateSigMap, definitionDb);
        addToDatabase("expressionSigMap", expressionSigMap, definitionDb);
        addToDatabase("npSigMap", npSigMap, definitionDb);

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

        checkDatabases();
        log.end("loadSigs");
    }

    private HashSet<String> storeExpression(ResourceImpl microplan, String expName, HashSet<String> arg1Types, HashSet<String> arg2Types) {
        log.start("storeExpression");
        log.output("processing expression " + expName);
        HashSet<String> expNames = new HashSet<String>();
        int typesCounter = 0;
        for (String arg1Type : arg1Types) {
            for (String arg2Type : arg2Types) {
                String thisExpName = expName + typesCounter++;
                Map dataMap = new HashMap();
                dataMap.put("arg1Type", arg1Type);
                dataMap.put("arg2Type", arg2Type);
                String agg = microplan.getProperty(AGGR_PROP).getString().toLowerCase();
                dataMap.put("aggregationAllowedBefore", agg);
                dataMap.put("aggregationAllowedAfter", agg);
                List microplanSlotsList = ((RDFListImpl)microplan.getProperty(SLOTS_PROP).getObject().as(RDFList.class)).asJavaList();
                for (Iterator microplanSlotsIter = microplanSlotsList.iterator(); microplanSlotsIter.hasNext();) {
                    ResourceImpl microplanSlot = (ResourceImpl)microplanSlotsIter.next();
                    if (microplanSlot.hasProperty(TYPE_PROP, VERB_PROP)) {
                        String tense = microplanSlot.getProperty(TENSE_PROP).getString().toLowerCase();
                        if (tense.equalsIgnoreCase("present")) {
                            tense = "pres";
                        }
                        String voice = microplanSlot.getProperty(VOICE_PROP).getString().toLowerCase();
                        String maybeVerb = microplanSlot.getProperty(VAL_PROP).getString().toLowerCase();
                        String verb = fixUpVerb(maybeVerb);
                        String verbString = removeExtraWords(maybeVerb);
                        microplanVerbs.add(verbString);
                        dataMap.put("verb", verb);
                        dataMap.put("tense",tense);
                        dataMap.put("voice", voice);
                    }
                    if (microplanSlot.hasProperty(TYPE_PROP, PREP_PROP)) {
                        log.output("prep is " + microplanSlot);
                        //	                    dataMap.put("preposition", prepString);
                    }
                }
                log.output("exp data map is " + dataMap);
                addToDatabase(thisExpName, dataMap, definitionDb);
                expNames.add(thisExpName);
            }
        }
        log.end("storeExpression");
        return expNames;
    }

    
    private String removeExtraWords(String verb) {
        String[] words = verb.split(" ");
        if (words.length > 1) {
            return words[1];
        }
        return words[0];
    }
    
    
    private String fixUpVerb(String maybeVerb) {
        if (maybeVerb.equals("is") || maybeVerb.equals("are") || maybeVerb.equals("was") || maybeVerb.equals("were")) {
            return "be-verb";
        }
        if (maybeVerb.equals("has") || maybeVerb.equals("have") || maybeVerb.equals("had")) {
            return "have-verb";
        }
        int length = maybeVerb.length();
        String wasStart = "was ";
        String isStart = "is ";
        String hadStart = "had ";
        String hasStart = "has ";
        String verbWord;

        String fixedVerbWord;
        if (maybeVerb.startsWith(wasStart)) {
            if ((maybeVerb.endsWith("d") || maybeVerb.endsWith("t") || maybeVerb.endsWith("n"))) {
                verbWord = maybeVerb.substring(wasStart.length(), length);
                fixedVerbWord = removePast(verbWord);
                log.output(maybeVerb + " past passive of " + fixedVerbWord);
            }
            else if (maybeVerb.equals("was made")) {
                verbWord = maybeVerb.substring(wasStart.length(), length);
                fixedVerbWord = "make-verb";
                log.output(maybeVerb + " past passive of " + fixedVerbWord);
            }
            else {
                fixedVerbWord = "be-verb";
                log.output(maybeVerb + " text remaining from verb is " + maybeVerb.substring(wasStart.length(), length));
            }
        }

        else if (maybeVerb.startsWith(hadStart)) {
            if ((maybeVerb.endsWith("d") || maybeVerb.endsWith("t") || maybeVerb.endsWith("n"))) {
                verbWord = maybeVerb.substring(hadStart.length(), length);
                fixedVerbWord = removePast(verbWord);
                log.output(maybeVerb + " past active of " + fixedVerbWord);
            }
            else {
                fixedVerbWord = "have-verb";
                log.output(maybeVerb + " text remaning from verb is " + maybeVerb.substring(wasStart.length(), length));
            }
        }

        else if (maybeVerb.startsWith(isStart)) {
            if ((maybeVerb.endsWith("d") || maybeVerb.endsWith("t") || maybeVerb.endsWith("n") || maybeVerb.endsWith("de"))) {
                verbWord = maybeVerb.substring(isStart.length(), length);
                fixedVerbWord = removePast(verbWord);
                log.output(maybeVerb + " present passive of " + fixedVerbWord);
            }
            else  if (maybeVerb.equalsIgnoreCase("is made")) {
                fixedVerbWord = "make-verb";
            }
            else {
                fixedVerbWord = "be-verb";
                log.output(maybeVerb + " text remaning from verb is " + maybeVerb.substring(wasStart.length(), length));
            }
        }
        else {
            fixedVerbWord = maybeVerb;
        }
        return fixedVerbWord;
    }

    private String removePast(String participle) {
        if (participle.equals("built")) {
            return "build-verb";
        }
        if (participle.equals("ordered")) {
            return "order-verb";
        }
        String singString = participle.substring(0, participle.length() - 1);
        if (singString.endsWith("ye")) {
            singString = singString.substring(0, singString.length() - 1);
        }
        String returnString = singString + "-verb";
//        String returnString = singString;
        return returnString;
    }

    private void storeInstances() throws LoadException {
        HashMap<String, Integer> duplicates = new HashMap<String, Integer>();
        log.start("rdfStoreInstances");
        HashSet<Individual> individuals = new HashSet<Individual>();
        ExtendedIterator extIt = null;
        try {
            extIt = owlModel.listIndividuals();
            while (extIt.hasNext()) {
                //	    for (Iterator<Individual> extIt = owlModel.listIndividuals(); extIt.hasNext();) {
                individuals.add((Individual)extIt.next());
            }
        }
        finally {
            if (extIt != null) {
                extIt.close();
                extIt = null;
            }
        }

        for (Individual individual : individuals) {
            String key = individual.getLocalName().toLowerCase();
            String originalKey = individual.getLocalName();

            log.output("individual is " + key);
            String type = individual.getOntClass().getLocalName().toLowerCase();
            HashMap<String,String> dataMap = new HashMap<String,String>();
            dataMap.put("type", type);

            StmtIterator propIt = null;
            try {
                propIt = individual.listProperties();

                while (propIt.hasNext()) {
                    Statement s = propIt.nextStatement();
                    Property p = s.getPredicate();
                    if (!(p.equals(TYPE_PROP) || p.equals(DOMAIN_PROP) || p.equals(RANGE_PROP) || p.equals(UNION_PROP))) {
                        String prop = p.getLocalName().toLowerCase();
                        String obj = s.getResource().getLocalName().toLowerCase();
                        dataMap.put(prop, obj);
                    }
                }				
            }	
            finally {
                if (propIt != null) {
                    propIt.close();
                    propIt = null;
                }
            }

            String thisNP = instanceCTs.get(key);
            if (thisNP == null) {
                thisNP = instanceNPs.get(key);
            }
            else {
                String[] cts = thisNP.split(":");
                
            }
            if (thisNP != null) {
                dataMap.put("name", thisNP);
                typesForOpenCCG.put(thisNP, type);
            }

            addToDatabase(key, (HashMap)dataMap.clone(), db);

            OntClass theClass = individual.getOntClass();
            dataMap.put("canned", "false");

            if (theClass.hasSuperClass(CANNED_TEXT_ONTOLOGY_CLASS)) {
                dataMap.put("canned", "true");
            }

        }

        checkDatabases();
        log.end("rdfStoreInstances");
    }

    private void storeTypes() throws LoadException {
        log.start("rdfStoreTypes");
        Map typesDataMap = new HashMap();
        for (Iterator<String> classIter = classes.iterator(); classIter.hasNext();) {
            Map dataMap = new HashMap();
            String className = classIter.next();
            OntClass ontClass = owlModel.getOntClass(className);
            String shortName = ontClass.getLocalName().toLowerCase();

            String parents = null;
            ExtendedIterator superIter = null;
            try {
                superIter = ontClass.listSuperClasses(true);
                while (superIter.hasNext()) {
                    //            for (Iterator<OntClass> superIter = ontClass.listSuperClasses(true); superIter.hasNext();) {
                    String parentName = ((OntClass)superIter.next()).getLocalName().toLowerCase();
                    if (parentName.equals("resource")) {
                        continue;
                    }
                    if (parentName.equals("basic-entity-types")) {
                        parentName = "basic";
                    }
                    if (parents == null) {
                        parents = parentName;
                    }
                    else {
                        parents += ":" + parentName;
                    }
                }
            }
            finally {
                if (superIter != null) {
                    superIter.close();
                    superIter = null;
                }
            }
            if (parents == null) {
                parents = "basic";
            }
            dataMap.put("parents", parents);
            dataMap.put("nps", classNPs.get(shortName));
            typesDataMap.put(shortName, dataMap);
        }

        String key = "typesDataMap";

        addToDatabase(key, typesDataMap, definitionDb);
        checkDatabases();
        log.end("rdfStoreTypes");
    }

    private List getNpList(Resource npRes) {
        StatementImpl languagesNpProp = (StatementImpl)npRes.getProperty(LANGUAGES_NP_PROP);
        ResourceImpl languagesNp = (ResourceImpl)languagesNpProp.getObject();
        List npList = ((RDFListImpl)languagesNp.as(RDFList.class)).asJavaList();
        return npList;
    }

    private String getOwlnlVal(Resource r, Property p) {
        String returnString = ((LiteralImpl)r.getProperty(p).getObject()).getString();
        //	    returnString.replace(" ", "_").replace("__", "_");
        return returnString;
    }

    private HashMap<String,String> getNounCases(StatementImpl nounList) {
        HashMap returnMap = new HashMap<String,String>();
        ResourceImpl nextObj = (ResourceImpl)nounList.getObject();

        Statement nomStatement = nextObj.getProperty(NOMINATIVE_PROP);
        if (nomStatement != null) {
            returnMap.put("nom", nomStatement.getString());
        }
        Statement accStatement = nextObj.getProperty(ACCUSATIVE_PROP);
        if (accStatement != null) {
            returnMap.put("acc", accStatement.getString());
        }
        Statement genStatement = nextObj.getProperty(GENITIVE_PROP);
        if (genStatement != null) {
            returnMap.put("gen", genStatement.getString());
        }

        return returnMap;
    }

    private void assignStaticFields() {
        HAS_NP_PROP = new PropertyImpl(OWLNL_BASE + "hasNP");
        HAS_CT_PROP = new PropertyImpl(OWLNL_BASE + "hasCannedText");
        LANGUAGES_NP_PROP = new PropertyImpl(OWLNL_BASE + "LanguagesNP");
        SINGULAR_PROP = new PropertyImpl(OWLNL_BASE + "singular");
        GENDER_PROP = new PropertyImpl(OWLNL_BASE + "gender");
        PLURAL_PROP = new PropertyImpl(OWLNL_BASE + "plural");
        NOMINATIVE_PROP= new PropertyImpl(OWLNL_BASE + "nominative");
        ACCUSATIVE_PROP= new PropertyImpl(OWLNL_BASE + "accusative");
        GENITIVE_PROP= new PropertyImpl(OWLNL_BASE + "genitive");
        SINGULAR_FORMS_PROP = new PropertyImpl(OWLNL_BASE + "singularForms");
        PLURAL_FORMS_PROP = new PropertyImpl(OWLNL_BASE + "pluralForms");
        VAL_PROP = new PropertyImpl(OWLNL_BASE + "Val");
        OWLNL_PROPERTY_PROP = new PropertyImpl(OWLNL_BASE + "Property");
        OWLNL_PROPERTY_CLASS = microplanModel.getOntClass(OWLNL_BASE + "Property");
        ENGLISH_MICROPLANS_PROP = new PropertyImpl(OWLNL_BASE + "EnglishMicroplans");
        COMPARISONS_PROP = new PropertyImpl(OWLNL_BASE + "UsedForComparisons");
        USED_PROP = new PropertyImpl(OWLNL_BASE + "Used");
        AGGR_PROP = new PropertyImpl(OWLNL_BASE + "AggrAllowed");
        SLOTS_PROP = new PropertyImpl(OWLNL_BASE + "Slots");
        VERB_PROP = new PropertyImpl(OWLNL_BASE + "Verb");
        OWNER_PROP = new PropertyImpl(OWLNL_BASE + "Owner");
        PREP_PROP = new PropertyImpl(OWLNL_BASE + "Prep");
        FILLER_PROP = new PropertyImpl(OWLNL_BASE + "Filler");
        TEXT_PROP = new PropertyImpl(OWLNL_BASE + "Text");
        ADVERB_PROP = new PropertyImpl(OWLNL_BASE + "Adverb");
        TENSE_PROP = new PropertyImpl(OWLNL_BASE + "tense");
        VOICE_PROP = new PropertyImpl(OWLNL_BASE + "voice");
        REFEXP_TYPE_PROP= new PropertyImpl(OWLNL_BASE + "RETYPE");
        REFEXP_CASE_PROP= new PropertyImpl(OWLNL_BASE + "case");
        USER_TYPE_PROP = new PropertyImpl(OWLNL_BASE + "UserType");
        MAX_FACTS_SENTENCE_PROP = new PropertyImpl(OWLNL_BASE + "MaxFactsPerSentence");
        FACTS_PAGE_PROP = new PropertyImpl(OWLNL_BASE + "FactsPerPage");


        NP_CLASS = lexiconModel.getOntClass(OWLNL_BASE + "NP");
        ENGLISH_NP_CLASS = lexiconModel.getOntClass(OWLNL_BASE + SHORT_ENGLISH_NP_STRING);
        GREEK_NP_CLASS = lexiconModel.getOntClass(OWLNL_BASE + SHORT_GREEK_NP_STRING);
        ENGLISH_NP_STRING = ENGLISH_NP_CLASS.toString();
        GREEK_NP_STRING = GREEK_NP_CLASS.toString();
        CANNED_TEXT_CLASS = lexiconModel.getOntClass(OWLNL_BASE + SHORT_CANNED_TEXT_STRING);
        CANNED_TEXT_ONTOLOGY_CLASS = owlModel.getOntClass(ONTOLOGY_BASE + SHORT_CANNED_TEXT_STRING);
    }

}
