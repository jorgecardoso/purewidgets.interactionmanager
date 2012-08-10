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
		ObjectifyService.register(PlaceDaot.class);
		ObjectifyService.register(ReferenceCodeGeneratorDAO.class);
		ObjectifyService.register(ApplicationDaot.class);
		ObjectifyService.register(WidgetDaot.class);
		ObjectifyService.register(WidgetOptionDaot.class);
		ObjectifyService.register(WidgetInputDao.class);
		ObjectifyService.register(ChannelMapDao.class);
	}

	public static void beginTransaction() {
		ofy = ObjectifyService.beginTransaction();
		Log.get().debug("Started transaction: " + ofy.getTxn().getId() );
	}

	public static boolean commitOrRollbackTransaction() {
		Log.get().debug("Commiting transaction: " + ofy.getTxn().getId() );
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

	public static void deleteWidget(Key<ApplicationDaot> applicationKey,
			String widgetId) {
		delete(new Key<WidgetDaot>(applicationKey, WidgetDaot.class, widgetId));
	}

	public static void deleteWidget(String placeId, String applicationId,
			String widgetId) {

		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		delete(widgetKey);
	}

	public static void deleteWidgetInput(Key<?> applicationOrWidgetKey) {

		delete(getWidgetInputsKeys(applicationOrWidgetKey));
	}

	public static void deleteWidgetInput(Key<ApplicationDaot> applicationKey,
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

	public static void deleteWidgetOptions(Key<ApplicationDaot> applicationKey,
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

	public static void deleteWidgets(Key<ApplicationDaot> applicationKey) {
		delete(getWidgetsKeys(applicationKey));
	}

	public static void deleteWidgets(Key<PlaceDaot> placeKey,
			String applicationId) {
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);

		delete(getWidgetsKeys(applicationKey));
	}

	public static void deleteWidgets(String placeId, String applicationId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);

		deleteWidgets(applicationKey);
	}

	public static <T> java.util.Map<Key<T>,T>	get(java.lang.Iterable<? extends Key<? extends T>> keys) {
		return ofy.get(keys);
	}

	public static ApplicationDaot getApplication(
			Key<ApplicationDaot> applicationKey) {
		return ofy.find(applicationKey);
	}

	public static ApplicationDaot getApplication(Key<PlaceDaot> parent,
			String applicationId) {
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(parent,
				ApplicationDaot.class, applicationId);
		return getApplication(applicationKey);
	}

	public static ApplicationDaot getApplication(String placeId,
			String applicationId) {
		Key<PlaceDaot> parent = new Key<PlaceDaot>(PlaceDaot.class, placeId);

		Key<ApplicationDaot> key = new Key<ApplicationDaot>(parent,
				ApplicationDaot.class, applicationId);

		return getApplication(key);
	}

	public static List<ApplicationDaot> getApplications(Key<PlaceDaot> parentKey) {
		Query<ApplicationDaot> q = ofy.query(ApplicationDaot.class).ancestor(
				parentKey);

		return q.list();
	}

	public static List<ApplicationDaot> getApplications(String placeId) {
		return getApplications(new Key<PlaceDaot>(PlaceDaot.class, placeId));
	}

	public static WidgetInputDao getLastWidgetInput(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class)
				.ancestor(widgetKey).order("-timeStamp");

		return q.get();

	}

	public static PlaceDaot getPlace(Key<PlaceDaot> placeKey) {
		return ofy.find(placeKey);
	}

	public static PlaceDaot getPlace(String placeId) {
		return ofy.find(PlaceDaot.class, placeId);
	}

	
	public static ChannelMapDao getChannelMap(String placeId, String applicationId) {
		return getChannelMap(placeId+applicationId);
	}
	
	public static ChannelMapDao getChannelMap(String placeApplicationId) {
		return ofy.find(ChannelMapDao.class, placeApplicationId);
	}
	
	public static List<Key<PlaceDaot>> getPlaceKeys() {
		/*
		 * We can't get all root entities inside a transaction, so don't use the
		 * static ofy
		 */
		Objectify ofy_ = ObjectifyService.begin();
		Query<PlaceDaot> q = ofy_.query(PlaceDaot.class);

		return q.listKeys();

	}

	public static List<Key<ApplicationDaot>> getApplicationKeys() {
		/*
		 * We can't get all root entities inside a transaction, so don't use the
		 * static ofy
		 */
		Objectify ofy_ = ObjectifyService.begin();
		Query<ApplicationDaot> q = ofy_.query(ApplicationDaot.class);

		return q.listKeys();

	}
	
	public static List<PlaceDaot> getPlaces() {
		/*
		 * We can't get all root entities inside a transaction, do don't use the
		 * static ofy
		 */
		Objectify ofy_ = ObjectifyService.begin();
		Query<PlaceDaot> q = ofy_.query(PlaceDaot.class);

		return q.list();

	}
	
	public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(
			Key<PlaceDaot> placeKey) {
		Query<ReferenceCodeGeneratorDAO> q = ofy.query(
				ReferenceCodeGeneratorDAO.class).ancestor(placeKey);

		return q.get();
	}

	public static ReferenceCodeGeneratorDAO getReferenceCodeGenerator(
			String placeId) {
		Query<ReferenceCodeGeneratorDAO> q = ofy.query(
				ReferenceCodeGeneratorDAO.class).ancestor(
				new Key<PlaceDaot>(PlaceDaot.class, placeId));

		return q.get();
	}

	
	public static WidgetDaot getWidget(Key<ApplicationDaot> parentKey,
			String widgetId) {
		return getWidget(new Key<WidgetDaot>(parentKey, WidgetDaot.class,
				widgetId));
	}

	public static WidgetDaot getWidget(Key<WidgetDaot> widgetKey) {
		return ofy.find(widgetKey);
	}

	public static WidgetDaot getWidget(String placeId, String applicationId,
			String widgetId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

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
	
	public static List<WidgetOptionDaot> getWidgetOptionsByReferenceCode(
			String placeId, String referenceCode) {
		
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		
		Log.get().debug("Searching for widget options on place + " + placeId + ", with reference code " + referenceCode);
		Query<WidgetOptionDaot> q = ofy.query(WidgetOptionDaot.class)
				.ancestor(placeKey).filter("referenceCode =", referenceCode.trim());

		return q.list();
	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId) {

		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);

		return getWidgetInputs(applicationKey);

	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId, long from) {

		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);

		return getWidgetInputs(applicationKey, from);

	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId, String widgetId) {

		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		return getWidgetInputs(widgetKey);

	}

	public static List<WidgetInputDao> getWidgetInputs(String placeId,
			String applicationId, String widgetId, long from) {

		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

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
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		Query<WidgetInputDao> q = ofy.query(WidgetInputDao.class).ancestor(
				widgetKey);

		return q.listKeys();
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(String placeId,
			String applicationId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);

		return getWidgetInputsKeys(applicationKey);
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		return getWidgetInputsKeys(widgetKey);
	}

	public static List<Key<WidgetInputDao>> getWidgetInputsKeys(String placeId,
			String applicationId, String widgetId, String widgetOptionId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);
		Key<WidgetOptionDaot> widgetOptionKey = new Key<WidgetOptionDaot>(widgetKey,
				WidgetOptionDaot.class, widgetOptionId);
		

		return getWidgetInputsKeys(widgetOptionKey);
	}
	
	public static WidgetOptionDaot getWidgetOption(Key<WidgetDaot> parentKey,
			String widgetOptionId) {

		return getWidgetOption(new Key<WidgetOptionDaot>(parentKey,
				WidgetOptionDaot.class, widgetOptionId));

	}

	public static WidgetOptionDaot getWidgetOption(
			Key<WidgetOptionDaot> widgetOptionKey) {

		return ofy.find(widgetOptionKey);

	}

	public static WidgetOptionDaot getWidgetOption(String placeId,
			String applicationId, String widgetId, String widgetOptionId) {

		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);
		Key<WidgetOptionDaot> widgetOptionKey = new Key<WidgetOptionDaot>(
				widgetKey, WidgetOptionDaot.class, widgetOptionId);

		return getWidgetOption(widgetOptionKey);
	}

	public static List<WidgetOptionDaot> getWidgetOptions(
			Key<?> parentKey) {
		Query<WidgetOptionDaot> q = ofy.query(WidgetOptionDaot.class).ancestor(
				parentKey);

		return q.list();
	}

	public static List<WidgetOptionDaot> getWidgetOptions(
			Key<ApplicationDaot> applicationKey, String widgetId) {
		return getWidgetOptions(new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId));
	}

	public static List<WidgetOptionDaot> getWidgetOptions(String placeId
			) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		

		return getWidgetOptions(placeKey);
	}
	
	public static List<WidgetOptionDaot> getWidgetOptions(String placeId,
			String applicationId, String widgetId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		return getWidgetOptions(widgetKey);
	}

	public static List<Key<WidgetOptionDaot>> getWidgetOptionsKeys(
			Key<?> applicatonOrWidgetKey) {
		Query<WidgetOptionDaot> q = ofy.query(WidgetOptionDaot.class).ancestor(
				applicatonOrWidgetKey);

		return q.listKeys();
	}

	public static List<Key<WidgetOptionDaot>> getWidgetOptionsKeys(
			Key<ApplicationDaot> applicationKey, String widgetId) {
		return getWidgetOptionsKeys(new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId));
	}

	public static List<Key<WidgetOptionDaot>> getWidgetOptionsKeys(
			String placeId, String applicationId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);

		return getWidgetOptionsKeys(applicationKey);
	}

	public static List<Key<WidgetOptionDaot>> getWidgetOptionsKeys(
			String placeId, String applicationId, String widgetId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		Key<WidgetDaot> widgetKey = new Key<WidgetDaot>(applicationKey,
				WidgetDaot.class, widgetId);

		return getWidgetOptionsKeys(widgetKey);
	}

	public static List<WidgetDaot> getWidgets(Key<ApplicationDaot> parentKey) {
		Query<WidgetDaot> q = ofy.query(WidgetDaot.class).ancestor(parentKey);
		return q.list();
	}

	public static List<WidgetDaot> getWidgets(Key<PlaceDaot> placeKey,
			String applicationId) {
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		return getWidgets(applicationKey);
	}

	public static List<WidgetDaot> getWidgets(String placeId,
			String applicationId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		return getWidgets(applicationKey);
	}

	public static List<Key<WidgetDaot>> getWidgetsKeys(
			Key<ApplicationDaot> parentKey) {
		Query<WidgetDaot> q = ofy.query(WidgetDaot.class).ancestor(parentKey);
		return q.listKeys();
	}

	public static List<Key<WidgetDaot>> getWidgetsKeys(Key<PlaceDaot> placeKey,
			String applicationId) {
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		return getWidgetsKeys(applicationKey);
	}

	public static List<Key<WidgetDaot>> getWidgetsKeys(String placeId,
			String applicationId) {
		Key<PlaceDaot> placeKey = new Key<PlaceDaot>(PlaceDaot.class, placeId);
		Key<ApplicationDaot> applicationKey = new Key<ApplicationDaot>(placeKey,
				ApplicationDaot.class, applicationId);
		return getWidgetsKeys(applicationKey);
	}

	
	public static void put(java.lang.Iterable<?> objs) {
		ofy.put(objs);
	}

	public static void put(Object o) {
		ofy.put(o);
	}

}