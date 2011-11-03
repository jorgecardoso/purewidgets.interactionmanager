package org.instantplaces.im.server.referencecode;

import java.util.ArrayList;


import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;


import com.google.appengine.api.datastore.Key;


@PersistenceCapable(detachable="true")
public class ReferenceCodeGenerator {
	
	
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private ArrayList<Integer> codes;
	
	private static final int MAX_CODE = 500;
	

	
	public ReferenceCodeGenerator() {
    	codes = new ArrayList<Integer>(1000);
    	this.rebuild();
    	//Log.get().debug("ReferenceCodeManager: " + this.codes.toString());
	}
	
    public synchronized String getNextCodeAsString() {
    	// TODO: Check nulls!!!!
    	return String.format("%03d", this.getNextCode());
    }
    
    public int getNextCode() {
    	//Log.get().debug("ReferenceCodeManager codes: " + this.codes.toString());
    	Integer i = codes.remove(0);
    	//Log.get().debug("ReferenceCodeManager codes: " + this.codes.toString());
    	Log.get().debug("Next code: " + i.toString());
    	//this.getCodes().remove(0);
    	//pm.deletePersistent(i);
    	return i.intValue();
    }
    
    public void recycleCode(String code) {
    	try {
    		Integer c = Integer.valueOf(code);
    		if (!this.codes.contains(c) ) {
    			this.codes.add(c);
    		}
    		
    	} catch (NumberFormatException e) {
    		Log.get().error(e.getMessage());
    		e.printStackTrace();
    		
    	}
    }
    
    public void remove(String code) {
    	try {
    		Integer c = Integer.valueOf(code);
    		this.codes.remove(c);
    		
    	} catch (NumberFormatException e) {
    		Log.get().error(e.getMessage());
    		e.printStackTrace();
    		
    	}
    }
    
    public void rebuild() {
    	this.codes.clear();
    	for (int i = 0; i < MAX_CODE; i++) {
    		codes.add(new Integer(i));
    	}
    }
    /*
    public static ReferenceCodeGenerator getFromDSO(PersistenceManager pm) {
    	try {
	    	Extent e = pm.getExtent(ReferenceCodeGenerator.class);
	    	
	    	Iterator iter=e.iterator();
	    	
	    	   
	        //List<Generator> results = (List<Generator>) query.execute();
	        
	        if ( iter.hasNext() ) {
	        	Log.get().debug("Retrieved generator from data store.");
	        	
	        	return (ReferenceCodeGenerator)iter.next();
	        	
	        }  
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  
	    return null;
    }
    */
/*
	private static void loadGeneratorFromDatastore() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		//Query query = pm.newQuery(Generator.class);
	    //query.setFilter("id == idParam");
	    //query.declareParameters("String idParam");
		Log.get().debug("Retrieving generator from data store.");
	    try {
	    	Extent e = pm.getExtent(Generator.class);
	    	
	    	Iterator iter=e.iterator();
	    	
	    	   
	        //List<Generator> results = (List<Generator>) query.execute();
	        
	        if ( iter.hasNext() ) {
	        	Log.get().debug("Retrieved generator from data store.");
	        	
	        	gen = (Generator)iter.next();
	        	
	        } else {
	        	Log.get().debug("Generator not found in data store, creating...");
	        	gen = new Generator();
	        	ArrayList<Integer>codes = new ArrayList<Integer>(1000);
	        	for (int i = 0; i < 1000; i++) {
	        		codes.add(new Integer(i));
	        	}
	        	gen.setCodes(codes);
	        	pm.makePersistent(gen);
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	       // query.closeAll();
	    }
	}
	*/
    
	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}
	
	public void setCodes(ArrayList<Integer> codes) {
		this.codes = codes;
		
	}
	
	public ArrayList<Integer> getCodes() {
		return this.codes;
	}
}
