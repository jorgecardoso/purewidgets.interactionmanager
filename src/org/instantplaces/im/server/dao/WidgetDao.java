package org.instantplaces.im.server.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


import javax.persistence.Embedded;
import javax.persistence.Id;

import org.instantplaces.im.server.Log;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class WidgetDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@NotSaved
//	private ApplicationDao application;

	@Parent
	private Key<ApplicationDao> applicationKey;

	@Unindexed
	private String controlType;

	/**
	 * A long description for the widget. The descriptions can be used to
	 * generate a more informative GUI by other system applications.
	 */
	@Unindexed
	private String longDescription;

	/**
	 * A short description (label) for the widget. The descriptions can be used
	 * to generate a more informative GUI by other system applications.
	 * 
	 */
	@Unindexed
	private String shortDescription;
	

	@Id
	private String widgetId;
	
	@Unindexed
	@Embedded
	private ArrayList<WidgetParameterDao> widgetParameters;
	
	@NotSaved
	private ArrayList<WidgetOptionDao> widgetOptions;

	@NotSaved
	private boolean changedFlag;
	
	public WidgetDao(Key<ApplicationDao> applicationKey) {
		this(applicationKey, null, null, null, null);
	}

	public WidgetDao(Key<ApplicationDao> applicationKey, String widgetId, String controlType,
			String shortDescription, String longDescription) {
		this.widgetId = widgetId;
		this.controlType = controlType;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.applicationKey = applicationKey;
		//this.setApplication(application);
	}

	@SuppressWarnings("unused")
	private WidgetDao() {

	}

	public void clearChangedFlag() {
		this.changedFlag = false;
	}
	
	public void addWidgetOption(WidgetOptionDao widgetOption) {
		if (null == this.widgetOptions) {
			this.widgetOptions = new ArrayList<WidgetOptionDao>();
		}
		this.widgetOptions.add(widgetOption);
		this.changedFlag = true;
	}
	

	public void addWidgetParameter(WidgetParameterDao widgetParameter) {
		if (null == this.widgetParameters) {
			this.widgetParameters = new ArrayList<WidgetParameterDao>();
		}
		this.widgetParameters.add(widgetParameter);
		this.changedFlag = true;
	}

	public ArrayList<WidgetOptionDao> assignReferenceCodes(ReferenceCodeGeneratorDAO rcg) {
		ArrayList<WidgetOptionDao> assigned = new ArrayList<WidgetOptionDao>();
		
		if ( this.controlType.equalsIgnoreCase("presence") ) {
			WidgetOptionDao option = this.getWidgetOptions().get(0);
			option.clearChangedFlag();
			option.setReferenceCode("checkin");
			option.setRecyclable(false);
			if ( option.isChangedFlag() ) {
				assigned.add(option);
			}
			return assigned;
		}
		
		String code;
		for ( WidgetOptionDao option : this.widgetOptions ) {
			option.clearChangedFlag();
			
			if ( null == option.getReferenceCode() ) {
				if ( null != option.getSuggestedReferenceCode() && option.getSuggestedReferenceCode().length() > 1 ) {
					code = option.getSuggestedReferenceCode();
					option.setRecyclable(false);
				} else {
					code = rcg.getNextCodeAsString();
				}
				Log.get().debug("Assigning reference code: " + code + " to " + this.toString());
				option.setReferenceCode(code);
			}
			if ( option.isChangedFlag() ) {
				assigned.add(option);
			}
		}
		return assigned;
	}

