package uk.ac.ed.methodius;

import java.util.HashMap;

public class Expression {

    //	The different parts of the expression, some with default values.

    private HashMap<String,String> fields;
    
    private String expressionId = null;

    public Expression(String id) {
        expressionId = id;
        fields = new HashMap<String,String>();
        fields.put("mood", "dcl");
        fields.put("reversible", "false");
        fields.put("aggregationAllowedBefore", "false");
        fields.put("aggregationAllowedAfter", "false");
        fields.put("arg1RefExp", "default");
        fields.put("arg2RefExp", "default");
    }

    public String getId() {
        return expressionId;
    }

    public void setAnything(String field, String value) {
        fields.put(field, value);
    }
    
    public String getAnything(String field) {
        return fields.get(field);
    }
    
    public String toString() {
        String me = fields.toString();
        return me;
    }

    


}
