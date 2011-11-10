/**
 * 
 */
package org.instantplaces.im.server.dao;

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

public class DAO extends DAOBase {
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
		try {
			ofy.getTxn().commit();
			success = true;
		} catch (Exception e) {
			Log.get().warn("Could not commit transaction: " + e.getMessage());
		} finally {
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

	public static <T> java.util.Map<Key<T>,T>	get(java.lang.Iterable<? extends Key<? extends T>> keys) {
		return ofy.get(keys);
	}
	
	
	public static void delete(java.lang.Iterable<?> keysOrEntities) {
		ofy.delete(keysOrEntities);
	}

	public static void delete(Object o) {
		ofy.delete(o);
	}

	public static void deleteWidget(Key<ApplicationDAO> applicationKey,
			String widgetId) {
		delete(new Key<WidgetDAO>(applicationKey, WidgetDAO.class, widgetId));
	}

	public static void deleteWidget(String placeId, String applicationId,
			String widgetId) {

		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		delete(widgetKey);

		// TODO: application is responsible for deleting the optins and widget
		// input
	}

	public static void deleteWidgetInput(Key<?> applicationOrWidgetKey) {

		delete(getWidgetInputsKeys(applicationOrWidgetKey));
	}

	public static void deleteWidgetInput(Key<ApplicationDAO> applicationKey,
			String widgetId) {

		delete(getWidgetInputsKeys(applicationKey, widgetId));
	}

	public static void deleteWidgetInput(String placeId, String applicationId) {
		delete(getWidgetInputsKeys(placeId, applicationId));
	}

	public static void deleteWidgetInput(String placeId, String applicationId,
			String widgetId) {
		delete(getWidgetInputsKeys(placeId, applicationId, widgetId));
	}

	public static void deleteWidgetOptions(Key<?> applicationOrWidgetKey) {

		delete(getWidgetOptionsKeys(applicationOrWidgetKey));
	}

	public static void deleteWidgetOptions(Key<ApplicationDAO> applicationKey,
			String widgetId) {

		delete(getWidgetOptionsKeys(applicationKey, widgetId));
	}

	public static void deleteWidgetOptions(String placeId, String applicationId) {
		delete(getWidgetOptionsKeys(placeId, applicationId));
	}

	public static void deleteWidgetOptions(String placeId,
			String applicationId, String widgetId) {
		delete(getWidgetOptionsKeys(placeId, applicationId, widgetId));
	}

	public static void deleteWidgets(Key<ApplicationDAO> applicationKey) {
		delete(getWidgetsKeys(applicationKey));
	}

	public static void deleteWidgets(Key<PlaceDAO> placeKey,
			String applicationId) {
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);

		delete(getWidgetsKeys(applicationKey));
	}

	public static void deleteWidgets(String placeId, String applicationId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);

