package uk.ac.ed.methodius;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.nio.file.Paths;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import uk.ac.ed.methodius.Exceptions.ConfigurationException;

/**
 * holds the configuration information for Methodius.
 * Mostly this will come from the config file but some may be set
 * dynamically. Gets passed to everything.
 *
 */

public class Configuration {
    
    private String configType = null;
    
    private String dataDir = "/data";
	/* Root dir for relative pathnames, defaults to execution dir */
	private String rootDir = null;
	
	/* Root dir for openccg resources e.g. xsl files*/
	private String openCCGRoot = null;
	private String openCCGBase = null;
	private String openCCGDomain = null;
	
	private HashMap<String,String> fileNames = null;
	
	private String databaseDir = null;
	private String databaseName = null;
	private String defDatabaseName = null;
	private String classDatabaseName = null;
	
	private String imagesdir = "pictures";
	
	/* handles to various objects used everywhere */
	private TypeHandler typeHandler = null;
	private UserModel userModel = null;
	private Log log = null;
	private MethodiusRealizer mr = null;
	private SignificanceHandler significanceHandler = null;
	private GenericFactHandler genericFactHandler = null;
	private AdverbHandler adverbHandler = null;
	private PredicateHandler predicateHandler = null;
	private HashSet<String> compPreds = null;
	private HashMap<String,Integer> adverbMap = null;
	
	/* the maximum depth of the CP web extracted round a focal object */
	private int maxCPDepth = 5;
	
	private String logFileName = null; //TODO: use this guy!

	private String filesep = "/";
    
    private boolean doComparisons = true;
    private boolean createTypeFact = false;
    
    private String language = "english";
	
	/* what we say when we've got nothing left to say */
	private String defaultSentence = "Nothing more";
	
	
	/* getting files and directories common to both loading types (classic xml or owl/rdf) */
	
	public String getOpenCCGRoot() {
		return openCCGRoot;
	}
	
	public String getOpenCCGBase() {
        return openCCGBase;
    }

    public String getOpenCCGDomain() {
        return openCCGDomain;
    }

    public void setOpenCCGDomain(String openCCGDomain) {
        this.openCCGDomain = openCCGDomain;
    }

    public void setOpenCCGBase(String openCCGBase) {
        this.openCCGBase = openCCGBase;
    }

    public String getDatabaseDir() {
		return databaseDir;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public String getDefinitionDatabaseName() {
		return defDatabaseName;
	}
	
	public String getClassDatabaseName() {
		return classDatabaseName;
	}
	
	public String getAdverbsFileName() {
	    return fileNames.get("adverbs");
	}
	
	/* getting files if loading from "classic" methodius xml files */
	
	public String getObjectsFileName() {
	    return fileNames.get("instances");
	}
	
	public String getTypesFileName() {
	    return fileNames.get("types");
	}
	
	public String getUserTypesFileName() {
	    return fileNames.get("usertypes");
	}
	
	public String getSignificancesFileName() {
	    return fileNames.get("significance");
	}
	

	
	/* methods for getting files if loading from owl/rdf files */
	
	public String getOwlFileName() {
	    return fileNames.get("owl");
	}
	
	public String getLexiconFileName() {
	    return fileNames.get("lexicon");
	}
	
	public String getMicroplansFileName() {
	    return fileNames.get("microplans");
	}
	
	public String getUserModellingFileName() {
	    return fileNames.get("usermodel");
	}
	
	/* retrieving various handlers */
	
	public SignificanceHandler getSignificanceHandler() {
		return significanceHandler;
	}
	
	public void setSignificanceHandler(SignificanceHandler sh) {
		significanceHandler = sh;
	}
	
	public PredicateHandler getPredicateHandler() {
        return predicateHandler;
    }


    public void setPredicateHandler(PredicateHandler predicateHandler) {
        this.predicateHandler = predicateHandler;
    }


    public GenericFactHandler getGenericFactHandler() {
		return genericFactHandler;
	}
	
	public void setGenericFactHandler(GenericFactHandler gfh) {
		genericFactHandler = gfh;
	}
	
	public AdverbHandler getAdverbHandler() {
        return adverbHandler;
    }


    public void setAdverbHandler(AdverbHandler adverbHandler) {
        this.adverbHandler = adverbHandler;
    }


    public HashMap<String, Integer> getAdverbMap() {
        return adverbMap;
    }


    public void setAdverbMap(HashMap<String, Integer> adverbMap) {
        this.adverbMap = adverbMap;
    }


    public String getImagesDir() {
		return imagesdir;
	}
	
	public void setImagesDir(String dir) {
		imagesdir = dir;
	}
	
	public int getMaxCPDepth() {
		return maxCPDepth;
	}
	
	public void setMaxCPDepth(int i) {
		maxCPDepth = i;
	}
	
	public String getPredicatesFileName() {
	    return fileNames.get("predicates");
	}
	
	public void setTypeHandler(TypeHandler th) {
		typeHandler = th;
	}
	
	public TypeHandler getTypeHandler() {
		return typeHandler;
	}
	
	public Log getLog() {
		return log;
	}
	
	public void setLog(Log l) {
		log = l;
	}
	
	public void setUserModel(UserModel um) {
		userModel = um;
	}
	
	public UserModel getUserModel() {
		return userModel;
	}
	
	public String getDefaultSentence() {
		return defaultSentence;
	}
	
	public MethodiusRealizer getMethodiusRealizer() {
		return mr;
	}
	
	public void setMethodiusRealizer(MethodiusRealizer m) {
		mr = m;
	}
	
    public boolean getComparisons() {
        return doComparisons;
    }


    public void setComparisons(boolean doComparisons) {
        this.doComparisons = doComparisons;
    }

	public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }

