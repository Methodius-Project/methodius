package uk.ac.ed.methodius;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import uk.ac.ed.methodius.Exceptions.DataStoreException;

/**
 * GenericFactHandler class handles generic facts for objects. For an object of type
 * T there may be a generic entity, usually called something like "generic-T". As the
 * data is loaded we gather the names of these generic entities and store them in a
 * Map. Each generic Entity only has one fact which is the generic fact. We only
 * want to return a generic fact once and so we destroy it on use.
 * This is used by the content potential. When returning facts it checks first for a
 * generic fact and adds that in.
 * 
 * @author Ray
 *
 */

@SuppressWarnings("unchecked")

public class GenericFactHandler {
	
	/* Map from type name -> generic entity name */
	private Map generics;
	
	private DataStoreRead ds;
	private TypeHandler typeHandler;
	private Log log;
//	private Type stringType;
 //   private UserModel um;
	
	public GenericFactHandler(DataStoreRead d, Configuration config) {
		try {
			log = config.getLog();
//            um = config.getUserModel();
			ds = d;
			generics = ds.getGenerics();
			log.output("got generic map");
			log.output(generics.toString());
			typeHandler = config.getTypeHandler();
//			stringType = typeHandler.getType("string");
		} catch(Exception e) {
			if(log != null){
				log.output("Exception creating GenericFactHandler: " + e);
			}
			generics = new HashMap(); 
		}
	}
	
	
	/**
	 * return a generic fact from the named type if there is one which hasn't
	 * already been expressed.
	 * @param type the name of the type to check for
	 * @return the generic Fact or null
	 */
	public Fact getGeneric(String type) {
		log.output("getGeneric for: " + type);
		Fact f = null;
		String entityName = (String)generics.get(type);
		if(entityName != null ) {
			Entity ent;
			try {
				ent = ds.getEntity(entityName);
				List facts = ent.getFactNames();
				
				/* TODO: not true, a generic entity can have more than one fact
                 * 
				 * a generic entity only has one fact and because we've just
				 * retrieved the entity, the fact will just be a name and not
				 * yet expanded so we need to go and get the fact
				 */
				String factName = (String)facts.get(0);
				f = ds.getFact(factName);
				f.setArgByName(ent.getId(),ent);
				
				String nxtName = f.getUnsetArgName();
				log.output("unset arg name = " + nxtName);
				if( nxtName != null ) {
					Entity nxtEntity = ds.getEntity(nxtName);
					if( nxtEntity == null ) {
						nxtEntity = new Entity(nxtName, nxtName, "string");
						f.setArgByName(nxtName,nxtEntity);
					} else {
						f = null;
					}
				}
			} catch (Exception e) {
				f = null;
			}
			
			/* delete it so we don't return it again */
//			generics.remove(type);
		}
		log.output("getGeneric");
		return f;
	}
}
