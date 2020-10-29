package uk.ac.ed.methodius;

import java.util.HashMap;
import java.util.HashSet;

public class OpenCCGEntry {
    
    private String pos;
    private String stem;
    private HashSet<OpenCCGWord> words;
    private String openCCGClass;
    private HashMap<String,String> memberOf;
    private HashSet<String> macros;
   
    public OpenCCGEntry() {
        words = new HashSet<OpenCCGWord>();
        memberOf = new HashMap<String,String>();
        macros = new HashSet<String>();
    }

    public String getPos() {
        return pos;
    }
    
    public void setPos(String pos) {
        this.pos = pos;
    }

    public HashSet<OpenCCGWord> getWords() {
        return words;
    }
    
    public void addWord( OpenCCGWord word) {
        words.add(word);
    }
    
    public String getOpenCCGClass() {
        return openCCGClass;
    }

    public void setOpenCCGClass(String openCCGClass) {
        this.openCCGClass = openCCGClass;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public HashMap<String,String> getMemberOf() {
        return memberOf;
    }
    
    public void addMemberOf(String family, String pred) {
        memberOf.put(family, pred);
    }
    
    public HashSet<String> getMacros() {
        return macros;
    }

    public void addMacro(String macro) {
        macros.add(macro);
//        hashCode = calculateCode();
    }

 /*   
    public int hashCode() {
        return hashCode;
    }
*/
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((macros == null) ? 0 : macros.hashCode());
        result = prime * result
                + ((memberOf == null) ? 0 : memberOf.hashCode());
        result = prime * result
                + ((openCCGClass == null) ? 0 : openCCGClass.hashCode());
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        result = prime * result + ((stem == null) ? 0 : stem.hashCode());
        result = prime * result + ((words == null) ? 0 : words.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OpenCCGEntry other = (OpenCCGEntry) obj;
        if (macros == null) {
            if (other.macros != null)
                return false;
        } else if (!macros.equals(other.macros))
            return false;
        if (memberOf == null) {
            if (other.memberOf != null)
                return false;
        } else if (!memberOf.equals(other.memberOf))
            return false;
        if (openCCGClass == null) {
            if (other.openCCGClass != null)
                return false;
        } else if (!openCCGClass.equals(other.openCCGClass))
            return false;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        if (stem == null) {
            if (other.stem != null)
                return false;
        } else if (!stem.equals(other.stem))
            return false;
        if (words == null) {
            if (other.words != null)
                return false;
        } else if (!words.equals(other.words))
            return false;
        return true;
    }
    
    public String toString() {
        return "[HASH: " + hashCode() + " POS: " + pos + " STEM: " + stem + " MEMBER: " + memberOf + " CLASS: " + openCCGClass + " WORDS: " + words + "]";
    }
    
    

}
