package uk.ac.ed.methodius;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
@SuppressWarnings("unchecked")
public class Util {

    private static Log log = null;
    private static int id = 0;

    public static void setLog(Log l) {
        log = l;
    }

    public static Log getLog() {
        return log;
    }

    public static int getNextId() {
        id++;
        return id;
    }


    public static String XMLtoString(Element root) throws IOException {
        StringWriter sw = new StringWriter();
        Util.write(root, "", sw);
        return sw.toString();
    }

    /**
     * Output the specified DOM Node object, printing it using the specified
     * indentation string
     **/
    public static void write(Element elt, String indent, PrintStream out) {
        out.print(indent + "<" + elt.getName());   // Begin start tag
        List attrs = elt.getAttributes();     // Get attributes
        Iterator iter = attrs.iterator();
        while(iter.hasNext()) {
            Attribute a = (Attribute)iter.next();
            out.print(" " + a.getName() + "='" +  // Print attr. name
                    fixup(a.getValue()) + "'"); // Print attr. value
        }
        out.println(">");                             // Finish start tag

        String newindent = indent + "    ";           // Increase indent
        List children = elt.getChildren();             // Get child
        iter = children.iterator();
        while(iter.hasNext()) {                        // Loop 
            Element child = (Element)iter.next();
            write(child, newindent, out);                  // Output child
        }

        out.println(indent + "</" +                   // Output end tag
                elt.getName() + ">");
    }

    public static void write(Element elt, String indent) {
        log.print(true, indent + "<" + elt.getName());   // Begin start tag
        List attrs = elt.getAttributes();     // Get attributes
        Iterator iter = attrs.iterator();
        while(iter.hasNext()) {
            Attribute a = (Attribute)iter.next();
            log.print(" " + a.getName() + "='" +  // Print attr. name
                    fixup(a.getValue()) + "'"); // Print attr. value
        }
        log.println(">");                             // Finish start tag

        String newindent = indent + "    ";           // Increase indent
        List children = elt.getChildren();             // Get child
        iter = children.iterator();
        while(iter.hasNext()) {                        // Loop 
            Element child = (Element)iter.next();
            write(child, newindent);                  // Output child
        }

        log.println(indent + "</" +                   // Output end tag
                elt.getName() + ">");
    }

    public static void write(Element elt, String indent, Writer out) throws IOException {
        out.write(indent + "<" + elt.getName());   // Begin start tag
        List attrs = elt.getAttributes();     // Get attributes
        Iterator iter = attrs.iterator();
        while(iter.hasNext()) {
            Attribute a = (Attribute)iter.next();
            out.write(" " + a.getName() + "='" +  // Print attr. name
                    fixup(a.getValue()) + "'"); // Print attr. value
        }
        out.write(">\n");                             // Finish start tag

        String newindent = indent + "    ";           // Increase indent
        List children = elt.getChildren();             // Get child
        iter = children.iterator();
        while(iter.hasNext()) {                        // Loop 
            Element child = (Element)iter.next();
            write(child, newindent, out);                  // Output child
        }

        out.write(indent + "</" +                   // Output end tag
                elt.getName() + ">\n");
    }


    public static void write(Document doc, String indent, PrintStream out) {
        Element elt = doc.getRootElement();
        out.print(indent + "<" + elt.getName());   // Begin start tag
        List attrs = elt.getAttributes();     // Get attributes
        Iterator iter = attrs.iterator();
        while(iter.hasNext()) {
            Attribute a = (Attribute)iter.next();
            out.print(" " + a.getName() + "='" +  // Print attr. name
                    fixup(a.getValue()) + "'"); // Print attr. value
        }
        out.println(">");                             // Finish start tag

        String newindent = indent + "    ";           // Increase indent
        List children = elt.getChildren();             // Get child
        iter = children.iterator();
        while(iter.hasNext()) {                        // Loop 
            Element child = (Element)iter.next();
            write(child, newindent, out);                  // Output child
        }

        out.println(indent + "</" +                   // Output end tag
                elt.getName() + ">");
    }

    public static void write(Document doc, String indent) {
        Element elt = doc.getRootElement();
        log.print(true, indent + "<" + elt.getName());   // Begin start tag
        List attrs = elt.getAttributes();     // Get attributes
        Iterator iter = attrs.iterator();
        while(iter.hasNext()) {
            Attribute a = (Attribute)iter.next();
            log.print(" " + a.getName() + "='" +  // Print attr. name
                    fixup(a.getValue()) + "'"); // Print attr. value
        }
        log.println(">");                             // Finish start tag

        String newindent = indent + "    ";           // Increase indent
        List children = elt.getChildren();             // Get child
        iter = children.iterator();
        while(iter.hasNext()) {                        // Loop 
            Element child = (Element)iter.next();
            write(child, newindent);                  // Output child
        }

        log.println(indent + "</" +                   // Output end tag
                elt.getName() + ">");
    }

    // This method replaces reserved characters with entities.
    private static String fixup(String s) {
        StringBuffer sb = new StringBuffer();
        int len = s.length();
        for(int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch(c) {
            default: sb.append(c); break;
            case '<': sb.append("&lt;"); break;
            case '>': sb.append("&gt;"); break;
            case '&': sb.append("&amp;"); break;
            case '"': sb.append("&quot;"); break;
            case '\'': sb.append("&apos;"); break;
            }
        }
        return sb.toString();
    }



    public static String fileToString(String fname) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fname));
        String line = br.readLine();
        String res = null;
        while( line != null ) {
            if(res == null) {
                res = line;
            } else {
                res = res + "\n" + line;
            }
            line = br.readLine();
        }
        br.close();
        return res;
    }

    public static String listOfStringsToString(List l) {
        String ret = "{";
        Iterator iter = l.iterator();
        while(iter.hasNext()) {
            String nxt = (String)iter.next();
            ret = ret + nxt;
            if(iter.hasNext()) {
                ret = ret + ",";
            }
        }
        ret = ret + "}";
        return ret;
    }

    public static String listOfTypesToString(List l) {
        String ret = "{";
        Iterator iter = l.iterator();
        while(iter.hasNext()) {
            Type nxt = (Type)iter.next();
            ret = ret + nxt.getName();
            if(iter.hasNext()) {
                ret = ret + ",";
            }
        }
        ret = ret + "}";
        return ret;
    }

    public static Map stringToAttrMap(String s) {
        System.out.println("stringToAttrMap: " + s);
        Map m = new HashMap();
        String stripped = s.trim();
        String[] fields = stripped.split("[ =]");
        for(int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].replaceAll("\"", "");
            if(fields[i].endsWith("/>")){
                int l = fields[i].length();
                fields[i] = fields[i].substring(0,l-2);
            }			
            if(fields[i].endsWith(">")){
                int l = fields[i].length();
                fields[i] = fields[i].substring(0,l-1);
            }
        }		
        for(int i = 1; i < fields.length; i = i+2) {
            System.out.println(fields[i] + "=" + fields[i+1]);
            m.put(fields[i],fields[i+1]);
        }
        return m;
    }

}
