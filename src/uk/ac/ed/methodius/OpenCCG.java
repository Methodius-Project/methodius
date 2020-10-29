package uk.ac.ed.methodius;
import uk.ac.ed.methodius.Configuration;
//import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class OpenCCG {

    static String VERB_POS ="V";
    static String COMMONNOUN_POS ="N";
    static String PROPERNOUN_POS ="NNP";
    static String PREPOSITION_POS ="Prep";
    static String ADVERB_POS ="Adverb";
    
    static String PLUR_NOUN_MACRO = "@pl";
    static String SING_NOUN_MACRO = "@sg";
    static String FIRST_VERB_MACRO = "1st-agr";
    static String SECOND_VERB_MACRO = "2nd-agr";
    static String THIRD_VERB_MACRO = "3rd-agr";
    static String PLUR_VERB_MACRO = "pl-agr";
    static String SING_VERB_MACRO = "sg-agr";
    static String PRESENT_MACRO = "pres";
    static String PAST_MACRO = "past";
    static String PARTICIPLE_MACRO = "ppart";
    
    private HashMap<OpenCCGEntry,String> properNouns;
    private HashMap<OpenCCGEntry,String> commonNouns;
    private HashMap<OpenCCGEntry,String> verbs;
    private HashSet<String> prepositions;
    private HashSet<String> adverbs;
    private LinkedHashMap<String,HashSet<String>> types;
    private Element typesEl;
    
