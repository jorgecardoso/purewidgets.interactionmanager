package org.instantplaces.im.server.referencecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.PMF;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class ReferenceCodeGenerator {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Element(dependent = "true")
	@Persistent
	private ArrayList<Integer> codes;
	
	private static ReferenceCodeGenerator gen = null;
	
	
	public ReferenceCodeGenerator() {
		
	}
	
    public String getNextCodeAsString() {
    	return String.format("%03d", this.getNextCode());
    }
    
    public int getNextCode() {
    	Integer i = codes.remove(0);
    	
    	//this.getCodes().remove(0);
    	//pm.deletePersistent(i);
    	return i.intValue();
    }
    
    public void recycleCode(String code) {
    	try {
    		Integer c = Integer.valueOf(code);
    		this.codes.add(c);
    	} catch (NumberFormatException e) {
    		Log.get().equals(e.getMessage());
    		e.printStackTrace();
    		
    	}
    }
    
    public static ReferenceCodeGenerator getFromDSO(PersistenceManager pm) {
    	try {
	    	Extent e = pm.getExtent(ReferenceCodeGenerator.class);
	    	
	    	Iterator iter=e.iterator();
	    	
	    	   
	        //List<Generator> results = (List<Generator>) query.execute();
	        
	        if ( iter.hasNext() ) {
	        	Log.get().debug("Retrieved generator from data store.");
	        	
	        	return (ReferenceCodeGenerator)iter.next();
	        	
	        }  else {
	        	Log.get().debug("Generator not found in data store, creating...");
	        	ReferenceCodeGenerator gen = new ReferenceCodeGenerator();
	        	ArrayList<Integer>codes = new ArrayList<Integer>(1000);
	        	for (int i = 0; i < 1000; i++) {
	        		codes.add(new Integer(i));
	        	}
	        	gen.setCodes(codes);
	        	
	        	pm.makePersistent(gen);
	        	//gen.getCodes().remove(0);
	        	return gen;
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  
	    return null;
    }
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