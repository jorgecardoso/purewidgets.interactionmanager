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

public class Dao extends DAOBase {
	private static Objectify ofy;

	static {
		ObjectifyService.register(PlaceDao.class);
		ObjectifyService.register(ReferenceCodeGeneratorDAO.class);
		ObjectifyService.register(ApplicationDao.class);
		ObjectifyService.register(WidgetDao.class);
		ObjectifyService.register(WidgetOptionDao.class);
		ObjectifyService.register(WidgetInputDao.class);

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

	public static void delete(java.lang.Iterable<?> keysOrEntities) {
		ofy.delete(keysOrEntities);
	}
	
	
	public static void delete(Object o) {
		ofy.delete(o);
	}

	public static void deleteWidget(Key<ApplicationDao> applicationKey,
			String widgetId) {
		delete(new Key<WidgetDao>(applicationKey, WidgetDao.class, widgetId));
	}

	public static void deleteWidget(String placeId, String applicationId,
			String widgetId) {

		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		delete(widgetKey);
	}

	public static void deleteWidgetInput(Key<?> applicationOrWidgetKey) {

		delete(getWidgetInputsKeys(applicationOrWidgetKey));
	}

	public static void deleteWidgetInput(Key<ApplicationDao> applicationKey,
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

	public static void deleteWidgetOptions(Key<ApplicationDao> applicationKey,
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

	public static void deleteWidgets(Key<ApplicationDao> applicationKey) {
		delete(getWidgetsKeys(applicationKey));
	}

	public static void deleteWidgets(Key<PlaceDao> placeKey,
			String applicationId) {
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);

		delete(getWidgetsKeys(applicationKey));
	}

	public static void deleteWidgets(String placeId, String applicationId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);

		deleteWidgets(applicationKey);
	}

	public static <T> java.util.Map<Key<T>,T>	get(java.lang.Iterable<? extends Key<? extends T>> keys) {
		return ofy.get(keys);
	}

	public static ApplicationDao getApplication(
			Key<ApplicationDao> applicationKey) {
		return ofy.find(applicationKey);
	}

	public static ApplicationDao getApplication(Key<PlaceDao> parent,
			String applicationId) {
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(parent,
				ApplicationDao.class, applicationId);
		return getApplication(applicationKey);
	}

	public static ApplicationDao getApplication(String placeId,
			String applicationId) {
		Key<PlaceDao> parent = new Key<PlaceDao>(PlaceDao.class, placeId);

		Key<ApplicationDao> key = new Key<ApplicationDao>(parent,
				ApplicationDao.class, applicationId);

		return getApplication(key);
	}

	public static List<ApplicationDao> getApplications(Key<PlaceDao> parentKey) {
		Query<ApplicationDao> q = ofy.query(ApplicationDao.class).ancestor(
				parentKey);

		return q.list();
	}

	public static List<ApplicationDao> getApplications(String placeId) {
		return getApplications(new Key<PlaceDao>(PlaceDao.class, placeId));
	}

	public static WidgetInputDao getLastWidgetInput(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class)
				.ancestor(widgetKey).order("-timeStamp");

		return q.get();

	}

	public static PlaceDao getPlace(Key<PlaceDao> placeKey) {
		return ofy.find(placeKey);
	}

	public static PlaceDao getPlace(String placeId) {
		return ofy.find(PlaceDao.class, placeId);
	}

	public static List<Key<PlaceDao>> getPlaceKeys() {
		/*
		 * We can't get all root entities inside a transaction, do don't use the
		 * static ofy
		 */
		Objectify ofy_ = ObjectifyService.begin();
		Query<PlaceDao> q = ofy_.query(PlaceDao.class);

		return q.listKeys();

	}

	public static List<PlaceDao> getPlaces() {
		/*
		 * We can't get all root entities inside a transaction, do don't use the
		 * static ofy
		 */
		Objectify ofy_ = ObjectifyService.begin();
		Query<PlaceDao> q = ofy_.query(PlaceDao.class);

		return q.list();

	}
	
	public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(
			Key<PlaceDao> placeKey) {
		Query<ReferenceCodeGeneratorDAO> q = ofy.query(
				ReferenceCodeGeneratorDAO.class).ancestor(placeKey);

		return q.get();
	}

	public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(
			String placeId) {
		Query<ReferenceCodeGeneratorDAO> q = ofy.query(
				ReferenceCodeGeneratorDAO.class).ancestor(
				new Key<PlaceDao>(PlaceDao.class, placeId));

		return q.get();
	}

	
	public static WidgetDao getWidget(Key<ApplicationDao> parentKey,
			String widgetId) {
		return getWidget(new Key<WidgetDao>(parentKey, WidgetDao.class,
				widgetId));
	}

	public static WidgetDao getWidget(Key<WidgetDao> widgetKey) {
		return ofy.find(widgetKey);
	}

	public static WidgetDao getWidget(String placeId, String applicationId,
			String widgetId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		return getWidget(widgetKey);
	}

	public static List<WidgetInputDao> getWidgetInputs(
			Key<?> widgetOrApplicationKey) {
		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class).ancestor(
				widgetOrApplicationKey);

