/**
 * 
 */
package org.instantplaces.im.server.dao;

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
		Query query = pm.newQuery(WidgetDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId );
		
	    DsoFetcher.deleteWidgetOptionFromDSO(pm, placeId, applicationId, widgetId);
	    DsoFetcher.deleteWidgetInputDSO(pm, placeId, applicationId, widgetId);
	}

	public static void deleteWidgetFromDSO(PersistenceManager pm,
			String placeId, String applicationId) {
		Query query = pm.newQuery(WidgetDAO.class);
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
		Query query = pm.newQuery(WidgetOptionDAO.class);
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
		Query query = pm.newQuery(WidgetOptionDAO.class);
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
		Query query = pm.newQuery(WidgetInputDAO.class);
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
		Query query = pm.newQuery(WidgetInputDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId);
	}
	
	public static void deleteWidgetInputDSO(PersistenceManager pm, String placeId, String applicationId, String widgetId, String widgetOptionId) {
		Query query = pm.newQuery(WidgetInputDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam && widgetOptionId == widgetOptionIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam, String widgetOptionIdParam");
	    query.deletePersistentAll(placeId, applicationId, widgetId, widgetOptionId);
	}
	
	public static void deleteApplicationDSO(PersistenceManager pm, String placeId, String applicationId) {
		Query query = pm.newQuery(ApplicationDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    query.deletePersistentAll(placeId, applicationId);
	}
	
//	public static void deleteApplicationsDSO(PersistenceManager pm,  long earlierThan) {
//		Query query = pm.newQuery(ApplicationDAO.class);
//	    query.setFilter("lastRequestTimestamp < earlierThanParam");
//	    query.declareParameters("long earlierThanParam");
//	    
//	    
//	    try {
//	    	List<ApplicationDAO> result = (List<ApplicationDAO>)  query.execute(earlierThan);
//	        if ( null != result ) {
//	        	Log.get().warn("Found " + result.size() + " applications. Deleting...");
//	        	for ( ApplicationDAO app : result ) {
//	        		Log.get().warn("Deleting application: " + app.getApplicationId());
//	        		DsoFetcher.deleteWidgetInputDSO(pm, app.getPlaceId(), app.getApplicationId());
//	        		DsoFetcher.deleteWidgetOptionFromDSO(pm, app.getPlaceId(), app.getApplicationId());
//	        		DsoFetcher.deleteWidgetFromDSO(pm, app.getPlaceId(), app.getApplicationId());
//	        		DsoFetcher.deleteApplicationDSO(pm, app.getPlaceId(), app.getApplicationId());
//	        	}
//	        } else {
//	        	Log.get().debug("No applications found.");
//	        }
//	    } catch (Exception e) {
//	    	Log.get().error("Could not access data store." + e.getMessage());
//	    }  finally {
//	        query.closeAll();
//	    }
//	  
//	}
//	
	public static ApplicationDAO getApplicationDSO( PersistenceManager pm, String placeId, String applicationId ) {
	Log.get().debug("Fetching application Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
	
		
		Query query = pm.newQuery(ApplicationDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    query.setUnique(true);
	    
	    try {
	    	ApplicationDAO result = (ApplicationDAO) query.execute(placeId, applicationId);
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

	public static ArrayList<ApplicationDAO> getApplicationsDSO( PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching applications for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(ApplicationDAO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    
	    
	    try {
	    	List<ApplicationDAO> result = (List<ApplicationDAO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Applications not found.");
	    		
	    	} else {
	    		Log.get().debug("Found applications");
	    	}
	    	
	    	ArrayList<ApplicationDAO> toReturn = new ArrayList<ApplicationDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<ApplicationDAO> getApplicationsDSO( PersistenceManager pm, long lastActivityTimeLessThan ) {
		Log.get().debug("Fetching applications from Data Store.");
		
		Query query = pm.newQuery(ApplicationDAO.class);
	    query.setFilter("lastRequestTimestamp < timeParam");
	    query.declareParameters("long timeParam");
	    
	    
	    try {
	    	List<ApplicationDAO> result = (List<ApplicationDAO>) query.execute(lastActivityTimeLessThan);
	    
	    	if ( null == result) {
	    		Log.get().warn("Applications not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " applications");
	    	}
	    	
	    	ArrayList<ApplicationDAO> toReturn = new ArrayList<ApplicationDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<PlaceDAO> getPlacesFromDSO( PersistenceManager pm ) {
		Log.get().debug("Fetching places from Data Store.");
		
		Query query = pm.newQuery(PlaceDAO.class);
	   
	    
	    try {
	    	List<PlaceDAO> result = (List<PlaceDAO>) query.execute();
	    
	    	if ( null == result) {
	    		Log.get().warn("Places not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " places");
	    	}
	    	
	    	ArrayList<PlaceDAO> toReturn = new ArrayList<PlaceDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static WidgetInputDAO getLastWidgetInputFromDSO( PersistenceManager pm, String placeId, String applicationId, String widgetId) {
		Log.get().debug("Fetching input Place(" + placeId + "), Application("+ applicationId + "),  from Data Store.");
	
		Query query = pm.newQuery(WidgetInputDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam" );
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.setOrdering("timeStamp desc");
	    query.setUnique(true);
	    
	    try {
	    	WidgetInputDAO result = (WidgetInputDAO) query.execute(placeId, applicationId, widgetId);
	    
	    	
	    	return result;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<WidgetInputDAO> getWidgetInputFromDSO( PersistenceManager pm, String placeId, String applicationId, long from) {
		Log.get().debug("Fetching widget Place(" + placeId + "), Application("+ applicationId + "),  from Data Store.");
	
		Query query = pm.newQuery(WidgetInputDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && timeStamp > fromParameter" );
	    query.declareParameters("String placeIdParam, String applicationIdParam, long fromParameter");
	    
	    
	    try {
	    	List<WidgetInputDAO> result = (List<WidgetInputDAO>) query.execute(placeId, applicationId, from);
	    
	    	if ( null == result) {
	    		Log.get().warn("Input not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " inputs");
	    	}
	    	
	    	ArrayList<WidgetInputDAO> toReturn = new ArrayList<WidgetInputDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static WidgetDAO getWidgetFromDSO( PersistenceManager pm, String placeId, String applicationId, String widgetId) {
		Log.get().debug("Fetching widget Place(" + placeId + "), Application("+ applicationId + "), Widget(" + widgetId + ") from Data Store.");
	
		Query query = pm.newQuery(WidgetDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && widgetId == widgetIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam, String widgetIdParam");
	    query.setUnique(true);
	    
	    try {
	    	WidgetDAO result = (WidgetDAO) query.execute(placeId, applicationId, widgetId);
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

	public static WidgetOptionDAO getWidgetOptionDSOByReferenceCode(PersistenceManager pm, String referenceCode ) {
		Log.get().debug("Fetching WidgetOptionDSO with referenceCode(" + referenceCode + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDAO.class);
	    query.setFilter("referenceCode == idParam");
	    query.declareParameters("String idParam");
	    
	    try {
	        List<WidgetOptionDAO> results = (List<WidgetOptionDAO>) query.execute(referenceCode);
	        if (!results.isEmpty()) {
	        	Log.get().debug("Found " + results.size() + " widget options. Returning first.");
	        	WidgetOptionDAO widgetOption = results.get(0);
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

	public static ArrayList<WidgetOptionDAO> getWidgetOptionFromDSO(
			PersistenceManager pm, String placeId) {
		Log.get().debug("Fetching WidgetOptionDSO from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDAO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    
	    try {
	        List<WidgetOptionDAO> results = (List<WidgetOptionDAO>) query.execute(placeId);
	        ArrayList<WidgetOptionDAO> toReturn = new ArrayList<WidgetOptionDAO>();
	        
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
	
	public static ArrayList<WidgetOptionDAO> getWidgetOptionFromDSO(
			PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching WidgetOptionDSO from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    
	    try {
	        List<WidgetOptionDAO> results = (List<WidgetOptionDAO>) query.execute(placeId, applicationId);
	        ArrayList<WidgetOptionDAO> toReturn = new ArrayList<WidgetOptionDAO>();
	        
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

	public static ArrayList<WidgetOptionDAO> getWidgetOptionsFromDSO( PersistenceManager pm, String placeId) {
		Log.get().debug("Fetching widgets options for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDAO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    try {
	    	List<WidgetOptionDAO> result = (List<WidgetOptionDAO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetOptionDAO> toReturn = new ArrayList<WidgetOptionDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<WidgetDAO> getWidgetsFromDSO( PersistenceManager pm, String placeId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetDAO.class);
	    query.setFilter("placeId == placeIdParam ");
	    query.declareParameters("String placeIdParam");
	    try {
	    	List<WidgetDAO> result = (List<WidgetDAO>) query.execute(placeId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetDAO> toReturn = new ArrayList<WidgetDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static ArrayList<WidgetDAO> getWidgetsFromDSO( PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam");
	    query.declareParameters("String placeIdParam, String applicationIdParam");
	    try {
	    	List<WidgetDAO> result = (List<WidgetDAO>) query.execute(placeId, applicationId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetDAO> toReturn = new ArrayList<WidgetDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}
	
	public static ArrayList<WidgetDAO> getVolatileWidgetsFromDSO( PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetDAO.class);
	    query.setFilter("placeId == placeIdParam && applicationId == applicationIdParam && volatileWidget == true");
	    query.declareParameters("String placeIdParam, String applicationIdParam ");
	    try {
	    	List<WidgetDAO> result = (List<WidgetDAO>) query.execute(placeId, applicationId);
	    
	    	if ( null == result) {
	    		Log.get().warn("Widgets not found.");
	    		
	    	} else {
	    		Log.get().debug("Found " + result.size() + " widgets");
	    	}
	    	
	    	ArrayList<WidgetDAO> toReturn = new ArrayList<WidgetDAO>();
	    	toReturn.addAll(result);
	    	return toReturn;
	    	
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store." + e.getMessage());
	    }  finally {
	        query.closeAll();
	    }
	    return null;
	}

	public static PlaceDAO getPlaceFromDSO(PersistenceManager pm, String placeId ) {
		Log.get().debug("Fetching place Place(" + placeId + ") from Data Store.");
		
		Query query = pm.newQuery(PlaceDAO.class);
	    query.setFilter("placeId == idParam");
	    query.declareParameters("String idParam");
	    query.setUnique(true);
	    
	    try {
	        PlaceDAO result = (PlaceDAO) query.execute(placeId);
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
