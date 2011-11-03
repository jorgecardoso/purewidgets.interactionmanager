package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.Iterator;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;
import org.instantplaces.im.server.rest.WidgetArrayListREST;



import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable="true")
public class WidgetDSO {
	
	
	@PrimaryKey
    @Persistent//(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
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
	
	@NotPersistent
	private ArrayList<WidgetOptionDSO> widgetOptions;
	
	
	@NotPersistent
	private ApplicationDSO application;
	
	public WidgetDSO(ApplicationDSO application) {
		this(application, null, null, null, null);
	}

	
	public WidgetDSO(ApplicationDSO application, String widgetId, String controlType, String shortDescription, String longDescription) {
		this.widgetId = widgetId;
		this.controlType = controlType;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.volatileWidget = true;
		
		this.setApplication(application);
	}

	
	public void setApplication(ApplicationDSO a) {
		this.application = a;	
		if ( null != a ) {
			this.key = KeyFactory.createKey(a.getKey(), WidgetDSO.class.getSimpleName(),  this.widgetId);
			this.applicationId = a.getApplicationId();
			this.placeId = a.getPlaceId();
		}
	}
	
	public void setWidgetId(String id) {
		this.widgetId = id;
		
	}


	public String getWidgetId() {
		return this.widgetId;
	}


	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void addWidgetOption(WidgetOptionDSO widgetOption) {
		if ( null == this.widgetOptions ) {
			this.widgetOptions = new ArrayList<WidgetOptionDSO>();
		}
		this.widgetOptions.add(widgetOption);
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
		sb.append("Widget(id: ").append(this.widgetId).append("; ");
		
		return sb.toString();
	}

	
	
	
	
	
	
	
	
	
/*	public void copySuggestedReferenceCodesToReferenceCodes() {
		//Log.get().debug(this.toString());
		for (WidgetOptionDSO option : this.options) {
			option.setReferenceCode(option.getSuggestedReferenceCode());
		}
	//	Log.get().debug(this.toString());
	}
	*/
	
	public ArrayList<WidgetOptionDSO> mergeOptionsToDelete(WidgetDSO that) {
		ArrayList<WidgetOptionDSO> toDelete = new ArrayList<WidgetOptionDSO>();
		/*
		 * Check options that have been deleted in that and... delete them in this 
		 */
		if ( null != this.widgetOptions ) {
			Iterator<WidgetOptionDSO> it = this.widgetOptions.iterator();
			while (it.hasNext()) {
				WidgetOptionDSO next = it.next();
				if (!that.getWidgetOptions().contains(next)) {
					Log.get().debug("Deleting unused option " + it.toString());
					it.remove();	
					toDelete.add(next);
				} 
			}
		}
		return toDelete;
	}
	
	public ArrayList<WidgetOptionDSO> mergeOptionsToAdd(WidgetDSO that) {
		ArrayList<WidgetOptionDSO> toAdd = new ArrayList<WidgetOptionDSO>();
		if ( null == this.widgetOptions ) {
			this.widgetOptions = new ArrayList<WidgetOptionDSO>();
			this.widgetOptions.addAll(that.getWidgetOptions());
			toAdd.addAll(that.getWidgetOptions());
		} else {
			Iterator<WidgetOptionDSO> it = that.getWidgetOptions().iterator();
			while (it.hasNext()) {
				WidgetOptionDSO next = it.next();
				if (!this.widgetOptions.contains(next)) {
					Log.get().debug("Adding to new option " + next.toString());
					this.widgetOptions.add(next);	
					toAdd.add(next);
				} 
			}
		}
		
		return toAdd;
	}
	
	
//	/**
//	 * Merges this WidgetDSO with the information from that WidgetDSO.
//	 * 
//	 * @param that
//	 */
//	public void mergeWith(WidgetDSO that) {
//		Log.get().debug("Merging " + this.toString() + " with " + that.toString());
//		//right now only merging options. the widget itself has no other
//		// information that can be merged
//		
//		// TODO: if client changed the suggested ref code for an option should we 
//		// update it and try to generate a new ref code?
//		
//		/*
//		 * Check options that have been deleted in that and... delete them in this 
//		 */
//		Iterator<WidgetOptionDSO> it = this.widgetOptions.iterator();
//		while (it.hasNext()) {
//			WidgetOptionDSO next = it.next();
//			if (!that.getWidgetOptions().contains(next)) {
//				Log.get().debug("Deleting unused option " + it.toString());
//				it.remove();	
//			} 
//		}
//		
//		/*
//		 * Add new options to this
//		 */
//		it = that.getWidgetOptions().iterator();
//		while (it.hasNext()) {
//			WidgetOptionDSO next = it.next();
//			if (!this.options.contains(next)) {
//				Log.get().debug("Adding to new option " + next.toString());
//				this.addWidgetOption(next);	
//			} 
//		}
//		
//	}
//


	public void assignReferenceCodes(ReferenceCodeGenerator rcg) {
		
		String code;
		for (WidgetOptionDSO option : this.widgetOptions) {
			if ( null == option.getReferenceCode() ) {
				code = rcg.getNextCodeAsString();
				Log.get().debug("Assigning reference code: " + code +" to " + this.toString());
				option.setReferenceCode(code);
			}
		}
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
//	public static ArrayList<WidgetDSO> fromREST(WidgetArrayListREST widgetArrayListREST) {
//		
//		ArrayList<WidgetDSO> widgetListDSO = new ArrayList<WidgetDSO>();
//		
//		Log.get().debug("Converting WidgetArrayListREST to ArrayList of WidgetDSO");
//		
//		for ( WidgetREST wREST : widgetArrayListREST.widgets ) {
//		
//			widgetListDSO.add(WidgetDSO.fromREST(wREST));
//		}
//		
//		
//		return widgetListDSO;
//	}


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


	/**
	 * @return the widgetOptions
	 */
	public ArrayList<WidgetOptionDSO> getWidgetOptions() {
		return widgetOptions;
	}


	/**
	 * @param widgetOptions the widgetOptions to set
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionDSO> widgetOptions) {
		this.widgetOptions = widgetOptions;
	}
}
