package org.instantplaces.im.server.resource;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.Dao;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;



public class TestResource extends ServerResource {

	@Override
	public void doInit() {
		Log.get().debug("Test Resource.");
	}
	
	@Get
	public Object doGet() {
		Log.get().debug("Get Test Resource");
		
		Dao.beginTransaction();
		
//		PlaceDAO place = new PlaceDAO("place");
//		ApplicationDAO app = new ApplicationDAO(place, "application");
//		WidgetDAO widget = new WidgetDAO(app, "widget", null, null, null);
//		DAO.putApplication(app);
//		DAO.putPlace(place);
//		DAO.putWidget(widget);
		
		//ArrayList<WidgetOptionDAO> options = DAO.getWidgetOption();
//		for (WidgetOptionDAO option : options) {
//			Log.get().debug("option: "  + option.getWidgetOptionId());
//		}
		
		Dao.commitOrRollbackTransaction();
//		List<Key<PlaceDAO>> placeKeys = DAO.getPlaceKeys();
//		
//		for ( Key<PlaceDAO> key : placeKeys ) {
//			Log.get().debug(key.getName());
//		}
		
		
		return null;
	}

	
}
