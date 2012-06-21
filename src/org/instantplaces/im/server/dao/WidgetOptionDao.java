package org.instantplaces.im.server.dao;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

public class WidgetOptionDao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Unindexed
	private String longDescripton;

	@Indexed
	private String referenceCode;

	@Unindexed
	private String shortDescription;

	@Unindexed
	private String suggestedReferenceCode;
	
	/**
	 * Was the reference code assigned from the pool of unique codes?
	 * Default is yes.
	 */
	@Unindexed 
	private boolean recyclable;

//	@NotSaved
//	private WidgetDao widget;

	@Parent
	private Key<WidgetDao> widgetKey;

	@Id
	private String widgetOptionId;

	public WidgetOptionDao(Key<WidgetDao> parentKey, String widgetOptionId) {
		this(parentKey, widgetOptionId, null, null);
	}

	public WidgetOptionDao(Key<WidgetDao> parentKey, String widgetOptionId, String suggestedReferenceCode,
			String referenceCode) {
		this.widgetOptionId = widgetOptionId;
		this.suggestedReferenceCode = suggestedReferenceCode;
		this.referenceCode = referenceCode;
		this.widgetKey = parentKey;
		this.recyclable = true;
	}

	@SuppressWarnings("unused")
	private WidgetOptionDao() {
	}

	public Key<WidgetOptionDao> getKey() {
		return new Key<WidgetOptionDao>(this.widgetKey, WidgetOptionDao.class, this.widgetOptionId);
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
	public Key<WidgetDao> getWidgetKey() {
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
		this.longDescripton = longDescripton;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setSuggestedReferenceCode(String suggestedReferenceCode) {
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
	public void setWidgetKey(Key<WidgetDao> widgetKey) {
		this.widgetKey = widgetKey;
	}

	public void setWidgetOptionId(String id) {
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
		if (!(obj instanceof WidgetOptionDao))
			return false;
		WidgetOptionDao other = (WidgetOptionDao) obj;
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
		this.recyclable = recyclable;
	}

}
