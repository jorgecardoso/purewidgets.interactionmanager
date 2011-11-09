package org.instantplaces.im.server.dao;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.Id;

import org.instantplaces.im.server.Log;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

public class WidgetDAO {

	@Parent
	private Key<ApplicationDAO> applicationKey;
	
	@Id
	private String widgetId;
	
	@Unindexed
	private String controlType;
	
	@Unindexed
	private boolean volatileWidget;
	
	
	/**
	 * A short description (label) for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 *
	 */
	@Unindexed
	private String shortDescription;
	
	/**
	 * A long description for the widget. The descriptions
	 * can be used to generate a more informative GUI by other system applications.
	 */
	@Unindexed
	private String longDescription;
	
	
	@NotSaved
	private ArrayList<WidgetOptionDAO> widgetOptions;
	
	
	@NotSaved
	private ApplicationDAO application;
	
	private WidgetDAO() {
		this(null, null, null, null, null);
	}
	
	public WidgetDAO(ApplicationDAO application) {
		this(application, null, null, null, null);
	}

	
	public WidgetDAO(ApplicationDAO application, String widgetId, String controlType, String shortDescription, String longDescription) {
		this.widgetId = widgetId;
		this.controlType = controlType;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.volatileWidget = true;
		
		this.setApplication(application);
	}

	
	public void setApplication(ApplicationDAO a) {
		this.application = a;	
		if ( null != a ) {
			this.application = a;
			
			this.applicationKey = new Key<ApplicationDAO>(a.getPlaceKey(), ApplicationDAO.class, a.getApplicationId());		
		}
	}
	

	
	public void setWidgetId(String id) {
		this.widgetId = id;
		
	}


	public String getWidgetId() {
		return this.widgetId;
	}



	public void addWidgetOption(WidgetOptionDAO widgetOption) {
		if ( null == this.widgetOptions ) {
			this.widgetOptions = new ArrayList<WidgetOptionDAO>();
		}
		this.widgetOptions.add(widgetOption);
	}
	
//	@Override
//	public boolean equals(Object app) {
//		if ( !(app instanceof WidgetDSO) ) {
//			return false;
//		}
//		return ((WidgetDSO) app).getKey().equals(this.key);
//	} 	
//	

	@Override
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
	
	public ArrayList<WidgetOptionDAO> mergeOptionsToDelete(WidgetDAO that) {
		ArrayList<WidgetOptionDAO> toDelete = new ArrayList<WidgetOptionDAO>();
		/*
		 * Check options that have been deleted in that and... delete them in this 
		 */
		if ( null != this.widgetOptions ) {
			Iterator<WidgetOptionDAO> it = this.widgetOptions.iterator();
			while (it.hasNext()) {
				WidgetOptionDAO next = it.next();
				if (!that.getWidgetOptions().contains(next)) {
					Log.get().debug("Deleting unused option " + it.toString());
					it.remove();	
					toDelete.add(next);
				} 
			}
		}
		return toDelete;
	}
	
	public ArrayList<WidgetOptionDAO> mergeOptionsToAdd(WidgetDAO that) {
		ArrayList<WidgetOptionDAO> toAdd = new ArrayList<WidgetOptionDAO>();
		if ( null == this.widgetOptions ) {
			this.widgetOptions = new ArrayList<WidgetOptionDAO>();
			this.widgetOptions.addAll(that.getWidgetOptions());
			toAdd.addAll(that.getWidgetOptions());
		} else {
			Iterator<WidgetOptionDAO> it = that.getWidgetOptions().iterator();
			while (it.hasNext()) {
				WidgetOptionDAO next = it.next();
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


	public void assignReferenceCodes(ReferenceCodeGeneratorDAO rcg) {
		
		String code;
		for (WidgetOptionDAO option : this.widgetOptions) {
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
	 * @return the widgetOptions
	 */
	public ArrayList<WidgetOptionDAO> getWidgetOptions() {
		return widgetOptions;
	}


	/**
	 * @param widgetOptions the widgetOptions to set
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionDAO> widgetOptions) {
		this.widgetOptions = widgetOptions;
	}


	/**
	 * @return the applicationKey
	 */
	public Key<ApplicationDAO> getApplicationKey() {
		return applicationKey;
	}


	/**
	 * @param applicationKey the applicationKey to set
	 */
	public void setApplicationKey(Key<ApplicationDAO> applicationKey) {
		this.applicationKey = applicationKey;
	}
}
