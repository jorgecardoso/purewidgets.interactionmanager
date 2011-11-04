/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dso.ApplicationDSO;
import org.instantplaces.im.server.dso.WidgetDSO;
import org.instantplaces.im.server.dso.WidgetInputDSO;
import org.instantplaces.im.server.dso.WidgetOptionDSO;

/**
 * @author "Jorge C. S. Cardoso"
 *
 */
public class RestConverter {

	public static WidgetArrayListREST widgetArrayListFromDso(ArrayList<WidgetDSO> widgetDSOList) {
		
		ArrayList<WidgetREST> widgetListREST = new ArrayList<WidgetREST>();
		for ( WidgetDSO wDSO : widgetDSOList ) {
			WidgetREST wREST = RestConverter.widgetRestFromDso(wDSO);
			widgetListREST.add(wREST);
		}
		
		WidgetArrayListREST widgetArrayList= new WidgetArrayListREST();
		widgetArrayList.widgets = widgetListREST;
		return widgetArrayList;
	}

	/**
	 * Converts a WidgetDSO object to a WidgetREST object.
	 * 
	 * @param widgetDSO
	 * @return
	 */
	public static WidgetREST widgetRestFromDso(WidgetDSO widgetDSO) {
		WidgetREST  w = new WidgetREST();
	
		if ( null != widgetDSO.getWidgetOptions() ) {
			for (WidgetOptionDSO option : widgetDSO.getWidgetOptions()) {
				w.addWidgetOption( RestConverter.widgetOptionFromDSO(option) );
			}
		}
		
		w.setPlaceId(widgetDSO.getPlaceId());
		w.setApplicationId(widgetDSO.getApplicationId());
		w.setWidgetId(widgetDSO.getWidgetId());
		w.setControlType(widgetDSO.getControlType());
		w.setVolatileWidget(widgetDSO.isVolatileWidget());
		w.setShortDescription(widgetDSO.getShortDescription());
		w.setLongDescription(widgetDSO.getLongDescription());
		return w; 
	}

	/**
	 * Converts a WidgetOptionDSO object to a WidgetOptionREST object
	 * 
	 * @param widgetOptionDSO
	 * @return
	 */
	public static WidgetOptionREST widgetOptionFromDSO(WidgetOptionDSO widgetOptionDSO) {
		WidgetOptionREST woREST = new WidgetOptionREST();
		woREST.setWidgetOptionId(widgetOptionDSO.getWidgetOptionId());
		woREST.setReferenceCode(widgetOptionDSO.getReferenceCode());
		woREST.setSuggestedReferenceCode(widgetOptionDSO.getSuggestedReferenceCode());
		woREST.setLongDescription(widgetOptionDSO.getLongDescripton());
		woREST.setShortDescription(widgetOptionDSO.getShortDescription());
		
		
		return woREST;
	}

	/**
	 * Converts a ApplicationDSO object to a ApplicationREST object.
	 * 
	 * @param widgetDSO
	 * @return
	 */
	public static ApplicationREST applicationFromDSO(ApplicationDSO applicationDSO) {
		Log.get().debug("Converting to REST " + applicationDSO.toString());
		ApplicationREST  a = new ApplicationREST();
	
		a.setPlaceId( applicationDSO.getPlaceId() );
		a.setApplicationId( applicationDSO.getApplicationId() );
		
		
		/*for ( WidgetDSO widget : applicationDSO.getWidgets() ) {
			a.addWidget( widgetRestFromDso( widget ));
		}*/
		
		a.setLastRequestTimestamp(applicationDSO.getLastRequestTimestamp());
		
		Log.get().debug("Converted: " + a.toString());
		return a; 
	}

	public static ApplicationArrayListREST applicationArrayListFromDSO(ArrayList<ApplicationDSO> applicationDSOList) {
		
		ArrayList<ApplicationREST> applicationListREST = new ArrayList<ApplicationREST>();
		for ( ApplicationDSO aDSO : applicationDSOList ) {
			ApplicationREST aREST = applicationFromDSO(aDSO);
			applicationListREST.add(aREST);
		}
		
		ApplicationArrayListREST applicationArrayList= new ApplicationArrayListREST();
		applicationArrayList.applications = applicationListREST;
		return applicationArrayList;
	}
	
	public static WidgetInputArrayListREST widgetInputArrayListFromDso(ArrayList<WidgetInputDSO> widgetInputDSOList) {
		
		ArrayList<WidgetInputREST> widgetListREST = new ArrayList<WidgetInputREST>();
		for ( WidgetInputDSO aDSO : widgetInputDSOList ) {
			WidgetInputREST aREST = widgetInputFromDSO(aDSO);
			widgetListREST.add(aREST);
		}
		
		WidgetInputArrayListREST widgetInputArrayList= new WidgetInputArrayListREST();
		widgetInputArrayList.inputs = widgetListREST;
		return widgetInputArrayList;
	}


	public static WidgetInputREST widgetInputFromDSO(WidgetInputDSO wiDso) {
		
		
		WidgetInputREST  w = new WidgetInputREST();
	
		
		w.setWidgetOptionId(wiDso.getWidgetOptionId());
		w.setWidgetId(wiDso.getWidgetId());
		
		w.setParameters(wiDso.getParameters());
		w.setPersona(wiDso.getPersona());
		w.setTimeStamp(""+wiDso.getTimeStamp());
		w.setInputMechanism(wiDso.getInputMechanism());
		w.setDelivered(wiDso.isDelivered());
		
	
		return w; 
	}

}
