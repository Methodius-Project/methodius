package uk.ac.ed.methodius;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class OpenCCGEntries  {

    private HashMap<String, OpenCCGEntry> entriesMap;
    private HashMap<String, HashSet<OpenCCGWord>> formToWordsMap;
    private HashMap<OpenCCGWord, OpenCCGEntry> wordToEntryMap;

    public OpenCCGEntries() {
        entriesMap = new HashMap<String, OpenCCGEntry>();
        formToWordsMap = new HashMap<String, HashSet<OpenCCGWord>>();
        wordToEntryMap = new HashMap<OpenCCGWord, OpenCCGEntry>();
    }

    public void addEntry(OpenCCGEntry e) {
        entriesMap.put(e.getStem(), e);
        if (e.getPos().equals("V")) {
            for (OpenCCGWord w : e.getWords()) {
                String form = w.getForm();
                HashSet<OpenCCGWord> words = formToWordsMap.get(form);
                if (words == null) {
                    words = new HashSet<OpenCCGWord>();
                }
                words.add(w);
                formToWordsMap.put(w.getForm(), words);
                wordToEntryMap.put(w, e);
            }
        }
    }

    public OpenCCGEntry getEntry(String stem) {
        return entriesMap.get(stem);
    }

    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OpenCCGEntry)) {
            return false;
        }
        if (entriesMap.containsValue(obj)) {
            return true;
        }
        return false;
    }
    
    public Collection<OpenCCGEntry> values() {
        return entriesMap.values();
    }
    
    public boolean containsStem(String stem) {
        return entriesMap.containsKey(stem);
    }
    
    public OpenCCGEntry getStemEntry(String stem) {
        return entriesMap.get(stem);
    }
    
    public boolean verbFormExists(String form) {
        return formToWordsMap.containsKey(form);
    }
    
    public HashSet<OpenCCGWord> getWordsWithForm(String form) {
        return formToWordsMap.get(form);
    }
}
