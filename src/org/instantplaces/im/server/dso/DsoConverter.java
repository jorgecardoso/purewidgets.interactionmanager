package org.instantplaces.im.server.dso;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.WidgetOptionREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.shared.WidgetOption;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DsoConverter {

	public static WidgetOptionDSO widgetOptionDSOfromRest(WidgetDSO parent, WidgetOptionREST widgetOptionREST) {
		WidgetOptionDSO woDSO = new WidgetOptionDSO(parent, widgetOptionREST.getWidgetOptionId());

		woDSO.setSuggestedReferenceCode(widgetOptionREST.getSuggestedReferenceCode());
		woDSO.setReferenceCode(widgetOptionREST.getReferenceCode());
		woDSO.setLongDescripton(widgetOptionREST.getLongDescription());
		woDSO.setShortDescription(widgetOptionREST.getShortDescription());
		
		return woDSO;
	}

	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetREST
	 * @return
	 */
	public static WidgetDSO widgetDSOfromRest(ApplicationDSO parent, WidgetREST widgetREST) {
		WidgetDSO wDSO = new WidgetDSO(parent, widgetREST.getWidgetId(), widgetREST.getControlType(), widgetREST.getShortDescription(), widgetREST.getLongDescription());
		
		wDSO.setVolatileWidget(widgetREST.isVolatileWidget());
		
		for (WidgetOptionREST woRest : widgetREST.getWidgetOptions()) {
			wDSO.addWidgetOption(DsoConverter.widgetOptionDSOfromRest(wDSO, woRest));
		}
		
		return wDSO;
	}

}