		return q.list();
	}

	public static List<WidgetInputDao> getWidgetInputs(
			Key<?> widgetOrApplicationKey, long from) {
		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class)
				.ancestor(widgetOrApplicationKey).filter("timeStamp > ", from);

		return q.list();
	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId) {

		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);

		return getWidgetInputs(applicationKey);

	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId, long from) {

		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);

		return getWidgetInputs(applicationKey, from);

	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId, String widgetId) {

		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		return getWidgetInputs(widgetKey);

	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId, String widgetId, long from) {

		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		return getWidgetInputs(widgetKey, from);

	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(
			Key<?> widgetOrApplicationKey) {
		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class).ancestor(
				widgetOrApplicationKey);

		return q.listKeys();
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(
			Key<?> applicationKey, String widgetId) {
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class).ancestor(
				widgetKey);

		return q.listKeys();
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(String placeId,
			String applicationId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);

		return getWidgetInputsKeys(applicationKey);
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		return getWidgetInputsKeys(widgetKey);
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(String placeId,
			String applicationId, String widgetId, String widgetOptionId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);
		Key<WidgetOptionDao> widgetOptionKey = new Key<WidgetOptionDao>(widgetKey,
				WidgetOptionDao.class, widgetOptionId);
		

		return getWidgetInputsKeys(widgetOptionKey);
	}
	
	public static WidgetOptionDao getWidgetOption(Key<WidgetDao> parentKey,
			String widgetOptionId) {

		return getWidgetOption(new Key<WidgetOptionDao>(parentKey,
				WidgetOptionDao.class, widgetOptionId));

	}

	public static WidgetOptionDao getWidgetOption(
			Key<WidgetOptionDao> widgetOptionKey) {

		return ofy.find(widgetOptionKey);

	}

	public static WidgetOptionDao getWidgetOption(String placeId,
			String applicationId, String widgetId, String widgetOptionId) {

		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);
		Key<WidgetOptionDao> widgetOptionKey = new Key<WidgetOptionDao>(
				widgetKey, WidgetOptionDao.class, widgetOptionId);

		return getWidgetOption(widgetOptionKey);
	}

	public static List<WidgetOptionDao> getWidgetOptions(
			Key<?> parentKey) {
		Query<WidgetOptionDao> q = ofy.query(WidgetOptionDao.class).ancestor(
				parentKey);

		return q.list();
	}

	public static List<WidgetOptionDao> getWidgetOptions(
			Key<ApplicationDao> applicationKey, String widgetId) {
		return getWidgetOptions(new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId));
	}

	public static List<WidgetOptionDao> getWidgetOptions(String placeId
			) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		

		return getWidgetOptions(placeKey);
	}
	
	public static List<WidgetOptionDao> getWidgetOptions(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		return getWidgetOptions(widgetKey);
	}

	public static List<Key<WidgetOptionDao>> getWidgetOptionsKeys(
			Key<?> applicatonOrWidgetKey) {
		Query<WidgetOptionDao> q = ofy.query(WidgetOptionDao.class).ancestor(
				applicatonOrWidgetKey);

		return q.listKeys();
	}

	public static List<Key<WidgetOptionDao>> getWidgetOptionsKeys(
			Key<ApplicationDao> applicationKey, String widgetId) {
		return getWidgetOptionsKeys(new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId));
	}

	public static List<Key<WidgetOptionDao>> getWidgetOptionsKeys(
			String placeId, String applicationId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);

		return getWidgetOptionsKeys(applicationKey);
	}

	public static List<Key<WidgetOptionDao>> getWidgetOptionsKeys(
			String placeId, String applicationId, String widgetId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		Key<WidgetDao> widgetKey = new Key<WidgetDao>(applicationKey,
				WidgetDao.class, widgetId);

		return getWidgetOptionsKeys(widgetKey);
	}

	public static List<WidgetDao> getWidgets(Key<ApplicationDao> parentKey) {
		Query<WidgetDao> q = ofy.query(WidgetDao.class).ancestor(parentKey);
		return q.list();
	}

	public static List<WidgetDao> getWidgets(Key<PlaceDao> placeKey,
			String applicationId) {
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		return getWidgets(applicationKey);
	}

	public static List<WidgetDao> getWidgets(String placeId,
			String applicationId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		return getWidgets(applicationKey);
	}

	public static List<Key<WidgetDao>> getWidgetsKeys(
			Key<ApplicationDao> parentKey) {
		Query<WidgetDao> q = ofy.query(WidgetDao.class).ancestor(parentKey);
		return q.listKeys();
	}

	public static List<Key<WidgetDao>> getWidgetsKeys(Key<PlaceDao> placeKey,
			String applicationId) {
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		return getWidgetsKeys(applicationKey);
	}

	public static List<Key<WidgetDao>> getWidgetsKeys(String placeId,
			String applicationId) {
		Key<PlaceDao> placeKey = new Key<PlaceDao>(PlaceDao.class, placeId);
		Key<ApplicationDao> applicationKey = new Key<ApplicationDao>(placeKey,
				ApplicationDao.class, applicationId);
		return getWidgetsKeys(applicationKey);
	}

	
	public static void put(java.lang.Iterable<?> objs) {
		ofy.put(objs);
	}

	public static void put(Object o) {
		ofy.put(o);
	}

}