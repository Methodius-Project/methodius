package uk.ac.ed.methodius;

import java.util.HashSet;

public class OpenCCGWord {

    private String form;
    private HashSet<String> macros;
//    private int hashCode;
    
    public OpenCCGWord() {
        macros = new HashSet<String>();
//        hashCode = calculateCode();
    }
    
    public OpenCCGWord(String form) {
        macros = new HashSet<String>();
        this.form = form;
 //       hashCode = calculateCode();
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
 //       hashCode = calculateCode();
    }

    public HashSet<String> getMacros() {
        return macros;
    }
    
    public void setMacros(HashSet<String>macros) {
        this.macros = macros;
 //       hashCode = calculateCode();
    }

    public void addMacro(String macro) {
        macros.add(macro);
 //       hashCode = calculateCode();
    }

    /*
    public int hashCode() {
        return hashCode;
    }
    */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((form == null) ? 0 : form.hashCode());
        result = prime * result + ((macros == null) ? 0 : macros.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OpenCCGWord other = (OpenCCGWord) obj;
        if (form == null) {
            if (other.form != null)
                return false;
        }
        else if (!form.equals(other.form)) {
            return false;
        }
        if (macros == null) {
            if (other.macros != null) {
                return false;
            }
        }
        else if (!macros.equals(other.macros)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return hashCode() + " " + form + " macros(" + macros + ")";
    }
    
}
