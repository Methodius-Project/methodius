package uk.ac.ed.methodius;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jdom.Document;

import uk.ac.ed.methodius.Exceptions.DataStoreException;
import uk.ac.ed.methodius.Exceptions.LoadException;
import uk.ac.ed.methodius.Exceptions.UnknownCommandException;

/**
 * The Command class provides a simple command interface to Methodius primarily
 * for testing. Commands available are:
 * <dl>
 * <dt>load</dt>
 * <dd>loads all the data needed by Methodius from various XML files.</dd>
 * <dt>createCP</dt>
 * <dd>having loaded data reform into CP entity-reln model</dd>
 * <dt>get objectid</dt>
 * <dd>retrieve a single object</dd>
 * <dt>getCP objectid depth</dt>
 * <dd>get CP around objectid to a depth</dd>
 * <dt>getCPFacts objectid depth</dt>
 * <dd>get the facts from the CP around objectid to a depth</dd>
 * <dt>getNFacts objectid depth nfacts</dt>
 * <dd>like getCPFacts but limit max number of facts to nfacts.</dd>
 * <dt>pub objectid num_facts</dt>
 * <dd>publish a description of the object saying num_facts things.</dd>
 * <dt>pubmany {objectid num_facts}*</dt>
 * <dd>publish descriptions of the objects saying num_facts things.</dd>
 * </dl>
 *
 */

public class Command {

    private static Configuration config = null;

    private static DataStoreRead dataStoreRead = null;

    private static Log log = null;

    private static boolean readOnly = false;

    private static boolean doComparisons = true;

    private static final String usage = "Usage: config-file command\n" + "where command is one of:\n" + "loadXML - loads all the data needed by Methodius from various XML files.\n"
            + "createCP - having loaded data reform into CP entity-reln model.\n" + "get objectid - retrieve a single object.\n" + "getCP objectid depth - get CP around objectid to a depth.\n"
            + "getCPFacts objectid depth - get the facts from the CP around objectid to a depth.\n" + "getNFacts objectid depth nfacts - like getCPFacts but limit max number of facts to nfacts.\n"
            + "pub objectid num_facts - publish a description of the object saying num_facts things.\n" + "pubmany {objectid num_facts}* - publish descriptions of the objects saying num_facts things.";

    /**
     * parses and executes the command args. TODO: better structure for adding
     * new commands. TODO: Exception from CP - needs rethinking.
     *
     * @param args
     *            string args to parse
     * @throws LoadException
     * @throws DataStoreException
     * @throws UnknownCommandException
     */

