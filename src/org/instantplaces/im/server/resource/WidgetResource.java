package org.instantplaces.im.server.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.instantplaces.im.server.rest.RestConverter;
import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetRest;
import org.instantplaces.im.server.Log;
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

	@Override
	protected Object doPut(Object in) {
		String errorMessage = "Put not allowed. Sorry, only GET or POST methods allowed for this resource.";
		Log.get().error(errorMessage);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, errorMessage);
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

		WidgetArrayListREST receivedWidgetListREST = (WidgetArrayListREST) in;

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

		for (WidgetRest receivedWidget : receivedWidgetListREST.widgets) {

			WidgetDao widget = Dao
					.getWidget(this.placeId, this.appId, receivedWidget.getWidgetId());

			if (null != widget) {
				//TODO: merge the other fields of the widget
				
				/*
				 * The widget already exists, so fetch its options
				 */
				ArrayList<WidgetOptionDao> optionsFromDataStore = new ArrayList<WidgetOptionDao>(Dao.getWidgetOptions(widget.getKey()));
				widget.setWidgetOptions(optionsFromDataStore);
				
				/*
				 * And now merge with the received one
				 */
				WidgetDao wDSO = DaoConverter.getWidgetDao(existingApplicationDSO,
						receivedWidget);
				widget.mergeOptionsToAdd(wDSO);
				ArrayList<WidgetOptionDao> optionsToDelete = widget.mergeOptionsToDelete(wDSO);

				/*
				 * Recycle the reference codes and delete all input associated
				 * with each option that not longer exists in the widget
				 */
				for (WidgetOptionDao option : optionsToDelete) {
					rcg.recycleCode(option.getReferenceCode());
					Dao.delete(Dao.getWidgetInputsKeys(this.placeId, this.appId,
							widget.getWidgetId(), option.getWidgetOptionId()));
				}

				/*
				 * Bulk delete the options
				 */
				Dao.delete(optionsToDelete);

				/*
				 * Assign reference codes to the new options
				 */
				widget.assignReferenceCodes(rcg);
				
				/*
				 * And save the options
				 */
				Dao.put(widget.getWidgetOptions());
				

			} else {
				widget = DaoConverter.getWidgetDao(existingApplicationDSO, receivedWidget);
				widget.assignReferenceCodes(rcg);
				Dao.put(widget);
				Dao.put(widget.getWidgetOptions());
			}
			widgetsAdded.add(widget);
		}

		/*
		 * We are going to return this
		 */
		WidgetArrayListREST walr = RestConverter.widgetArrayListFromDso(widgetsAdded);

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
			toReturn = RestConverter.widgetArrayListFromDso(widgets);

		

		}

		return toReturn;
	}

	private WidgetArrayListREST deleteSpecifiedWidgets(
			ArrayList<Key<WidgetDao>> widgetsToDeleteKeys, boolean volatileOnly) {

		ReferenceCodeGeneratorDAO rcg = Dao.getReferenceCodeGenerator(this.placeId);

		Collection<WidgetDao> widgetsToDelete = Dao.get(widgetsToDeleteKeys).values();

		ArrayList<WidgetOptionDao> widgetOptionsToDelete = new ArrayList<WidgetOptionDao>();

		ArrayList<WidgetInputDao> widgetInputToDelete = new ArrayList<WidgetInputDao>();

		Iterator<WidgetDao> iterator = widgetsToDelete.iterator();

		while (iterator.hasNext()) {
			WidgetDao widget = iterator.next();

			if (volatileOnly && !widget.isVolatileWidget()) {
				iterator.remove();
				continue;
			}

			/*
			 * Recycle the reference codes
			 */
			List<WidgetOptionDao> widgetOptions = Dao.getWidgetOptions(this.placeId, this.appId,
					widget.getWidgetId());

			for (WidgetOptionDao option : widgetOptions) {
				rcg.recycleCode(option.getReferenceCode());

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
		WidgetArrayListREST walr = new WidgetArrayListREST();
		walr.widgets = new ArrayList<WidgetRest>();

		for (Key<WidgetDao> widgetKey : widgetsToDeleteKeys) {
			/*
			 * Add the rest version to the list to return to the client
			 */
			WidgetRest toReturn = new WidgetRest();
			toReturn.setPlaceId(this.placeId);
			toReturn.setApplicationId(this.appId);
			toReturn.setWidgetId(widgetKey.getName());

			walr.widgets.add(toReturn);
		}

		return walr;
	}

	@Override
	protected Object doDelete() {

		Log.get().debug("Responding to DELETE request.");
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put("place/"+this.placeId+"/application/"+this.appId+"/widget", null);
		
		
		WidgetArrayListREST toReturn = new WidgetArrayListREST();
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
			return WidgetArrayListREST.class;
		}
	}

}