//    private HashSet<OpenCCGEntry> dictEntries;
    private OpenCCGEntries dictEntries;

    private String openCCGRoot;
    private String openCCGBase;
    private String openCCGDomain;
    
    private Log log;

    public OpenCCG(Configuration config) {
        log = config.getLog();
        properNouns = new HashMap<OpenCCGEntry,String>();
        commonNouns = new HashMap<OpenCCGEntry,String>();
        verbs = new HashMap<OpenCCGEntry,String>();
        prepositions = new HashSet<String>();
        adverbs = new HashSet<String>();
        types = new LinkedHashMap<String,HashSet<String>>();
        
//        dictEntries = new HashSet<OpenCCGEntry>();
        dictEntries = new OpenCCGEntries();
        openCCGRoot = config.getOpenCCGRoot();
        openCCGBase = config.getOpenCCGBase();
        openCCGDomain = config.getOpenCCGDomain();
    }

    /*
    public Document addToBase(String baseFile, Document elsToAdd) {
        Element baseEl = null;
        
        SAXBuilder builder = new SAXBuilder();
        File baseDictFile = new File(openCCGBase + baseFile);
        Document baseDoc = new Document();
        try {
            baseDoc = builder.build(baseDictFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();            
        }
        if (baseDoc != null) {
            baseEl = baseDoc.getRootElement();
        }
        else {
            baseDoc = new Document();
            baseEl = new Element("base");
            baseDoc.setRootElement(baseEl);
        }
        Element topEl = (Element)elsToAdd.getRootElement().clone();
        
        ArrayList<Element> children = new ArrayList<Element>();
        for (Iterator<Element> childIter = topEl.getChildren().iterator(); childIter.hasNext();) {
            Element child = childIter.next();
            children.add((Element)child.clone());
        }
        
        int addPosition = 0;
        if (baseFile.equals("dict.xsl")) {
            Element dictEl = (Element)baseEl.getDescendants(new ElementFilter("dictionary")).next();
            List<Element> entries = dictEl.getChildren("entry");
            Element lastEntry = entries.get(entries.size() - 1);
            addPosition = dictEl.indexOf(lastEntry);
            dictEl.addContent(addPosition, children);
        }

        else if (baseFile.equals("types-extras.xml")) {
            baseEl.addContent(children);
        }
        return baseDoc;
    }
    */
    public void readDict() {
     // read in existing dictionary

        SAXBuilder builder = new SAXBuilder();
        openCCGBase = "/group/ltg/users/amyi/methodius/software/methodius/openccg/grammars/agora";
        File dictFile = new File(openCCGBase + "/dict.xml");
        Document dictDoc = new Document();
        try {
            dictDoc = builder.build(dictFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Element topEl = dictDoc.getRootElement();
        for (Iterator<Element> entryIter = topEl.getChildren("entry").iterator(); entryIter.hasNext();) {
            log.output("reading entry");
            Element entryEl = entryIter.next();

            HashSet<String> entryMacros = new HashSet<String>();
            String stringEntryMacros = entryEl.getAttributeValue("macros");
            if (stringEntryMacros != null) {
                String[] separateEntryMacros = stringEntryMacros.split(" ");
                for (String entryMacro : separateEntryMacros) {
                    entryMacros.add(entryMacro);
                }
            }
            log.output("entry macros are " + entryMacros);
            OpenCCGEntry openCCGEntry = new OpenCCGEntry();
//            output(openCCGEntry.toString());
            String pos = entryEl.getAttributeValue("pos");
            String stem = entryEl.getAttributeValue("stem");
            String entryClass = entryEl.getAttributeValue("class");
            String entryWord = entryEl.getAttributeValue("word");
            
            List<Element> wordElList = entryEl.getChildren("word");

            if (wordElList.isEmpty()) {
                log.output("no words in entry");
                OpenCCGWord openCCGWord = new OpenCCGWord();
                if (stem != null) {
                    openCCGWord.setForm(stem);
                }
                else if (entryWord != null) {
                    openCCGWord.setForm(entryWord);
                }
                openCCGWord.setMacros(entryMacros);
                openCCGEntry.addWord(openCCGWord);
                log.output(openCCGEntry.toString());
            }
            else {
                log.output("found words in entry");
                if (stem != null) {
                    openCCGEntry.setStem(stem);
                }
                for (Iterator<Element> wordIter = entryEl.getChildren("word").iterator(); wordIter.hasNext();) {
                    log.output("reading in word");
                    Element wordEl = wordIter.next();
                    String form = wordEl.getAttributeValue("form");
                    log.output("form " + form);
                    OpenCCGWord openCCGWord = new OpenCCGWord(form);
                    openCCGWord.setMacros((HashSet<String>) entryMacros.clone());
                    String wordMacros = wordEl.getAttributeValue("macros");
                    log.output("word macros " + wordMacros);
                    if (wordMacros != null) {
                        String[] separateWordMacros = wordMacros.split(" ");
                        for (String wordMacro : separateWordMacros) {
                            openCCGWord.addMacro(wordMacro);
                        }
                    }
                    openCCGEntry.addWord(openCCGWord);
                    log.output(openCCGEntry.toString());
                }
            }

            if (pos != null) {
                openCCGEntry.setPos(pos);
//                log.output(openCCGEntry.toString());
            }

            if (entryClass != null) {
                openCCGEntry.setOpenCCGClass(entryClass);
//                log.output(openCCGEntry.toString());
            }

            for (Iterator<Element> memberOfIter = entryEl.getChildren("member-of").iterator(); memberOfIter.hasNext();) {
                Element memberOfEl = memberOfIter.next();
                String family = memberOfEl.getAttributeValue("family");
                String pred = memberOfEl.getAttributeValue("pred");
                openCCGEntry.addMemberOf(family, pred);
//                log.output(openCCGEntry.toString());
            }
            log.output("ADD TO DICT " + openCCGEntry.toString());
            dictEntries.addEntry(openCCGEntry);
        }
    }
    
        
    public void readMorph() {
        // read in existing morphology
        HashMap<String, OpenCCGEntry> morphEntries = new HashMap<String, OpenCCGEntry>();
        SAXBuilder builder = new SAXBuilder();
        
        File morphFile = new File(openCCGBase + "morph.xml");
        Document morphDoc = new Document();
        try {
            morphDoc = builder.build(morphFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element topEl = morphDoc.getRootElement();
        for (Iterator<Element> entryIter = topEl.getChildren("entry").iterator(); entryIter.hasNext();) {
            Element entry = entryIter.next();
            OpenCCGEntry morphEntry = new OpenCCGEntry();
            String pos = entry.getAttributeValue("pos");
            String stem = entry.getAttributeValue("stem");
            String word = entry.getAttributeValue("word");
            String entryId = pos;
            if (stem != null) {
                entryId += stem;
            }
            else {
                entryId += word;
            }
            if (!morphEntries.keySet().contains(entryId)) {
                
            }
            morphEntry.setPos(pos);

            String macros = entry.getAttributeValue("macros");
            if (macros != null) {
                String[] macrosArray = macros.split(" ");
                for (int i = 0; i < macrosArray.length; i++) {
//                    morphEntry.addMacro(macrosArray[i]);
                }
            }

            if (pos.equals(PROPERNOUN_POS)) {

                morphEntry.setStem(stem);
                morphEntry.setOpenCCGClass(entry.getAttributeValue("class"));
                properNouns.put(morphEntry, stem);
            }
            else if (pos.equals(COMMONNOUN_POS)) {

                morphEntry.setStem(stem);
                morphEntry.setOpenCCGClass(entry.getAttributeValue("class"));
                commonNouns.put(morphEntry, stem);
            }
            else if (pos.equals(VERB_POS)) {
                morphEntry.setStem(stem);
                morphEntry.setOpenCCGClass(entry.getAttributeValue("class"));
                verbs.put(morphEntry, stem);
            }
            else if (pos.equals(PREPOSITION_POS)) {
                prepositions.add(entry.getAttributeValue("word"));
            }
            else if (pos.equals(ADVERB_POS)) {
                adverbs.add(entry.getAttributeValue("word"));
            }
        }
    }
    
    public void readTypes() {
        SAXBuilder builder = new SAXBuilder();
        
        File typesFile = new File(openCCGBase + "/core-en-types.xml");
        Document typesDoc = new Document();
        try {
            typesDoc = builder.build(typesFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element topEl = typesDoc.getRootElement();
        typesEl = (Element)topEl.clone();
        typesEl.removeContent();
        for (Iterator<Element> typeIter = topEl.getChildren().iterator(); typeIter.hasNext();) {
            Element typeEl = typeIter.next();
            String parentString = typeEl.getAttributeValue("parents");
            if (parentString == null || parentString.equals("")) {
                parentString = "NO-PARENT";
            }
            String[] parentTypesArray = parentString.split(" ");
            HashSet<String> parentTypesHash = new HashSet<String>();
            for (int i = 0; i < parentTypesArray.length; i++) {
                parentTypesHash.add(parentTypesArray[i]);
            }
            types.put(typeEl.getAttributeValue("name"), parentTypesHash);
        }
        System.out.println("types from old file " + types);
    }
    
    
    public Document processOldTypes() {
        System.out.println("types with new added " + types);
        Document typesDoc = new Document();
        typesDoc.setRootElement(typesEl);
        for (Iterator<String> typeIter = types.keySet().iterator(); typeIter.hasNext();) {
            String type = typeIter.next();
            HashSet<String> parents = types.get(type);
            String parentsString = "";
            boolean foundParent = false;
            boolean noParent = false;
            boolean person = false;
            if (type.contains("person") || type.contains("human")) {
                person = true;
            }
            for (Iterator<String> parentIter = parents.iterator(); parentIter.hasNext();) {
                String parent = parentIter.next();
                if (parent.equals("NO-PARENT")) {
                    noParent = true;
                    continue;
                }
                if (!foundParent) {
                    parentsString = parent;
                    if (person && parentsString.contains("basic")) {
                        parentsString += " animate-being";
                    }
                    else if (parentsString.contains("basic")) {
                        parentsString += " inanimate";
                    }
                }
                else {
                    if (person && parentsString.contains("basic") && !(parentsString.contains("animate-being"))) {
                        parentsString += " animate-being";
                    }
                    else if (parentsString.contains("basic") && !(parentsString.contains("inanimate"))) {
                        parentsString += " inanimate";
                    }
                    parentsString += " " + parent;
                }
                foundParent = true;
            }
            if (!foundParent && !noParent) {
                parentsString = "basic";
            }
            Element typeEl = new Element("type");
            typeEl.setAttribute("name", type);
            if (!parentsString.equals("")) {
                typeEl.setAttribute("parents", parentsString);
            }
            typesEl.addContent(typeEl);
        }
        return typesDoc;
    }
    
    public void runAnt() {
        File baseDir = new File(openCCGBase);
        File buildFile = new File(openCCGRoot + "/build.xml");
        
        
        Project p = new Project();
        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        p.setBaseDir(baseDir);
        p.setProperty("openccg.dir", openCCGRoot);
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        p.addBuildListener(consoleLogger);

        try {
            p.fireBuildStarted();
            p.init();

            System.out.println("base dir is " + p.getBaseDir());
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.executeTarget(p.getDefaultTarget());
            p.fireBuildFinished(null);
        } catch (BuildException e) {
            p.fireBuildFinished(e);
        }
    }
    
    public OutputStreamWriter getOutputStream(String file) {
        File newDictFile = new File(openCCGDomain + "/" + file);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        
        try {
            fos = new FileOutputStream(newDictFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            osw = new OutputStreamWriter(fos, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return osw;
    }
    
    
    public boolean verbStemExists(String form) {
        if (verbs.containsValue(form)) {
            return true;
        }
        return false;
    }
    
    public boolean verbExists(String form) {
        if (verbs.containsKey(form)) {
            return true;
        }
        return false;
    }
    
    public boolean commonNounStemExists(String form) {
        if (commonNouns.containsValue(form)) {
            return true;
        }
        return false;
    }
    
    public boolean commonNounExists(String form) {
        if (commonNouns.containsKey(form)) {
            return true;
        }
        return false;
    }
    
    public boolean properNounExists(String form) {
        if (properNouns.containsKey(form)) {
            return true;
        }
        return false;
    }
    
    public boolean properNounStemExists(String form) {
        if (properNouns.containsValue(form)) {
            return true;
        }
        return false;
    }
    
    public boolean prepExists(String form) {
        if (prepositions.contains(form)) {
            return true;
        }
        return false;
    }

    public boolean adverbExists(String form) {
        if (adverbs.contains(form)) {
            return true;
        }
        return false;
    }

    public void addVerb(OpenCCGEntry verb) {
        verbs.put(verb, verb.getStem());
    }
    
    public void addCommonNoun(OpenCCGEntry commonNoun) {
        commonNouns.put(commonNoun, commonNoun.getStem());
    }
    
    public void addProperNoun(OpenCCGEntry properNoun) {
        properNouns.put(properNoun, properNoun.getStem());
    }
    
    public void addPrep(String prep) {
        prepositions.add(prep);
    }

    public HashMap<OpenCCGEntry, String> getCommonNouns() {
        return commonNouns;
    }

    public HashSet<String> getPrepositions() {
        return prepositions;
    }

    public HashMap<OpenCCGEntry, String> getProperNouns() {
        return properNouns;
    }

    public void printProperNouns() {
        System.out.println("PROPER NOUNS");
        for (Iterator<OpenCCGEntry> wordIter = properNouns.keySet().iterator(); wordIter.hasNext();) {
            OpenCCGEntry word = wordIter.next();
            System.out.println(word);
        }
    }
    
    public void printCommonNouns() {
        System.out.println("COMMON NOUNS");
        for (Iterator<OpenCCGEntry> wordIter = commonNouns.keySet().iterator(); wordIter.hasNext();) {
            OpenCCGEntry word = wordIter.next();
            System.out.println(word);
        }
    }
    
    public void printVerbs() {
        System.out.println("VERBS");
        for (Iterator<OpenCCGEntry> wordIter = verbs.keySet().iterator(); wordIter.hasNext();) {
            OpenCCGEntry word = wordIter.next();
            System.out.println(word);
        }
    }
    
    public HashMap<OpenCCGEntry, String> getVerbs() {
        return verbs;
    }
    
    public void addType(String type, String parent) {
        HashSet<String> parents;
        if (!types.containsKey(type)) {
            parents = new HashSet<String>();
            parents.add(parent);
        }
        else {
            parents = types.get(type);
            parents.add(parent);
        }
        types.put(type, parents);
    }
    
 //   public HashSet<OpenCCGEntry>  getEntries() {
    public OpenCCGEntries getEntries() {
        return dictEntries;
    }
}


