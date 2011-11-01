package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;
import org.instantplaces.im.server.rest.WidgetArrayListREST;
import org.instantplaces.im.server.rest.WidgetOptionREST;
import org.instantplaces.im.server.rest.WidgetREST;
import org.instantplaces.im.shared.WidgetOption;



import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetDSO {
	
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	
	@Persistent
	private ApplicationDSO application;
	
	@Persistent 
	private String placeId;
	
	@Persistent
	private String applicationId;
	
	@Persistent
	private String widgetId;
	
	@Persistent
	private String controlType;
	
	@Persistent
	private boolean volatileWidget;
	
	/**
	 * A short description (label) for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 *
	 */
	@Persistent
	private String shortDescription;
	
	/**
	 * A long description for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 */
	@Persistent
	private String longDescription;
	
	
	@Persistent(mappedBy = "widget")
	private ArrayList <WidgetOptionDSO> options; 

	
	public WidgetDSO() {
		this(null, null, null);
	}

	
	public WidgetDSO(String id, ApplicationDSO app, ArrayList<WidgetOptionDSO>options) {
		this.widgetId = id;
		this.application = app;
		
		if (options != null) {
			this.options = options;
		} else {
			this.options = new ArrayList<WidgetOptionDSO>();
		}
			
	}
	

	public void setApplication(	ApplicationDSO app) {
		this.application = app;
		this.applicationId = app.getApplicationId();
		this.placeId = app.getPlaceId();
	}


	public 	ApplicationDSO getApplication() {
		return this.application;
	}



	public void setWidgetId(String id) {
		this.widgetId = id;
		
	}



	public String getWidgetId() {
		return this.widgetId;
	}



	public void addWidgetOption(WidgetOptionDSO option) {
		if (!this.options.contains(option)) {
			this.options.add(option);
		}
	}


	public ArrayList<WidgetOptionDSO> getWidgetOptions() {
		return this.options;//.toArray(new WidgetOptionDSO[0]);
	}
/*
	public ArrayList<WidgetOptionDSO> getWidgetOptionsAsArrayList() {
		return this.options;
	}
	*/
	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	
	/**
	 * Converts a WidgetREST object to a WidgetDSO object.
	 * 
	 * @param widgetREST
	 * @return
	 */
	public static WidgetDSO fromREST(WidgetREST widgetREST) {
		WidgetDSO wDSO = new WidgetDSO();
		Log.get().debug("Converting WidgetREST to DSO");
		wDSO.setWidgetId(widgetREST.getWidgetId());
		wDSO.setControlType(widgetREST.getControlType());
		wDSO.setVolatileWidget(widgetREST.isVolatileWidget());
		wDSO.setShortDescription(widgetREST.getShortDescription());
		wDSO.setLongDescription(widgetREST.getLongDescription());
		wDSO.setApplicationId(widgetREST.getApplicationId());
		wDSO.setPlaceId(widgetREST.getPlaceId());
		
		for (WidgetOption wo : widgetREST.getWidgetOptions()) {
			
			WidgetOptionREST woREST = (WidgetOptionREST)wo;
			
			wDSO.addWidgetOption(WidgetOptionDSO.fromREST(woREST));
		}
		
		return wDSO;
	}

	
	@Override
	public boolean equals(Object app) {
		if ( !(app instanceof WidgetDSO) ) {
			return false;
		}
		return ((WidgetDSO) app).getKey().equals(this.key);
	} 	
	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Widget(id: ").append(this.widgetId).append("; options: ");
		if ( this.options != null ) {
			for (WidgetOptionDSO wo : this.options) {
				sb.append(wo.toString()).append(" ");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	
	public static ArrayList<WidgetDSO> getWidgetsFromDSO( PersistenceManager pm, String placeId, String applicationId) {
		Log.get().debug("Fetching widgets for Place(" + placeId + "), Application("+ applicationId + ") from Data Store.");
		ApplicationDSO application = ApplicationDSO.getApplicationDSO(pm, placeId, applicationId);
		if (application == null) {
			Log.get().debug("Application not found.");
			return null;
		}
		if (application.getWidgets() == null) {
			Log.get().debug("Found 0 widgets.");
		} else {
			Log.get().debug("Found " + application.getWidgets().size() + " widgets.");
		}
		return application.getWidgets();
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
	
	public void copySuggestedReferenceCodesToReferenceCodes() {
		//Log.get().debug(this.toString());
		for (WidgetOptionDSO option : this.options) {
			option.setReferenceCode(option.getSuggestedReferenceCode());
		}
	//	Log.get().debug(this.toString());
	}
	
	/**
	 * Merges this WidgetDSO with the information from that WidgetDSO.
	 * 
	 * @param that
	 */
	public void mergeWith(WidgetDSO that) {
		Log.get().debug("Merging " + this.toString() + " with " + that.toString());
		//right now only merging options. the widget itself has no other
		// information that can be merged
		
		// TODO: if client changed the suggested ref code for an option should we 
		// update it and try to generate a new ref code?
		
		/*
		 * Check options that have been deleted in that and... delete them in this 
		 */
		Iterator<WidgetOptionDSO> it = this.options.iterator();
		while (it.hasNext()) {
			WidgetOptionDSO next = it.next();
			if (!that.getWidgetOptions().contains(next)) {
				Log.get().debug("Deleting unused option " + it.toString());
				it.remove();	
			} 
		}
		
		/*
		 * Add new options to this
		 */
		it = that.getWidgetOptions().iterator();
		while (it.hasNext()) {
			WidgetOptionDSO next = it.next();
			if (!this.options.contains(next)) {
				Log.get().debug("Adding to new option " + next.toString());
				this.addWidgetOption(next);	
			} 
		}
		
	}

	public void recycleReferenceCodes() {
		//ReferenceCodeGenerator rcg = ReferenceCodeGenerator.getFromDSO(pm);
		for (WidgetOptionDSO option : this.options) {
			this.application.getPlace().getCodeGenerator().recycleCode(option.getReferenceCode());
				
			Log.get().debug("Recycling reference code: " + option.toString());
		
		
		}
	}

	public void assignReferenceCodes() {
		//RCGF r = new RCGF();
		//ReferenceCodeGenerator g = r.get();~
		ReferenceCodeGenerator g = this.application.getPlace().getCodeGenerator();//ReferenceCodeGenerator.getFromDSO(pm);
		//Log.get().debug(this.toString());
		String code;
		for (WidgetOptionDSO option : this.options) {
			if ( null == option.getReferenceCode() ) {
				code = g.getNextCodeAsString();
				Log.get().debug("Assigning reference code: " + code +" to " + this.toString());
				option.setReferenceCode(code);
			}
		}
		
		//Log.get().debug("Generator code: " + g.getNextCode());
		
	}


	public boolean isVolatileWidget() {
		return volatileWidget;
	}


	public void setVolatileWidget(boolean volatileWidget) {
		this.volatileWidget = volatileWidget;
	}
	
	
	/**
	 * Converts a WidgetArrayListREST object to an ArrayList of WidgetDSO.
	 * 
	 * @param widgetArrayListREST
	 * @return
	 */
	public static ArrayList<WidgetDSO> fromREST(WidgetArrayListREST widgetArrayListREST) {
		
		ArrayList<WidgetDSO> widgetListDSO = new ArrayList<WidgetDSO>();
		
		Log.get().debug("Converting WidgetArrayListREST to ArrayList of WidgetDSO");
		
		for ( WidgetREST wREST : widgetArrayListREST.widgets ) {
			
//			Log.get().debug("Converting WidgetREST to DSO");
//			
//			WidgetDSO wDSO = new WidgetDSO();
//			wDSO.setWidgetId(wREST.getWidgetId());
//			wDSO.setVolatileWidget(wREST.isVolatileWidget());
//			for (WidgetOption wo : wREST.getWidgetOptions()) {
//				
//				WidgetOptionREST woREST = (WidgetOptionREST)wo;
//				
//				wDSO.addWidgetOption(WidgetOptionDSO.fromREST(woREST));
//			}
			
			widgetListDSO.add(WidgetDSO.fromREST(wREST));
		}
		
		
		return widgetListDSO;
	}


	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}


	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}


	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}


	/**
	 * @return the controlType
	 */
	public String getControlType() {
		return controlType;
	}


	/**
	 * @param controlType the controlType to set
	 */
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}


	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}


	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}


	/**
	 * @return the placeId
	 */
	public String getPlaceId() {
		return placeId;
	}


	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
}
