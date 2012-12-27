package org.instantplaces.im.server.rest.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.instantplaces.im.server.rest.representation.json.RestConverter;
import org.instantplaces.im.server.rest.representation.json.WidgetListRest;
import org.instantplaces.im.server.rest.representation.json.WidgetRest;
import org.instantplaces.im.server.logging.Log;
import org.instantplaces.im.server.dao.ApplicationDao;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.DaoConverter;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.ReferenceCodeGeneratorDAO;
import org.instantplaces.im.server.dao.WidgetDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Key;

public class WidgetResource extends GenericResource {

	private HashMap<String, WidgetDao> widgetsProxy;
	private HashMap<Key<WidgetDao>, ArrayList<WidgetOptionDao>> widgetOptionsProxy;
	
	@Override
	protected Object doPut(Object in) {
		String errorMessage = "Put not allowed. Sorry, only GET or POST methods allowed for this resource.";
		Log.get().error(errorMessage);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
	}

	
	private void fillProxy(WidgetListRest widgetListRest) {
		if ( widgetListRest.getWidgets().size() > 5 ) {
			Log.debug(this, "Filling in widget and widgetOption proxy.");
			Dao.beginTransaction();
			List<WidgetDao> widgetsDao = Dao.getWidgets(this.placeId, this.appId);
			List<WidgetOptionDao> widgetOptionsDao = Dao.getWidgetOptions(this.placeId, this.appId);
			
			Dao.commitOrRollbackTransaction();
			this.widgetsProxy = new HashMap<String, WidgetDao>();
			for ( WidgetDao widgetDao : widgetsDao ) {
				this.widgetsProxy.put(this.placeId+this.appId+widgetDao.getWidgetId(), widgetDao);
			}
			
			this.widgetOptionsProxy = new HashMap<Key<WidgetDao>, ArrayList<WidgetOptionDao>>();
			for ( WidgetOptionDao optionDao : widgetOptionsDao ) {
				ArrayList<WidgetOptionDao> current = this.widgetOptionsProxy.get(optionDao.getWidgetKey());
				if ( null == current ) {
					current = new ArrayList<WidgetOptionDao> ();
				}
				current.add(optionDao);
				this.widgetOptionsProxy.put(optionDao.getWidgetKey(), current);
			}
			
		} else {
			Log.debug(this, "Not enough widgets to compensate using proxy...");
		}
		
	}
	

	private ArrayList<WidgetOptionDao> getWidgetOptions( Key<WidgetDao>  widgetKey) {
		if ( null != this.widgetOptionsProxy ) {
			
			if ( this.widgetOptionsProxy.containsKey(widgetKey) ) {
				Log.debug(this, "Returning widgetOptions from proxy store.");
				return this.widgetOptionsProxy.get(widgetKey);
			}
		}
		Log.debug(this, "Returning widgetOptions from datastore.");
		return new ArrayList<WidgetOptionDao>(Dao.getWidgetOptions(widgetKey));
		
	}
	