		deleteWidgets(applicationKey);
	}

	public static ApplicationDAO getApplication(
			Key<ApplicationDAO> applicationKey) {
		return ofy.find(applicationKey);
	}

	public static ApplicationDAO getApplication(Key<PlaceDAO> parent,
			String applicationId) {
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(parent,
				ApplicationDAO.class, applicationId);
		return getApplication(applicationKey);
	}

	public static ApplicationDAO getApplication(String placeId,
			String applicationId) {
		Key<PlaceDAO> parent = new Key<PlaceDAO>(PlaceDAO.class, placeId);

		Key<ApplicationDAO> key = new Key<ApplicationDAO>(parent,
				ApplicationDAO.class, applicationId);

		return getApplication(key);
	}

	public static List<ApplicationDAO> getApplications(Key<PlaceDAO> parentKey) {
		Query<ApplicationDAO> q = ofy.query(ApplicationDAO.class).ancestor(
				parentKey);

		return q.list();
	}

	public static List<ApplicationDAO> getApplications(String placeId) {
		return getApplications(new Key<PlaceDAO>(PlaceDAO.class, placeId));
	}

	public static WidgetInputDAO getLastWidgetInput(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class)
				.ancestor(widgetKey).order("-timeStamp");

		return q.get();

	}

	public static PlaceDAO getPlace(Key<PlaceDAO> placeKey) {
		return ofy.find(placeKey);
	}

	public static PlaceDAO getPlace(String placeId) {
		return ofy.find(PlaceDAO.class, placeId);
	}

	public static List<Key<PlaceDAO>> getPlaceKeys() {
		/*
		 * We can't get all root entities inside a transaction, do don't use the
		 * static ofy
		 */
		Objectify ofy_ = ObjectifyService.begin();
		Query<PlaceDAO> q = ofy_.query(PlaceDAO.class);

		return q.listKeys();

	}

	public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(
			String placeId) {
		Query<ReferenceCodeGeneratorDAO> q = ofy.query(
				ReferenceCodeGeneratorDAO.class).ancestor(
				new Key<PlaceDAO>(PlaceDAO.class, placeId));

		return q.get();
	}

	public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(
			Key<PlaceDAO> placeKey) {
		Query<ReferenceCodeGeneratorDAO> q = ofy.query(
				ReferenceCodeGeneratorDAO.class).ancestor(placeKey);

		return q.get();
	}

	
	public static WidgetDAO getWidget(Key<ApplicationDAO> parentKey,
			String widgetId) {
		return getWidget(new Key<WidgetDAO>(parentKey, WidgetDAO.class,
				widgetId));
	}

	public static WidgetDAO getWidget(Key<WidgetDAO> widgetKey) {
		return ofy.find(widgetKey);
	}

	public static WidgetDAO getWidget(String placeId, String applicationId,
			String widgetId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		return getWidget(widgetKey);
	}

	public static List<WidgetInputDAO> getWidgetInputs(
			Key<?> widgetOrApplicationKey) {
		Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class).ancestor(
				widgetOrApplicationKey);

		return q.list();
	}

	public static List<WidgetInputDAO> getWidgetInputs(
			Key<?> widgetOrApplicationKey, long from) {
		Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class)
				.ancestor(widgetOrApplicationKey).filter("timeStamp > ", from);

		return q.list();
	}

	public static List<WidgetInputDAO> getWidgetInputs(String placeId,
			String applicationId) {

		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);

		return getWidgetInputs(applicationKey);

	}

	public static List<WidgetInputDAO> getWidgetInputs(String placeId,
			String applicationId, long from) {

		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);

		return getWidgetInputs(applicationKey, from);

	}

	public static List<WidgetInputDAO> getWidgetInputs(String placeId,
			String applicationId, String widgetId) {

		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		return getWidgetInputs(widgetKey);

	}

	public static List<WidgetInputDAO> getWidgetInputs(String placeId,
			String applicationId, String widgetId, long from) {

		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		return getWidgetInputs(widgetKey, from);

	}

	public static List<Key<WidgetInputDAO>> getWidgetInputsKeys(
			Key<?> widgetOrApplicationKey) {
		Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class).ancestor(
				widgetOrApplicationKey);

		return q.listKeys();
	}

	public static List<Key<WidgetInputDAO>> getWidgetInputsKeys(
			Key<?> applicationKey, String widgetId) {
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		Query<WidgetInputDAO> q = ofy.query(WidgetInputDAO.class).ancestor(
				widgetKey);

		return q.listKeys();
	}

	public static List<Key<WidgetInputDAO>> getWidgetInputsKeys(String placeId,
			String applicationId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);

		return getWidgetInputsKeys(applicationKey);
	}

	public static List<Key<WidgetInputDAO>> getWidgetInputsKeys(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		return getWidgetInputsKeys(widgetKey);
	}

	public static List<Key<WidgetInputDAO>> getWidgetInputsKeys(String placeId,
			String applicationId, String widgetId, String widgetOptionId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);
		Key<WidgetOptionDAO> widgetOptionKey = new Key<WidgetOptionDAO>(widgetKey,
				WidgetOptionDAO.class, widgetOptionId);
		

		return getWidgetInputsKeys(widgetOptionKey);
	}
	
	public static WidgetOptionDAO getWidgetOption(Key<WidgetDAO> parentKey,
			String widgetOptionId) {

		return getWidgetOption(new Key<WidgetOptionDAO>(parentKey,
				WidgetOptionDAO.class, widgetOptionId));

	}

	public static WidgetOptionDAO getWidgetOption(
			Key<WidgetOptionDAO> widgetOptionKey) {

		return ofy.find(widgetOptionKey);

	}

	public static WidgetOptionDAO getWidgetOption(String placeId,
			String applicationId, String widgetId, String widgetOptionId) {

		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);
		Key<WidgetOptionDAO> widgetOptionKey = new Key<WidgetOptionDAO>(
				widgetKey, WidgetOptionDAO.class, widgetOptionId);

		return getWidgetOption(widgetOptionKey);
	}

	public static List<WidgetOptionDAO> getWidgetOptions(
			Key<ApplicationDAO> applicationKey, String widgetId) {
		return getWidgetOptions(new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId));
	}

	public static List<WidgetOptionDAO> getWidgetOptions(
			Key<?> parentKey) {
		Query<WidgetOptionDAO> q = ofy.query(WidgetOptionDAO.class).ancestor(
				parentKey);

		return q.list();
	}

	public static List<WidgetOptionDAO> getWidgetOptions(String placeId
			) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		

		return getWidgetOptions(placeKey);
	}
	
	public static List<WidgetOptionDAO> getWidgetOptions(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		return getWidgetOptions(widgetKey);
	}

	public static List<Key<WidgetOptionDAO>> getWidgetOptionsKeys(
			Key<?> applicatonOrWidgetKey) {
		Query<WidgetOptionDAO> q = ofy.query(WidgetOptionDAO.class).ancestor(
				applicatonOrWidgetKey);

		return q.listKeys();
	}

	public static List<Key<WidgetOptionDAO>> getWidgetOptionsKeys(
			Key<ApplicationDAO> applicationKey, String widgetId) {
		return getWidgetOptionsKeys(new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId));
	}

	public static List<Key<WidgetOptionDAO>> getWidgetOptionsKeys(
			String placeId, String applicationId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);

		return getWidgetOptionsKeys(applicationKey);
	}

	public static List<Key<WidgetOptionDAO>> getWidgetOptionsKeys(
			String placeId, String applicationId, String widgetId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		Key<WidgetDAO> widgetKey = new Key<WidgetDAO>(applicationKey,
				WidgetDAO.class, widgetId);

		return getWidgetOptionsKeys(widgetKey);
	}

	public static List<WidgetDAO> getWidgets(Key<ApplicationDAO> parentKey) {
		Query<WidgetDAO> q = ofy.query(WidgetDAO.class).ancestor(parentKey);
		return q.list();
	}

	public static List<WidgetDAO> getWidgets(Key<PlaceDAO> placeKey,
			String applicationId) {
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		return getWidgets(applicationKey);
	}

	public static List<WidgetDAO> getWidgets(String placeId,
			String applicationId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		return getWidgets(applicationKey);
	}

	public static List<Key<WidgetDAO>> getWidgetsKeys(
			Key<ApplicationDAO> parentKey) {
		Query<WidgetDAO> q = ofy.query(WidgetDAO.class).ancestor(parentKey);
		return q.listKeys();
	}

	public static List<Key<WidgetDAO>> getWidgetsKeys(Key<PlaceDAO> placeKey,
			String applicationId) {
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		return getWidgetsKeys(applicationKey);
	}

	public static List<Key<WidgetDAO>> getWidgetsKeys(String placeId,
			String applicationId) {
		Key<PlaceDAO> placeKey = new Key<PlaceDAO>(PlaceDAO.class, placeId);
		Key<ApplicationDAO> applicationKey = new Key<ApplicationDAO>(placeKey,
				ApplicationDAO.class, applicationId);
		return getWidgetsKeys(applicationKey);
	}

	
	public static void put(java.lang.Iterable<?> objs) {
		ofy.put(objs);
	}

	public static void put(Object o) {
		ofy.put(o);
	}

}