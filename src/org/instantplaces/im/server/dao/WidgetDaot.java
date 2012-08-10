package org.instantplaces.im.server.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


import javax.persistence.Embedded;
import javax.persistence.Id;

import org.instantplaces.im.server.logging.Log;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class WidgetDaot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	@NotSaved
//	private ApplicationDao application;

	@Parent
	private Key<ApplicationDaot> applicationKey;

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
	private ArrayList<WidgetOptionDaot> widgetOptions;

	@NotSaved
	private boolean changedFlag;
	
	@NotSaved
	private boolean traceChanges;
	
	public WidgetDaot(Key<ApplicationDaot> applicationKey) {
		this(applicationKey, null, null, null, null);
	}

	public WidgetDaot(Key<ApplicationDaot> applicationKey, String widgetId, String controlType,
			String shortDescription, String longDescription) {
		this();
		this.widgetId = widgetId;
		this.controlType = controlType;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.applicationKey = applicationKey;
		
	}

	@SuppressWarnings("unused")
	private WidgetDaot() {
		this.widgetParameters = new ArrayList<WidgetParameterDao>();
		this.traceChanges = false;
	}

	public void clearChangedFlag() {
		this.changedFlag = false;
		this.traceChanges = true;
	}
	
	public void addWidgetOption(WidgetOptionDaot widgetOption) {
		if (null == this.widgetOptions) {
			this.widgetOptions = new ArrayList<WidgetOptionDaot>();
		}
		this.widgetOptions.add(widgetOption);
	}
	

	public void addWidgetParameter(WidgetParameterDao widgetParameter) {
		if (null == this.widgetParameters) {
			this.widgetParameters = new ArrayList<WidgetParameterDao>();
		}
		this.widgetParameters.add(widgetParameter);
		
		if ( this.traceChanges ) {
			this.changedFlag = true;
			Log.debugFinest(this, "Widget " + this.widgetId + " changed in parameters: added " + widgetParameter.toString());
		}
	}

	public ArrayList<WidgetOptionDaot> assignReferenceCodes(ReferenceCodeGeneratorDAO rcg) {
		ArrayList<WidgetOptionDaot> assigned = new ArrayList<WidgetOptionDaot>();
		
		if ( this.controlType.equalsIgnoreCase("presence") ) {
			WidgetOptionDaot option = this.getWidgetOptions().get(0);
			option.clearChangedFlag();
			option.setReferenceCode("checkin");
			option.setRecyclable(false);
			if ( option.isChangedFlag() ) {
				assigned.add(option);
			}
			return assigned;
		}
		
		String code;
		for ( WidgetOptionDaot option : this.widgetOptions ) {
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
	public Key<ApplicationDaot> getApplicationKey() {
		return applicationKey;
	}

	/**
	 * @return the controlType
	 */
	public String getControlType() {
		return controlType;
	}

	public Key<WidgetDaot> getKey() {
		return new Key<WidgetDaot>(this.applicationKey, WidgetDaot.class, this.widgetId);
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
	public ArrayList<WidgetOptionDaot> getWidgetOptions() {
		return widgetOptions;
	}


	public ArrayList<WidgetOptionDaot> mergeOptionsToAdd(WidgetDaot that) {
		
		ArrayList<WidgetOptionDaot> toAdd = new ArrayList<WidgetOptionDaot>();
		
		/*
		 * If we have no options, add all
		 */
		if (null == this.widgetOptions) {
			this.widgetOptions = new ArrayList<WidgetOptionDaot>();
			this.widgetOptions.addAll(that.getWidgetOptions());
			toAdd.addAll(that.getWidgetOptions());
			
		} else {
			Iterator<WidgetOptionDaot> it = that.getWidgetOptions().iterator();
			while (it.hasNext()) {
				WidgetOptionDaot next = it.next();
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
		if (!(obj instanceof WidgetDaot))
			return false;
		WidgetDaot other = (WidgetDaot) obj;
		if (widgetId == null) {
			if (other.widgetId != null)
				return false;
		} else if (!widgetId.equals(other.widgetId))
			return false;
		return true;
	}

	public ArrayList<WidgetOptionDaot> mergeOptionsToDelete(WidgetDaot that) {
		ArrayList<WidgetOptionDaot> toDelete = new ArrayList<WidgetOptionDaot>();
		/*
		 * Check options that have been deleted in that and... delete them in
		 * this
		 */
		if (null != this.widgetOptions) {
			Iterator<WidgetOptionDaot> it = this.widgetOptions.iterator();
			while (it.hasNext()) {
				WidgetOptionDaot next = it.next();
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
	public void setApplicationKey(Key<ApplicationDaot> applicationKey) {
		if ( this.traceChanges ) {
			if ( !this.applicationKey.equals(applicationKey) ) {
				this.changedFlag = true;
				Log.debugFinest(this, "Widget " + this.widgetId + " changed in application key.");
			}
		}
		this.applicationKey = applicationKey;
	}

	/**
	 * @param controlType
	 *            the controlType to set
	 */
	public void setControlType(String controlType) {
		if ( this.traceChanges ) {
			if ( !this.controlType.equals(controlType) ) {
				this.changedFlag = true;
				Log.debugFinest(this, "Widget " + this.widgetId + " changed in control type.");
			}	
		}
		this.controlType = controlType;
	}

	/**
	 * @param longDescription
	 *            the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		if ( this.traceChanges ) {
			if ( !this.longDescription.equals(longDescription) ) {
				this.changedFlag = true;
				Log.debugFinest(this, "Widget " + this.widgetId + " changed in long description.");
			}
		}
		this.longDescription = longDescription;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		if ( this.traceChanges ) {
			if ( !this.shortDescription.equals(shortDescription) ) {
				this.changedFlag = true;
				Log.debugFinest(this, "Widget " + this.widgetId + " changed in short description.");
			}
		}
		this.shortDescription = shortDescription;
	}



	public void setWidgetId(String id) {
		if ( this.traceChanges ) {
			if ( !this.widgetId.equals(id) ) {
				this.changedFlag = true;
				Log.debugFinest(this, "Widget " + this.widgetId + " changed in widget id.");
			}
		}
		this.widgetId = id;

	}

	/**
	 * @param widgetOptions
	 *            the widgetOptions to set
	 */
	public void setWidgetOptions(ArrayList<WidgetOptionDaot> widgetOptions) {
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
		if ( this.traceChanges ) {
			if ( !(this.widgetParameters==null ? widgetParameters==null : this.widgetParameters.equals(widgetParameters)) ) {
				this.changedFlag = true;
				Log.debugFinest(this, "Widget " + this.widgetId + " changed in parameters.");
				if ( null != this.widgetParameters ) {
					for (WidgetParameterDao p : this.widgetParameters) {
						Log.debugFinest(this, "current: " + p.toString());
					}
				} else {
					Log.debugFinest(this, "current: null");
				}
				if ( null != widgetParameters ) {
					for (WidgetParameterDao p : widgetParameters) {
						Log.debugFinest(this, "new: " + p.toString());
					}
				} else {
					Log.debugFinest(this, "new: null");
				}
			}
		}
		if ( null != widgetParameters ) {
			this.widgetParameters = widgetParameters;
		}
	}

	/**
	 * @return the changedFlag
	 */
	public boolean isChangedFlag() {
		return changedFlag;
	}

}