    public void addCompPred(String id) {
        compPreds.add(id);
    }

    public Set<String> getCompPreds() {
        return compPreds;
    }
    
    public String getConfigType() {
        return configType;
    }
    
    public boolean getCreateTypeFact() {
        return createTypeFact;
    }

    public void setCreateTypeFact(boolean createTypeFact) {
        this.createTypeFact = createTypeFact;
    }

    /**
	 * reads and parses the configuration file and sets up the config globals.
	 * 
	 * @param fileName name of the configuration file
	 * @throws IOException if it has trouble reading the file
	 * @throws ConfigurationException if it gets a setting it doesn't like.
	 */
	
    public Configuration(String fileName) throws IOException, ConfigurationException, JDOMException {
        this(fileName, true);
    }
    
    
    
	@SuppressWarnings("unchecked")
    public Configuration(String fileName, boolean comps) throws IOException, ConfigurationException, JDOMException{
        doComparisons = comps;
	    databaseDir = "databases";
	    databaseName = "methodiusDB";
	    classDatabaseName = "methodiusClassDB";
	    defDatabaseName = "methodiusDefinitionDB";
	    logFileName = "methodius.log";
	    fileNames = new HashMap<String,String>();
	    SAXBuilder builder = new SAXBuilder();
	    rootDir = System.getenv("METH_HOME");
	    if (rootDir == null) {
//		    String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
//		    System.out.println("Methodius home $METH_HOME not set, defaulting to working directory:");
//		    System.out.println(currentDir);
	    		rootDir = "";
	    }
	    
	    String filePath = rootDir + dataDir + "/" + fileName;
	    String jarFilePath = dataDir + "/" + fileName;
	    try {
	    		// load file from data dir in jar file - need to rebuild with ant to get
	    		// new version of file in there
	        Document configDoc = builder.build(getClass().getResourceAsStream(jarFilePath));   
	        extract(configDoc);
	    }
	    catch (Exception e) {
	        if (fileName == null || fileName.equals("load") || fileName.equals("createCP")) {
	            System.out.println("remember to specify the config file e.g. agora-config.xml");
	        }
	        else {
	            System.out.println("could not find config file " + filePath);
	            System.out.println(e);
	        }
	        System.exit(1);
	        
	    }

	    compPreds = new HashSet();
	}
	
    
    private void extract(Document configDoc) throws DataConversionException {
        Element configEl = configDoc.getRootElement();
        configType = configEl.getAttributeValue("type");
        Element domainEl = configEl.getChild("domain");
        if (configType == null) {
            configType = "classic";
        }
        else if (configType.equalsIgnoreCase("owl")) {
            dataDir = domainEl.getAttributeValue("dir");
        }
       
        Element openccgEl = configEl.getChild("openccg");
        String root = openccgEl.getAttributeValue("root");
        openCCGRoot = root;
        openCCGBase = root + "/" + "methodius-base";
        openCCGDomain = root + "/" + openccgEl.getAttributeValue("dir");
        
        for (Iterator domainFileIter = domainEl.getAttributes().iterator(); domainFileIter.hasNext();) {
            Attribute domainFile = (Attribute)domainFileIter.next();
            String file = domainFile.getName();
            String fileName = "";
            // AI 08/04/09 hack for adverbs in owl case.
            if (file.equalsIgnoreCase("adverbs")) {
                fileName = "/data/" + domainFile.getValue();
            }
            else {
                fileName = dataDir + "/" + domainFile.getValue();
            }
            fileNames.put(file,fileName);
        }
        
        Element dbEl = configEl.getChild("database");
        for (Iterator dbFileIter = dbEl.getAttributes().iterator(); dbFileIter.hasNext();) {
            Attribute dbInfo = (Attribute)dbFileIter.next();
            String info = dbInfo.getName();
            if (info.equals("dir")) {
                databaseDir = dbInfo.getValue();
            }
            else if (info.equals("db")) {
                databaseName = dbInfo.getValue();
            }
            else if (info.equals("class")) {
                classDatabaseName = dbInfo.getValue();
            }
            else if (info.equals("defs")) {
                defDatabaseName = dbInfo.getValue();
            }
        }
        
        Element textsEl = configEl.getChild("texts");
        for (Iterator textsIter = textsEl.getAttributes().iterator(); textsIter.hasNext();) {
            Attribute textInfo = (Attribute)textsIter.next();
            String info = textInfo.getName();
            if (info.equals("maxcpdepth")) {
                maxCPDepth = textInfo.getIntValue();
            }
            else if (info.equals("default")) {
                defaultSentence = textInfo.getValue();
            }
            else if (info.equals("useTypeFact")) {
                createTypeFact = new Boolean(textInfo.getValue());
            }
        }
        
        logFileName = configEl.getChild("log").getAttributeValue("file");
        imagesdir = configEl.getChild("images").getAttributeValue("dir");
        Element languageEl = configEl.getChild("language");
        if (languageEl != null) {
            language = configEl.getChild("language").getAttributeValue("name");
        }
    }

	/**
	 * trims whitespace and changes from a relative name to an absolute
	 * 
	 * @param fname the filename
	 * @return canonical version
	 */
	
	private String fixUp(String fname) {
		fname = fname.trim();
		File f = new File(fname);
		if( !f.isAbsolute()) {
			fname = rootDir + filesep + fname;
		}
		return fname;
	}
}