    private static void doCommand(String[] args) throws LoadException, DataStoreException, UnknownCommandException, Exception {
        if (args[1].equals("loadXML")) {
            DataStoreLoadXml dataStore = new DataStoreLoadXml(config, false);
            dataStore.init();
            dataStore.load();
        }
        else if (args[1].equals("loadRDF")) {
            DataStoreLoadRdf dataStore = new DataStoreLoadRdf(config, false);
            dataStore.init();
            dataStore.load();
        }
        else if (args[1].equals("get")) {
            Entity ent = dataStoreRead.getEntity(args[2]);
            System.out.println("Entity = " + ent);
        }
        else if (args[1].equals("createCP")) {
            DataStoreCP dataStore = new DataStoreCP(config, false);
            dataStore.init();
            dataStore.createCP();
        }
        else if (args[1].equals("getCP")) {
            int depth = Integer.parseInt(args[3]);
            config.setMaxCPDepth(depth);
            ContentPotential cp = new ContentPotential(dataStoreRead, args[2], config);
            System.out.println("CP for: " + args[2] + " of depth " + args[3] + " is ");
            CPPrinter cpOut = new CPPrinter(System.out);
            cpOut.print(cp);
            System.out.println("--------------------------");
        }
        else if (args[1].equals("getNFacts")) {
            int depth = Integer.parseInt(args[3]);
            config.setMaxCPDepth(depth);
            ContentPotential cp = new ContentPotential(dataStoreRead, args[2], config);
            System.out.println("CPFacts for: " + args[2] + " of depth " + args[3] + " is ");
            List facts = cp.getNFacts(Integer.parseInt(args[4]));
            Iterator iter = facts.iterator();
            while (iter.hasNext()) {
                Fact f = (Fact) iter.next();
                System.out.println(f.toString());
                System.out.println("---");
            }
            System.out.println("--------------------------");
        }
        else if (args[1].equals("listDB")) {
            dataStoreRead.listDB(args[2], System.out);
        }
        else if (args[1].equals("pub")) {
            // GenericFactHandler gfh = new
            // GenericFactHandler(dataStore,config);
            // config.setGenericFactHandler(gfh);
            String id = args[2];
            if (!dataStoreRead.isInDb(id)) {
                System.out.println("object " + id + " is not in the database.");
            }
            else {
                SignificanceHandler sh = new SignificanceHandler(dataStoreRead, config);
                config.setSignificanceHandler(sh);
                AdverbHandler ah = new AdverbHandler(config);
                config.setAdverbHandler(ah);
                TypeHandler th = new TypeHandler(dataStoreRead, config, log);
                config.setTypeHandler(th);
                log.output("Create user model");
                UserModel um = dataStoreRead.getUserModel("adult");
                config.setUserModel(um);
                um.setSearchWidth(config.getMaxCPDepth());
                int nFacts = Integer.parseInt(args[3]);
                um.setTargetSize(nFacts);
                MethodiusRealizer mr = new MethodiusRealizer(config);
                config.setMethodiusRealizer(mr);
                Publisher p = new Publisher(dataStoreRead, config);
                String[] sentences = p.describe(id);
                log.output("\nThe returned sentences are:\n");
                for (int i = 0; i < sentences.length && sentences[i] != null; i++) {
                    System.out.println(sentences[i]);
                }
                log.output("========= THE END =========");
            }
        }
        else if (args[1].equals("pubmany")) {
            // GenericFactHandler gfh = new
            // GenericFactHandler(dataStore,config);
            // config.setGenericFactHandler(gfh);
            SignificanceHandler sh = new SignificanceHandler(dataStoreRead, config);
            config.setSignificanceHandler(sh);
            AdverbHandler ah = new AdverbHandler(config);
            config.setAdverbHandler(ah);
            PredicateHandler ph = new PredicateHandler(dataStoreRead, config);
            config.setPredicateHandler(ph);
            TypeHandler th = new TypeHandler(dataStoreRead, config, log);
            config.setTypeHandler(th);
            log.output("Create user model");
            UserModel um = dataStoreRead.getUserModel("adult");
            config.setUserModel(um);
            um.setSearchWidth(config.getMaxCPDepth());
            MethodiusRealizer mr = new MethodiusRealizer(config);
            config.setMethodiusRealizer(mr);
            Publisher p = new Publisher(dataStoreRead, config);
            for (int j = 2; j < args.length; j++) {
                String id = args[j];
                if (!dataStoreRead.isInDb(id)) {
                    System.out.println("object " + id + " is not in the database.");
                }
                else {
                    int nFacts = 0;
                    try {
                        nFacts = Integer.parseInt(args[j + 1]);
                    }
                    catch (RuntimeException e) {
                        // TODO Auto-generated catch block
                        System.out.println("remember to put a number of facts after each object");
                        System.exit(1);
                        // e.printStackTrace();
                    }
                    log.output("setting target size to " + nFacts);
                    um.setTargetSize(nFacts);
                    String[] sentences = p.describe(id);
                    log.output("\nThe returned sentences are:\n");
                    for (int i = 0; i < sentences.length && sentences[i] != null; i++) {
                        System.out.println(sentences[i]);
                    }
                    System.out.println("-");
                }
                j++;
            }
            log.output("========= THE END =========");
        }
        else if (args[1].equals("pubcorpus")) {
            for (int z = 1; z < 501; z++) {
                Writer writer = null;

                try {
                    String fileName = "exhibit-chain" + z + ".xml";
                    writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream("/afs/inf.ed.ac.uk/group/project/triangle/" + fileName), "utf-8"));
                    SignificanceHandler sh = new SignificanceHandler(dataStoreRead, config);
                    config.setSignificanceHandler(sh);
                    AdverbHandler ah = new AdverbHandler(config);
                    config.setAdverbHandler(ah);
                    PredicateHandler ph = new PredicateHandler(dataStoreRead, config);
                    config.setPredicateHandler(ph);
                    TypeHandler th = new TypeHandler(dataStoreRead, config, log);
                    config.setTypeHandler(th);
                    //               GenericFactHandler gfh = new GenericFactHandler(dataStoreRead, config);
                    //               config.setGenericFactHandler(gfh);
                    log.output("Create user model");
                    UserModel um = dataStoreRead.getUserModel("adult");
                    config.setUserModel(um);
                    um.setSearchWidth(config.getMaxCPDepth());
                    MethodiusRealizer mr = new MethodiusRealizer(config);
                    config.setMethodiusRealizer(mr);
                    Publisher p = new Publisher(dataStoreRead, config);
                    CorpusOutput corpus = new CorpusOutput();
                    int nFacts = 6;
                    Random random = new Random();
                    ArrayList<String> exhibitList = new ArrayList<String>();
                    for (int ex = 0; ex < 10; ex++) {
                        int exNum = random.nextInt(50) + 1;

                        String id = "exhibit" + exNum;
                        System.out.println(id);
                        exhibitList.add(id);
                        if (!dataStoreRead.isInDb(id)) {
                            log.output("object " + id + " is not in the database, adding another object");
                            ex--;
                        }
                        else {
                            um.setTargetSize(nFacts);
                            boolean textCreated = p.addExhibitToCorpus(id, corpus);
                            if (!textCreated) {
                                System.out.println("problem with generation for " + id + " , adding another object");
                                ex--;
                            }
                        }
                    }
                    log.output(exhibitList.toString());

                    writer.write(corpus.getDocString());
                } catch (IOException ex) {
                    log.output(ex.toString());
              } finally {
                  try {writer.close();} catch (Exception ex) {/*ignore*/}
              }
            }
        }
        else if (args[1].equals("generics")) {
            Map generics = dataStoreRead.getGenerics();
            System.out.println("generics=\n" + generics);
        }
        else if (args[1].equals("loop")) {
            if (args.length != 8) {
                System.out.println("loop minNObs maxNObs dObs minFacts maxFacts dFacts");
                return;
            }
            long time = 0;
            int minObjs = Integer.parseInt(args[2]);
            int maxObjs = Integer.parseInt(args[3]);
            int dObjs = Integer.parseInt(args[4]);
            int minFacts = Integer.parseInt(args[5]);
            int maxFacts = Integer.parseInt(args[6]);
            int dFacts = Integer.parseInt(args[7]);
            System.out.println("loop " + minObjs + " " + maxObjs + " " + dObjs + " " + minFacts + " " + maxFacts + " " + dFacts);
            log.setNoOutput();
            for (int nFacts = minFacts; nFacts <= maxFacts; nFacts = nFacts + dFacts) {
                for (int nObjs = minObjs; nObjs <= maxObjs; nObjs = nObjs + dObjs) {
                    GenericFactHandler gfh = new GenericFactHandler(dataStoreRead, config);
                    config.setGenericFactHandler(gfh);
                    SignificanceHandler sh = new SignificanceHandler(dataStoreRead, config);
                    config.setSignificanceHandler(sh);
                    AdverbHandler ah = new AdverbHandler(config);
                    config.setAdverbHandler(ah);
                    UserModel um = dataStoreRead.getUserModel("adult");
                    config.setUserModel(um);
                    um.setSearchWidth(config.getMaxCPDepth());
                    MethodiusRealizer mr = new MethodiusRealizer(config);
                    config.setMethodiusRealizer(mr);
                    Publisher p = new Publisher(dataStoreRead, config);
                    um.setTargetSize(nFacts);
                    int n = 1;

                    for (int j = 0; j < nObjs; j++) {
                        String exhibitName = "exhibit" + n;
                        log.output("\n================= " + exhibitName + "===================");
                        n++;
                        if (n > 50) {
                            n = 1;
                        }
                        long startT = System.currentTimeMillis();
                        String[] sentences = p.describe(exhibitName);
                        for (int i = 0; i < sentences.length && sentences[i] != null; i++) {
                            log.output(sentences[i]);
                        }

                        long endT = System.currentTimeMillis();
                        long diff = endT - startT;
                        time = time + diff;
                    }
                    System.out.println("Total = " + time + " ms for " + nObjs + " objects and " + nFacts + " facts per object");
                }
            }
        }
        else {
            throw new UnknownCommandException(args[1]);
        }

    }

    /**
     * parses options out of command args. Current options are: -v verbose -q
     * quiet
     *
     * @param args
     * @return args array with options removed
     */

    private static String[] options(String[] args) {
        int argi = 0;
        String[] newArgs;
        boolean moreOpts = true;

        while (argi < args.length && moreOpts) {
            if (args[argi].startsWith("-")) {
                if (args[argi].equals("-q")) {
                    log.setNoOutput();
                }
                else if (args[argi].equals("-r")) {
                    readOnly = true;
                }
                else if (args[argi].equals("-nocomp")) {
                    doComparisons = false;
                }
                else {
                    System.out.println("Unknown option: " + args[argi]);
                }
                argi++;
            }
            else {
                moreOpts = false;
            }
        }

        newArgs = new String[args.length - argi];
        for (int i = argi; i < args.length; i++) {
            newArgs[i - argi] = args[i];
        }
        return newArgs;
    }

    /**
     * args are:
     * <p>
     * [options] configfile command
     * <p>
     * options are:
     * <p>
     * -q : quiet -r : read only
     * 
     * @param args
     */
    public static void main(String[] args) {

        if (args.length <= 0) {
            System.out.println(usage);
            return;
        }

        log = new Log(System.out);
        Util.setLog(log);
        args = options(args); // strips optional args from the front

        try {
            log.output("Using configuration: " + args[0]);
            config = new Configuration(args[0], doComparisons);
            config.setLog(log);
            log.output("read only is " + readOnly);
            dataStoreRead = new DataStoreRead(config, readOnly);
            dataStoreRead.init();

            doCommand(args);
        }
        catch (Exception e) {
            System.out.println("Exception thrown\n" + e);
            e.printStackTrace(System.out);
        }
        finally {
            if (dataStoreRead != null) {
                dataStoreRead.close();
            }
        }
    }

}