	private WidgetDao getWidget( String placeId, String applicationId, String widgetId ) {
		if ( null != widgetsProxy ) {
			String key = placeId+applicationId+widgetId;
			if ( widgetsProxy.containsKey(key) ) {
				Log.debug(this, "Returning widget from proxy store.");
				return widgetsProxy.get(key);
			}
		}
		Log.debug(this, "Returning widget from datastore.");
		return Dao.getWidget(placeId, applicationId, widgetId);
	}
	
	
	@Override
	protected Object doPost(Object in) {
		long start = System.currentTimeMillis();
		Log.get().debug("Responding to Post request.");
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put("place/"+this.placeId+"/application/"+this.appId+"/widget", null);
		
		PlaceDao existingPlaceDSO = null;
		ReferenceCodeGeneratorDAO rcg = null;
		ApplicationDao existingApplicationDSO = null;

		WidgetListRest receivedWidgetListREST = (WidgetListRest) in;
		this.fillProxy(receivedWidgetListREST);
		/*
		 * Get the Place from the store. Create one if it does not exist yet
		 */
		Dao.beginTransaction();
		existingPlaceDSO = Dao.getPlace(this.placeId);
		if (null == existingPlaceDSO) {
			Log.get().info(
					"The specified place " + this.placeId + " was not found. Creating new...");
			existingPlaceDSO = new PlaceDao(this.placeId);
			Dao.put(existingPlaceDSO);

			/*
			 * A new place needs a new ReferenceCodeGenerator
			 */
			rcg = new ReferenceCodeGeneratorDAO(existingPlaceDSO);
			Dao.put(rcg);
		} else {
			rcg = Dao.getReferenceCodeGenerator(this.placeId);
			
			/*
			 * Only needed because I sometimes delete the RCG directly from the appengine datastore viewer.
			 */
			if ( null == rcg ) {
				/*
				 * A new place needs a new ReferenceCodeGenerator
				 */
				rcg = new ReferenceCodeGeneratorDAO(existingPlaceDSO);
				Dao.put(rcg);
			}
				
		}

		/*
		 * Get the Application from the store. Create one if it does not exist
		 * yet.
		 */
		existingApplicationDSO = Dao.getApplication(this.placeId, this.appId);
		if (null == existingApplicationDSO) {
			Log.get().info(
					"The specified application " + this.appId + "was not found. Creating new...");
			existingApplicationDSO = new ApplicationDao(existingPlaceDSO, this.appId);
			Dao.put(existingApplicationDSO);
		}

		ArrayList<WidgetDao> widgetsAdded = new ArrayList<WidgetDao>();

		for (WidgetRest receivedWidgetRest : receivedWidgetListREST.getWidgets()) {

			WidgetDao existingWidgetDao = this
					.getWidget(this.placeId, this.appId, receivedWidgetRest.getWidgetId());
			
			/*
			 * The widget already exists, so merge with the received one
			 */
			if (null != existingWidgetDao) {
				existingWidgetDao.clearChangedFlag();
				
				//TODO: merge the other fields of the widget
				existingWidgetDao.setShortDescription(receivedWidgetRest.getShortDescription());
				existingWidgetDao.setLongDescription(receivedWidgetRest.getLongDescription());
				
				existingWidgetDao.setControlType(receivedWidgetRest.getControlType());
				
				
				existingWidgetDao.setWidgetParameters(DaoConverter.getWidgetParameterDao( receivedWidgetRest.getWidgetParameters()) );
				
				
				/*
				 * The widget already exists, so fetch its options
				 */
				ArrayList<WidgetOptionDao> optionsFromDataStore = this.getWidgetOptions(existingWidgetDao.getKey());
				existingWidgetDao.setWidgetOptions(optionsFromDataStore);
				
				/*
				 * And now merge with the received one
				 */
				WidgetDao receivedWidgetDao = DaoConverter.getWidgetDao(existingApplicationDSO,
						receivedWidgetRest);
				ArrayList<WidgetOptionDao> optionsToAdd = existingWidgetDao.mergeOptionsToAdd(receivedWidgetDao);
				ArrayList<WidgetOptionDao> optionsToDelete = existingWidgetDao.mergeOptionsToDelete(receivedWidgetDao);

				/*
				 * Recycle the reference codes and delete all input associated
				 * with each option that not longer exists in the widget
				 */
				for (WidgetOptionDao option : optionsToDelete) {
					rcg.recycleCode(option);
					Dao.delete(Dao.getWidgetInputsKeys(this.placeId, this.appId,
							existingWidgetDao.getWidgetId(), option.getWidgetOptionId()));
				}

				/*
				 * Bulk delete the options
				 */
				Dao.delete(optionsToDelete);

				/*
				 * Assign reference codes to the new options
				 */
				ArrayList<WidgetOptionDao> optionsChanged =  existingWidgetDao.assignReferenceCodes(rcg);
				
				
				/*
				 * Save the widget
				 */
				if ( existingWidgetDao.isChangedFlag() ) {
					Log.debugFinest(this, "Widget " + existingWidgetDao.getWidgetId() + " changed, updating datastore.");
					Dao.put(existingWidgetDao);
				} else {
					Log.debugFinest(this, "Widget " + existingWidgetDao.getWidgetId() + " did not change, skipping datastore put.");
				}
				
				/*
				 * And save the options
				 */
				//Dao.put(existingWidgetDao.getWidgetOptions());
				if ( optionsChanged.size() <= 0 && optionsToAdd.size() <= 0) {
					Log.debugFinest("No options changed in widget " + existingWidgetDao.getWidgetId() + ", skipping datastore put...");
				} else {
					Log.debugFinest("Options changed in widget " + existingWidgetDao.getWidgetId() + ", updating datastore.");
					Dao.put(optionsChanged);
					Dao.put(optionsToAdd);
				}

			} else {
				existingWidgetDao = DaoConverter.getWidgetDao(existingApplicationDSO, receivedWidgetRest);
				existingWidgetDao.assignReferenceCodes(rcg);
				Dao.put(existingWidgetDao);
				Dao.put(existingWidgetDao.getWidgetOptions());
			}
			widgetsAdded.add(existingWidgetDao);
		}

		/*
		 * We are going to return this
		 */
		WidgetListRest walr = RestConverter.widgetArrayListFromDso(widgetsAdded);

		/*
		 * Save the reference code
		 */
		Dao.put(rcg);

		if (!Dao.commitOrRollbackTransaction()) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Could not commit transaction");
		}

