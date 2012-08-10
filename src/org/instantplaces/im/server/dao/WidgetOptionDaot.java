package org.instantplaces.im.server.dao;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class WidgetOptionDaot implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Parent
	private Key<WidgetDaot> widgetKey;

	@Id
	private String widgetOptionId;
	
	@Unindexed
	private String longDescripton;

	@Indexed
	private String referenceCode;

	@Unindexed
	private String shortDescription;

	@Unindexed
	private String iconUrl;
	
	@Unindexed
	private String suggestedReferenceCode;
	
	/**
	 * Was the reference code assigned from the pool of unique codes?
	 * Default is yes.
	 */
	@Unindexed 
	private boolean recyclable;

	private boolean changedFlag;


	public WidgetOptionDaot(Key<WidgetDaot> parentKey, String widgetOptionId) {
		this(parentKey, widgetOptionId, null, null);
	}

	public WidgetOptionDaot(Key<WidgetDaot> parentKey, String widgetOptionId, String suggestedReferenceCode,
			String referenceCode) {
		this.widgetOptionId = widgetOptionId;
		this.suggestedReferenceCode = suggestedReferenceCode;
		this.referenceCode = referenceCode;
		this.widgetKey = parentKey;
		this.recyclable = true;
	}

	@SuppressWarnings("unused")
	private WidgetOptionDaot() {
	}

	public Key<WidgetOptionDaot> getKey() {
		return new Key<WidgetOptionDaot>(this.widgetKey, WidgetOptionDaot.class, this.widgetOptionId);
	}

	/**
	 * @return the longDescripton
	 */
	public String getLongDescripton() {
		return longDescripton;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	public String getSuggestedReferenceCode() {
		return this.suggestedReferenceCode;
	}

//	public WidgetDao getWidget() {
//		return this.widget;
//	}

	/**
	 * @return the widgetKey
	 */
	public Key<WidgetDaot> getWidgetKey() {
		return widgetKey;
	}

	public String getWidgetOptionId() {
		return this.widgetOptionId;
	}

	/**
	 * @param longDescripton
	 *            the longDescripton to set
	 */
	public void setLongDescripton(String longDescripton) {
		
		if ( !(this.longDescripton == null ? longDescripton == null : this.longDescripton.equals(longDescripton) ) ) {
			this.changedFlag = true;
		}

		this.longDescripton = longDescripton;
	}

	public void setReferenceCode(String referenceCode) {
		if ( !(this.referenceCode == null ? referenceCode == null : this.referenceCode.equals(referenceCode) ) ) {
			this.changedFlag = true;
		}
		

		this.referenceCode = referenceCode;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		if ( !(this.shortDescription == null ? shortDescription == null : this.shortDescription.equals(shortDescription) ) ) {
			this.changedFlag = true;
		}
		
		this.shortDescription = shortDescription;
	}

	public void setSuggestedReferenceCode(String suggestedReferenceCode) {
		if ( !(this.suggestedReferenceCode == null ? suggestedReferenceCode == null : this.suggestedReferenceCode.equals(suggestedReferenceCode) ) ) {
			this.changedFlag = true;
		}
	
		this.suggestedReferenceCode = suggestedReferenceCode;
	}

//	public void setWidget(WidgetDao w) {
//		this.widget = w;
//		if (null != w) {
//			this.widgetKey = new Key<WidgetDao>(w.getApplicationKey(), WidgetDao.class,
//					w.getWidgetId());
//		}
//	}

	/**
	 * @param widgetKey
	 *            the widgetKey to set
	 */
	public void setWidgetKey(Key<WidgetDaot> widgetKey) {
		if ( !this.widgetKey.equals(widgetKey) ) {
			this.changedFlag = true;
		}
		this.widgetKey = widgetKey;
	}

	public void setWidgetOptionId(String id) {
		if ( !this.widgetOptionId.equals(id) ) {
			this.changedFlag = true;
		}
		this.widgetOptionId = id;

	}

	@Override
	public String toString() {
		return "WidgetOption: " + this.widgetOptionId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WidgetOptionDaot))
			return false;
		WidgetOptionDaot other = (WidgetOptionDaot) obj;
		if (widgetOptionId == null) {
			if (other.widgetOptionId != null) {
				return false;
			}
		} else if (!widgetOptionId.equals(other.widgetOptionId)) {
			return false;
		} else if ( null == this.suggestedReferenceCode && other.getSuggestedReferenceCode() != null ) {
			return false;
		} else if ( null != this.suggestedReferenceCode && null != other.getSuggestedReferenceCode() && !this.suggestedReferenceCode.equals(other.getSuggestedReferenceCode()) ) {
			return false;
		}
		return true;
	}

	/**
	 * @return the recyclable
	 */
	public boolean isRecyclable() {
		return recyclable;
	}

	/**
	 * @param recyclable the recyclable to set
	 */
	public void setRecyclable(boolean recyclable) {
		if ( this.recyclable != recyclable ) {
			this.changedFlag = true;
		}
		this.recyclable = recyclable;
	}

	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/**
	 * @param iconUrl the iconUrl to set
	 */
	public void setIconUrl(String iconUrl) {
		if ( !(this.iconUrl == null ? iconUrl == null : this.iconUrl.equals(iconUrl) ) ) {
			this.changedFlag = true;
		}
		

		this.iconUrl = iconUrl;
	}
	
	public void clearChangedFlag() {
		this.changedFlag = false;
	}
	
	/**
	 * @return the changedFlag
	 */
	public boolean isChangedFlag() {
		return changedFlag;
	}

}