//	/**
//	 * @return the application
//	 */
//	public ApplicationDao getApplication() {
//		return application;
//	}

	/**
	 * @return the applicationKey
	 */
	public Key<ApplicationDao> getApplicationKey() {
		return applicationKey;
	}

	/**
	 * @return the controlType
	 */
	public String getControlType() {
		return controlType;
	}

	public Key<WidgetDao> getKey() {
		return new Key<WidgetDao>(this.applicationKey, WidgetDao.class, this.widgetId);
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	public String getWidgetId() {
		return this.widgetId;
	}

	/**
	 * @return the widgetOptions
	 */
	public ArrayList<WidgetOptionDao> getWidgetOptions() {
		return widgetOptions;
	}


	public ArrayList<WidgetOptionDao> mergeOptionsToAdd(WidgetDao that) {
		
		ArrayList<WidgetOptionDao> toAdd = new ArrayList<WidgetOptionDao>();
		
		/*
		 * If we have no options, add all
		 */
		if (null == this.widgetOptions) {
			this.widgetOptions = new ArrayList<WidgetOptionDao>();
			this.widgetOptions.addAll(that.getWidgetOptions());
			toAdd.addAll(that.getWidgetOptions());
			
		} else {
			Iterator<WidgetOptionDao> it = that.getWidgetOptions().iterator();
			while (it.hasNext()) {
				WidgetOptionDao next = it.next();
				if (!this.widgetOptions.contains(next)) {
					Log.get().debug("Adding to new option " + next.toString());
					this.widgetOptions.add(next);
					toAdd.add(next);
				}
			}
		}

		return toAdd;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WidgetDao))
			return false;
		WidgetDao other = (WidgetDao) obj;
		if (widgetId == null) {
			if (other.widgetId != null)
				return false;
		} else if (!widgetId.equals(other.widgetId))
			return false;
		return true;
	}

	public ArrayList<WidgetOptionDao> mergeOptionsToDelete(WidgetDao that) {
		ArrayList<WidgetOptionDao> toDelete = new ArrayList<WidgetOptionDao>();
		/*
		 * Check options that have been deleted in that and... delete them in
		 * this
		 */
		if (null != this.widgetOptions) {
			Iterator<WidgetOptionDao> it = this.widgetOptions.iterator();
			while (it.hasNext()) {
				WidgetOptionDao next = it.next();
				if (!that.getWidgetOptions().contains(next)) {
					Log.get().debug("Deleting unused option " + it.toString());
					it.remove();
					toDelete.add(next);
				}
			}
		}
		return toDelete;
	}

//	public void setApplication(ApplicationDao a) {
//		this.application = a;
//		if (null != a) {
//			this.application = a;
//
//			this.applicationKey = new Key<ApplicationDao>(a.getPlaceKey(), ApplicationDao.class,
//					a.getApplicationId());
//		}
//	}

	/**
	 * @param applicationKey
	 *            the applicationKey to set
	 */
	public void setApplicationKey(Key<ApplicationDao> applicationKey) {
		if ( !this.applicationKey.equals(applicationKey) ) {
			this.changedFlag = true;
		}
		this.applicationKey = applicationKey;
	}

	/**
	 * @param controlType
	 *            the controlType to set
	 */
	public void setControlType(String controlType) {
		if ( !this.controlType.equals(controlType) ) {
			this.changedFlag = true;
		}
		this.controlType = controlType;
	}

	/**
	 * @param longDescription
	 *            the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		if ( !this.longDescription.equals(longDescription) ) {
			this.changedFlag = true;
		}
		this.longDescription = longDescription;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		if ( !this.shortDescription.equals(shortDescription) ) {
			this.changedFlag = true;
		}
		this.shortDescription = shortDescription;
	}



	public void setWidgetId(String id) {
		if ( !this.widgetId.equals(id) ) {
			this.changedFlag = true;
		}
		this.widgetId = id;

	}

	/**
	 * @param widgetOptions
	 *            the widgetOptions to set
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionDao> widgetOptions) {
		this.widgetOptions = widgetOptions;
	}

	@Override
	public String toString() {
	
		return "Widget: " + this.widgetId;
	}

	

	/**
	 * @return the widgetParameters
	 */
	public ArrayList<WidgetParameterDao> getWidgetParameters() {
		return widgetParameters;
	}

	/**
	 * @param widgetParameters the widgetParameters to set
	 */
	public void setWidgetParameters(ArrayList<WidgetParameterDao> widgetParameters) {
		this.changedFlag = !(this.widgetParameters==null ? widgetParameters==null : this.widgetParameters.equals(widgetParameters));
		this.widgetParameters = widgetParameters;
	}

	/**
	 * @return the changedFlag
	 */
	public boolean isChangedFlag() {
		return changedFlag;
	}

}