		long end = System.currentTimeMillis();
		Log.get().debug("Time: " + (end - start));
		return walr;
	}

	@Override
	protected Object doGet() {
		Log.get().debug("Responding to GET request.");

		Object toReturn = null;
		

		if (this.widgetId != null) {
			Dao.beginTransaction();
			/*
			 * Return the specified widget
			 */
			WidgetDao widget = Dao.getWidget(this.placeId, this.appId, this.widgetId);

			Dao.commitOrRollbackTransaction();

			if (widget == null) {
				String errorMessage = "The specified widget was not found.";
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, errorMessage);
			} else {
				Log.get().debug("Widget found: " + widget.toString());
				toReturn = RestConverter.getWidgetRest(widget);
			}
		} else {
			MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
			ArrayList<WidgetDao> widgets = (ArrayList<WidgetDao>) syncCache.get("place/"+this.placeId+"/application/"+this.appId+"/widget");
			if ( null != widgets ) {
				
			} else {
				/*
				 * Return the list of widgets
				 */
				Dao.beginTransaction();
				widgets = new ArrayList<WidgetDao>(Dao.getWidgets(this.placeId, this.appId));
				for ( WidgetDao widget : widgets ) {
					ArrayList<WidgetOptionDao> options =  new ArrayList<WidgetOptionDao>(Dao.getWidgetOptions(widget.getKey()));
					
					widget.setWidgetOptions(options);
				}
				Dao.commitOrRollbackTransaction();
				syncCache.put("place/"+this.placeId+"/application/"+this.appId+"/widget", widgets);
			}
			

			

			/*
			 * Convert all to WidgetREST
			 */
			WidgetListRest listRest = RestConverter.widgetArrayListFromDso(widgets);
			listRest.setPlaceId(this.placeId);
			listRest.setApplicationId(this.appId);
			
			toReturn = listRest;
		}
		
		return toReturn;
	}

	private WidgetListRest deleteSpecifiedWidgets(
			ArrayList<Key<WidgetDao>> widgetsToDeleteKeys, boolean volatileOnly) {

		ReferenceCodeGeneratorDAO rcg = Dao.getReferenceCodeGenerator(this.placeId);

		Collection<WidgetDao> widgetsToDelete = Dao.get(widgetsToDeleteKeys).values();

		ArrayList<WidgetOptionDao> widgetOptionsToDelete = new ArrayList<WidgetOptionDao>();

		ArrayList<WidgetInputDao> widgetInputToDelete = new ArrayList<WidgetInputDao>();

		Iterator<WidgetDao> iterator = widgetsToDelete.iterator();

		while (iterator.hasNext()) {
			WidgetDao widget = iterator.next();
// TODO: remove this volatile optiion
//			if (volatileOnly && !widget.isVolatileWidget()) {
//				iterator.remove();
//				continue;
//			}

			/*
			 * Recycle the reference codes
			 */
			List<WidgetOptionDao> widgetOptions = Dao.getWidgetOptions(this.placeId, this.appId,
					widget.getWidgetId());

			for (WidgetOptionDao option : widgetOptions) {
				rcg.recycleCode(option);

				/*
				 * Add the option to the list of options that are going to be
				 * deleted in the end.
				 */
				widgetOptionsToDelete.add(option);
			}

			/*
			 * Get the list of input that belong to this widget, so that it can
			 * be deleted later
			 */
			widgetInputToDelete.addAll(Dao.getWidgetInputs(this.placeId, this.appId,
					widget.getWidgetId()));

		}
		Dao.delete(widgetInputToDelete);
		Dao.delete(widgetOptionsToDelete);
		Dao.delete(widgetsToDelete);

		/*
		 * Save the code generator with the new recycled codes.
		 */
		Dao.put(rcg);

		/*
		 * To be returned
		 */
		WidgetListRest walr = new WidgetListRest();
		walr.setWidgets(new ArrayList<WidgetRest>());

		for (Key<WidgetDao> widgetKey : widgetsToDeleteKeys) {
			/*
			 * Add the rest version to the list to return to the client
			 */
			WidgetRest toReturn = new WidgetRest();
			toReturn.setPlaceId(this.placeId);
			toReturn.setApplicationId(this.appId);
			toReturn.setWidgetId(widgetKey.getName());

			walr.getWidgets().add(toReturn);
		}

		return walr;
	}

	@Override
	protected Object doDelete() {

		Log.get().debug("Responding to DELETE request.");
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put("place/"+this.placeId+"/application/"+this.appId+"/widget", null);
		
		
		WidgetListRest toReturn = new WidgetListRest();
		ArrayList<Key<WidgetDao>> list = new ArrayList<Key<WidgetDao>>();

		Dao.beginTransaction();

		if (this.widgetId != null) { // Delete the specified widget

			Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, this.placeId);
			Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
					ApplicationDao.class, this.appId);
			Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey, WidgetDao.class,
					this.widgetId);

			list.add(widgetKey);
			toReturn = this.deleteSpecifiedWidgets(list, false);

		} else { // delete the widgets passed in the url param

			String widgetsToDeleteParam = this.getRequest().getOriginalRef().getQueryAsForm()
					.getFirstValue("widgets", "");

			if (widgetsToDeleteParam.length() > 0) {

				Log.get().debug("Deleting widgets: " + widgetsToDeleteParam);
				for (String widgetId : widgetsToDeleteParam.split(",")) {
					try {
						widgetId = URLDecoder.decode(widgetId, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						Log.get().warn("Could not decode widgetId passed in URL parameter. " + e.getMessage());
						e.printStackTrace();
					}
					Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, this.placeId);
					Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
							ApplicationDao.class, this.appId);
					Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey, WidgetDao.class,
							widgetId);

					list.add(widgetKey);
				}

				toReturn = this.deleteSpecifiedWidgets(list, false);

			} else { // delete all widgets from the app
				boolean volatileOnly = this.getRequest().getOriginalRef().getQueryAsForm()
						.getFirstValue("volatileonly", "true").equalsIgnoreCase("true");

				list.addAll(Dao.getWidgetsKeys(this.placeId, this.appId));

				toReturn = this.deleteSpecifiedWidgets(list, volatileOnly);

			}
		}

		if (!Dao.commitOrRollbackTransaction()) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					"Could not commit transaction");

		}
		return toReturn;

	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getResourceClass() {
		if (this.getMethod().equals(Method.DELETE)) {
			return WidgetRest.class;
		} else {
			return WidgetListRest.class;
		}
	}

}
