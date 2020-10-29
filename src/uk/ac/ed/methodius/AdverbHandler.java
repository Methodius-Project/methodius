package uk.ac.ed.methodius;

import java.util.HashMap;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.io.IOException;

public class AdverbHandler {

    private Log log = null;
        private String language;
    
    public AdverbHandler(Configuration config) throws JDOMException, IOException {
        log = config.getLog();
        language = config.getLanguage();
        String adverbFile = config.getAdverbsFileName();
        HashMap<String,Integer> adverbMap = readAdverbs(adverbFile);
        config.setAdverbMap(adverbMap);
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String,Integer> readAdverbs(String adverbFileName) throws JDOMException, IOException {
        log.output("Starting load of adverbs...");
        HashMap<String,Integer> thisMap = new HashMap<String,Integer>();
        SAXBuilder builder = new SAXBuilder();
        log.output("loading from file: [" + adverbFileName + "]");
        Document doc = null;
        doc = builder.build(getClass().getResourceAsStream(adverbFileName));
        Element root = doc.getRootElement();

        
        for (Iterator childIter = root.getChildren("adverbs").iterator(); childIter.hasNext();){
            Element child = (Element)childIter.next();
            if (child.getAttributeValue("lang").equalsIgnoreCase(language)) {
                for (Iterator adverbIter = child.getChildren().iterator(); adverbIter.hasNext();) {
                    Element adverb = (Element)adverbIter.next();
                    String text = adverb.getAttributeValue("text");
                    if (text != null && !text.equalsIgnoreCase("")) {
                        String position = adverb.getAttributeValue("position");
                        Integer pos = 5;
                        if (position != null && !position.equalsIgnoreCase("")) {
                            try {
                                pos = Integer.parseInt(position);
                            } catch (NumberFormatException e) {
                                log.output(position + "is not an integer");
                                pos = 5;
                            }
                            thisMap.put(text, pos);
                        }
                    }
                }
            }
        }
        return thisMap;
    }
}
