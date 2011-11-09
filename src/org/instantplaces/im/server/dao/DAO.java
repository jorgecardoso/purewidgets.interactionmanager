/**
 * 
 */
package org.instantplaces.im.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.instantplaces.im.server.Log;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */

public class DAO extends DAOBase
{
    private static Objectify ofy;
    
    
    static {
        ObjectifyService.register(PlaceDAO.class);
        ObjectifyService.register(ReferenceCodeGeneratorDAO.class);
        ObjectifyService.register(ApplicationDAO.class);
        ObjectifyService.register(WidgetDAO.class);
        ObjectifyService.register(WidgetOptionDAO.class);
        ObjectifyService.register(WidgetInputDAO.class);
       
    }
    
    public static void beginTransaction() {
    	ofy = ObjectifyService.beginTransaction();  
    }
    
    public static boolean commitOrRollbackTransaction() {
    	boolean success = false;
    	try
    	{
            ofy.getTxn().commit();
            success = true;
        } catch (Exception e) {
        	Log.get().warn("Could not commit transaction: " + e.getMessage());
        }
        finally
        {
            if (ofy.getTxn().isActive()) {
            	try {
                ofy.getTxn().rollback();
            	} catch (Exception e) {
            		Log.get().warn("Problem rolling back:" + e.getMessage());
            	}
            }
        }
    	
    	return success;
    }
    
    public static void deleteWidget(String placeId, String applicationId, String widgetId) {
    	Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey, ApplicationDAO.class, applicationId);
    	Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey, WidgetDAO.class, widgetId);
    	
    	
    	
    	deleteWidgetOptions(placeId, applicationId, widgetId);
    	ofy.delete(widgetKey);
    	//TODO: and widget input
    }
    
    public static ApplicationDAO getApplication(String placeId, String applicationId) {
    	Key<PlaceDAO> parent = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	
    	Key<ApplicationDAO> key = new Key<ApplicationDAO>(parent, ApplicationDAO.class, applicationId);
		return ofy.find(key);
    }
    
    public static ArrayList<ApplicationDAO> getApplications (String placeId) {
    	Query<ApplicationDAO> q = ofy.query(ApplicationDAO.class).ancestor(new Key<PlaceDAO>(PlaceDAO.class, placeId) );
    	
    	
    	ArrayList<ApplicationDAO> toReturn = new ArrayList<ApplicationDAO>();
    	if ( null != q ) {
    		for ( ApplicationDAO app : q ) {
    			toReturn.add(app);
    		}
    	}
    	return toReturn;
    	
    }
    
    public static PlaceDAO getPlace(String placeId) {
    		return ofy.find(PlaceDAO.class, placeId);
    }
    
    public static List<Key<PlaceDAO>> getPlaceKeys() {
    	/*
    	 * We can't get all root entities inside a transaction, do don't use the static ofy
    	 */
    	Objectify ofy_ = ObjectifyService.begin();  
    	Query<PlaceDAO> q = ofy_.query(PlaceDAO.class);
    	
    	return q.listKeys();
    	
    }
    
    public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(String placeId) {
    	Query<ReferenceCodeGeneratorDAO> q = ofy.query(ReferenceCodeGeneratorDAO.class).ancestor(new Key<PlaceDAO>(PlaceDAO.class, placeId) );
    	
    	return q.get();
    }
    
    public static WidgetDAO getWidget(String placeId, String applicationId, String widgetId) {
    	Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey, ApplicationDAO.class, applicationId);
    	Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey, WidgetDAO.class, widgetId);
    	
		return ofy.find(widgetKey);
    }
    
    public static ArrayList<WidgetOptionDAO> getWidgetOption(String placeId) {
    	Query<WidgetOptionDAO> q = ofy.query(WidgetOptionDAO.class).ancestor(new Key<PlaceDAO>(PlaceDAO.class, placeId) );
    	
    	
    	ArrayList<WidgetOptionDAO> toReturn = new ArrayList<WidgetOptionDAO>();
    	if ( null != q ) {
    		for ( WidgetOptionDAO option : q ) {
    			toReturn.add(option);
    		}
    	}
    	return toReturn;
    }
    
    
    public static List<Key<WidgetOptionDAO>>  getWidgetOptionKeys(String placeId, String applicationId, String widgetId) {
    	Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey, ApplicationDAO.class, applicationId);
    	Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey, WidgetDAO.class, widgetId);

    	Query<WidgetOptionDAO> q = ofy.query(WidgetOptionDAO.class).ancestor(widgetKey);
    	
    	return q.listKeys();
    	
    }
    
    

    
    public static WidgetInputDAO  getLastWidgetInput(String placeId, String applicationId, String widgetId) {
    	Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey, ApplicationDAO.class, applicationId);
    	Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey, WidgetDAO.class, widgetId);
    	
    	Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class).ancestor(widgetKey).order("-timeStamp");
    	
    	return q.get();
    	
    }
    
    public static ArrayList<WidgetInputDAO>  getWidgetInput(String placeId, String applicationId, long from) {
    	Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey, ApplicationDAO.class, applicationId);
    	
    	Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class).ancestor(applicationKey).filter("timeStamp > ", from);
    	
    	ArrayList<WidgetInputDAO> toReturn = new ArrayList<WidgetInputDAO>();
    	if ( null != q ) {
    		for ( WidgetInputDAO input : q ) {
    			toReturn.add(input);
    		}
    	}
    	return toReturn;
    	
    }
    
    
    
    public static ArrayList<WidgetDAO> getWidgets(String placeId, String applicationId) {
    	Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
    	Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey, ApplicationDAO.class, applicationId);
    	
    	Query<WidgetDAO> q = ofy.query(WidgetDAO.class).ancestor(applicationKey);
    	
    	ArrayList<WidgetDAO> toReturn = new ArrayList<WidgetDAO>();
    	if ( null != q ) {
    		for ( WidgetDAO widget : q ) {
    			toReturn.add(widget);
    		}
    	}
    	return toReturn;
    }
    
    public static void putApplication(ApplicationDAO application) {
    	ofy.put(application);
    }
    
    public static void putPlace(PlaceDAO place) {
    	ofy.put(place);
    }
    
    public static void putReferenceCodeGenerator(ReferenceCodeGeneratorDAO rcg) {
    	ofy.put(rcg);
    }
    
    public static void putWidget(WidgetDAO widget ) {
    	ofy.put(widget);
    }
    
    public static void putWidgetOption(ArrayList<WidgetOptionDAO> widgetOptions) {
    	ofy.put(widgetOptions);
    }
    
    public static void deleteWidgetOptions(ArrayList<WidgetOptionDAO> widgetOptions) {
    	ofy.delete(widgetOptions);
    }
    
    public static void putWidgetOption(WidgetOptionDAO widgetOption) {
    	ofy.put(widgetOption);
    }
    
    public static void putWidgetInput(WidgetInputDAO input) {
    	ofy.put(input);
    }
    
    private static void deleteWidgetOptions(String placeId, String applicationId, String widgetId) {
    	ofy.delete(getWidgetOptionKeys(placeId, applicationId, widgetId));
    }
    
}