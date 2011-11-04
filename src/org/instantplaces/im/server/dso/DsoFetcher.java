/**
 * 
 */
package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.instantplaces.im.server.Log;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class DsoFetcher {
	
	
	/**
	 * Delete the specified widget
	 * TODO: recyle codes
	 * @param pm
	 * @param placeId
	 * @param applicationId
	 * @param widgetId
	 */
	public static void deleteWidgetFromDSO(PersistenceManager pm,
			String placeId, String applicationId, String widgetId) {
		Query query = pm.newQuery(WidgetDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId );
		
	    DsoFetcher.deleteWidgetOptionFromDSO(pm, placeId, applicationId, widgetId);
	    DsoFetcher.deleteWidgetInputDSO(pm, placeId, applicationId, widgetId);
	}

	public static void deleteWidgetFromDSO(PersistenceManager pm,
			String placeId, String applicationId) {
		Query query = pm.newQuery(WidgetDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam ");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    query.deletePersistentAll(placeId, applicationId );
		
	    DsoFetcher.deleteWidgetOptionFromDSO(pm, placeId, applicationId);
	    DsoFetcher.deleteWidgetInputDSO(pm, placeId, applicationId);
	}

	/**
	 * Delete all options from the specified application
	 * @param pm
	 * @param placeId
	 * @param applicationId
	 * @param widgetId
	 */
	public static void deleteWidgetOptionFromDSO(PersistenceManager pm,
			String placeId, String applicationId) {
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam ");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    query.deletePersistentAll(placeId, applicationId);
		
	}
	/**
	 * Delete all options from the specified widget
	 * @param pm
	 * @param placeId
	 * @param applicationId
	 * @param widgetId
	 */
	public static void deleteWidgetOptionFromDSO(PersistenceManager pm,
			String placeId, String applicationId, String widgetId) {
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId);
		
	}
	
	
	/**
	 * Delete all input directed at the specified application;
	 * @param pm
	 * @param placeId
	 * @param applicationId
	 * @param widgetId
	 */
	public static void deleteWidgetInputDSO(PersistenceManager pm, String placeId, String applicationId) {
		Query query = pm.newQuery(WidgetInputDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam ");
	    query.declareParameters("String placeIdParam, String applicationIdParam  ");
	    query.deletePersistentAll(placeId, applicationId);
	}
	
	/**
	 * Delete all input directed at the specified widget;
	 * @param pm
	 * @param placeId
	 * @param applicationId
	 * @param widgetId
	 */
	public static void deleteWidgetInputDSO(PersistenceManager pm, String placeId, String applicationId, String widgetId) {
		Query query = pm.newQuery(WidgetInputDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId);
	}
	
	public static void deleteWidgetInputDSO(PersistenceManager pm, String placeId, String applicationId, String widgetId, String widgetOptionId) {
		Query query = pm.newQuery(WidgetInputDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam && widgetOptionId == widgetOptionIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam, String widgetOptionIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId, widgetOptionId);
	}
	
	public static void deleteApplicationDSO(PersistenceManager pm, String placeId, String applicationId) {
		Query query = pm.newQuery(ApplicationDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    query.deletePersistentAll(placeId, applicationId);
	}
	
	public static void deleteApplicationsDSO(PersistenceManager pm,  long earlierThan) {
		Query query = pm.newQuery(ApplicationDSO.class);
	    query.setFilter("lastRequestTimestamp < earlierThanParam");
	    query.declareParameters("long earlierThanParam");
	    
	    
	    try {
	    	List<ApplicationDSO> result = (List<ApplicationDSO>)  query.execute(earlierThan);
	        if ( null != result ) {
	        	Log.get().warn("Found " + result.size() + " applications. Deleting...");
	        	for ( ApplicationDSO app : result ) {
	        		Log.get().warn("Deleting application: " + app.getApplicationId());
	        		DsoFetcher.deleteWidgetInputDSO(pm, app.getPlaceId(), app.getApplicationId());
	        		DsoFetcher.deleteWidgetOptionFromDSO(pm, app.getPlaceId(), app.getApplicationId());
	        		DsoFetcher.deleteWidgetFromDSO(pm, app.getPlaceId(), app.getApplicationId());
	        		DsoFetcher.deleteApplicationDSO(pm, app.getPlaceId(), app.getApplicationId());
	        	}
	        } else {
	        	Log.get().debug("No applications found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	  
	}
	public static ApplicationDSO getApplicationDSO( PersistenceManager pm, String placeId, String applicationId ) {
	Log.get().debug("Fetching application Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
	
		
		Query query = pm.newQuery(ApplicationDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    query.setUnique(true);
	    
	    try {
	    	ApplicationDSO result = (ApplicationDSO) query.execute(placeId, applicationId);
	        if ( null != result ) {
	        	Log.get().debug("Found applications. Returning first.");
	        	
	        	return result;
	        } else {
	        	Log.get().debug("Application not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<ApplicationDSO> getApplicationsDSO( PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching applications for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(ApplicationDSO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    
	    
	    try {
	    	List<ApplicationDSO> result = (List<ApplicationDSO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Applications not found.");
	    		
	    	} else {
	    		Log.get().debug("Found applications");
	    	}
	    	
	    	ArrayList<ApplicationDSO> toReturn = new ArrayList<ApplicationDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<ApplicationDSO> getApplicationsDSO( PersistenceManager pm, long lastActivityTimeLessThan ) {
		Log.get().debug("Fetching applications from Data Store.");
		
		Query query = pm.newQuery(ApplicationDSO.class);
	    query.setFilter("lastRequestTimestamp < timeParam");
	    query.declareParameters("long timeParam");
	    
	    
	    try {
	    	List<ApplicationDSO> result = (List<ApplicationDSO>) query.execute(lastActivityTimeLessThan);
	    
	    	if ( null == result) {
	    		Log.get().warn("Applications not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " applications");
	    	}
	    	
	    	ArrayList<ApplicationDSO> toReturn = new ArrayList<ApplicationDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<PlaceDSO> getPlacesFromDSO( PersistenceManager pm ) {
		Log.get().debug("Fetching places from Data Store.");
		
		Query query = pm.newQuery(PlaceDSO.class);
	   
	    
	    try {
	    	List<PlaceDSO> result = (List<PlaceDSO>) query.execute();
	    
	    	if ( null == result) {
	    		Log.get().warn("Places not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " places");
	    	}
	    	
	    	ArrayList<PlaceDSO> toReturn = new ArrayList<PlaceDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static WidgetInputDSO getLastWidgetInputFromDSO( PersistenceManager pm, String placeId, String applicationId, String widgetId) {
		Log.get().debug("Fetching input Place(" + placeId + "), Application("+ applicationId + "),  from Data Store.");
	
		Query query = pm.newQuery(WidgetInputDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam" );
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.setOrdering("timeStamp desc");
	    query.setUnique(true);
	    
	    try {
	    	WidgetInputDSO result = (WidgetInputDSO) query.execute(placeId, applicationId, widgetId);
	    
	    	
	    	return result;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<WidgetInputDSO> getWidgetInputFromDSO( PersistenceManager pm, String placeId, String applicationId, long from) {
		Log.get().debug("Fetching widget Place(" + placeId + "), Application("+ applicationId + "),  from Data Store.");
	
		Query query = pm.newQuery(WidgetInputDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && timeStamp > fromParameter" );
	    query.declareParameters("String placeIdParam, String applicationIdParam, long fromParameter");
	    
	    
	    try {
	    	List<WidgetInputDSO> result = (List<WidgetInputDSO>) query.execute(placeId, applicationId, from);
	    
	    	if ( null == result) {
	    		Log.get().warn("Input not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " inputs");
	    	}
	    	
	    	ArrayList<WidgetInputDSO> toReturn = new ArrayList<WidgetInputDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static WidgetDSO getWidgetFromDSO( PersistenceManager pm, String placeId, String applicationId, String widgetId) {
		Log.get().debug("Fetching widget Place(" + placeId + "), Application("+ applicationId + "), Widget(" + widgetId + ") from Data Store.");
	
		Query query = pm.newQuery(WidgetDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.setUnique(true);
	    
	    try {
	    	WidgetDSO result = (WidgetDSO) query.execute(placeId, applicationId, widgetId);
	        if ( null != result ) {
	        	Log.get().debug("Found widgets. Returning first.");
	        	
	        	return result;
	        } else {
	        	Log.get().debug("Widget not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static WidgetOptionDSO getWidgetOptionDSOByReferenceCode(PersistenceManager pm, String referenceCode ) {
		Log.get().debug("Fetching WidgetOptionDSO with referenceCode(" + referenceCode + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("referenceCode == idParam");
	    query.declareParameters("String idParam");
	    
	    try {
	        List<WidgetOptionDSO> results = (List<WidgetOptionDSO>) query.execute(referenceCode);
	        if (!results.isEmpty()) {
	        	Log.get().debug("Found " + results.size() + " widget options. Returning first.");
	        	WidgetOptionDSO widgetOption = results.get(0);
	        	return widgetOption;
	        } else {
	        	Log.get().debug("Widget option not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store.");
	    	Log.get().error(e.getMessage());
	    	e.printStackTrace();
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<WidgetOptionDSO> getWidgetOptionFromDSO(
			PersistenceManager pm, String placeId) {
		Log.get().debug("Fetching WidgetOptionDSO from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    
	    try {
	        List<WidgetOptionDSO> results = (List<WidgetOptionDSO>) query.execute(placeId);
	        ArrayList<WidgetOptionDSO> toReturn = new ArrayList<WidgetOptionDSO>();
	        
	        if ( null == results) {
	    		Log.get().warn("Widget options not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + results.size() + " widget options");
	    	}
	    	
	    	
	    	toReturn.addAll(results);
	    	return toReturn;
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store.");
	    	Log.get().error(e.getMessage());
	    	e.printStackTrace();
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<WidgetOptionDSO> getWidgetOptionFromDSO(
			PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching WidgetOptionDSO from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    
	    try {
	        List<WidgetOptionDSO> results = (List<WidgetOptionDSO>) query.execute(placeId, applicationId);
	        ArrayList<WidgetOptionDSO> toReturn = new ArrayList<WidgetOptionDSO>();
	        
	        if ( null == results) {
	    		Log.get().warn("Widget options not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + results.size() + " widget options");
	    	}
	    	
	    	
	    	toReturn.addAll(results);
	    	return toReturn;
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store.");
	    	Log.get().error(e.getMessage());
	    	e.printStackTrace();
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<WidgetOptionDSO> getWidgetOptionsFromDSO( PersistenceManager pm, String placeId) {
		Log.get().debug("Fetching widgets options for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    try {
	    	List<WidgetOptionDSO> result = (List<WidgetOptionDSO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetOptionDSO> toReturn = new ArrayList<WidgetOptionDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<WidgetDSO> getWidgetsFromDSO( PersistenceManager pm, String placeId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetDSO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    try {
	    	List<WidgetDSO> result = (List<WidgetDSO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetDSO> toReturn = new ArrayList<WidgetDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<WidgetDSO> getWidgetsFromDSO( PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    try {
	    	List<WidgetDSO> result = (List<WidgetDSO>) query.execute(placeId, applicationId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetDSO> toReturn = new ArrayList<WidgetDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<WidgetDSO> getVolatileWidgetsFromDSO( PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetDSO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && volatileWidget == true");
	    query.declareParameters("String placeIdParam, String applicationIdParam ");
	    try {
	    	List<WidgetDSO> result = (List<WidgetDSO>) query.execute(placeId, applicationId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetDSO> toReturn = new ArrayList<WidgetDSO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static PlaceDSO getPlaceFromDSO(PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching place Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(PlaceDSO.class);
	    query.setFilter("placeId == idParam");
	    query.declareParameters("String idParam");
	    query.setUnique(true);
	    
	    try {
	        PlaceDSO result = (PlaceDSO) query.execute(placeId);
	        if ( null != result ) {
	        	Log.get().debug("Found places. Returning first.");
	        	
	        	return result;
	        } else {
	        	Log.get().debug("Place not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}


}
