package org.instantplaces.im.server.dso;


import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.referencecode.ReferenceCodeGenerator;


import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable="true")
public class WidgetOptionDSO {
	
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
	private String widgetOptionId;
	
	@Persistent
	private String suggestedReferenceCode;
	
	@Persistent
	private String referenceCode;
	
	@NotPersistent
	private WidgetDSO widget;
	
	@Persistent
	private String shortDescription;
	
	@Persistent
	private String longDescripton;
	
	public WidgetOptionDSO() {
		this(null, null);
	}
	
	
	public WidgetOptionDSO(WidgetDSO widget, String widgetOptionId) {
		this(widget, widgetOptionId, null, null);
	}
	
	
	public WidgetOptionDSO(WidgetDSO widget, String widgetOptionId, String suggestedReferenceCode, String referenceCode) {
		this.widgetOptionId = widgetOptionId;
		this.suggestedReferenceCode = suggestedReferenceCode;
		this.referenceCode = referenceCode;
		
		this.setWidget(widget);
	}

	
	public void setWidget(WidgetDSO w) {
		this.widget = w;	
		if ( null != w ) {
			this.key = KeyFactory.createKey(w.getKey(), WidgetOptionDSO.class.getSimpleName(),  this.widgetOptionId);
			this.widgetId = w.getWidgetId();
			this.applicationId = w.getApplicationId();
			this.placeId = w.getPlaceId();
		}
	}


	public WidgetDSO getWidget() {
		return this.widget;
	}


	public void setWidgetOptionId(String id) {
		this.widgetOptionId = id;
		
	}


	public String getWidgetOptionId() {
		return this.widgetOptionId;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}


	public void setSuggestedReferenceCode(String suggestedReferenceCode) {
		this.suggestedReferenceCode = suggestedReferenceCode;
	}

	public String getSuggestedReferenceCode() {
		return this.suggestedReferenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getReferenceCode() {
		return referenceCode;
	} 	
	
	@Override
	public String toString() {
		return "WidgetOption ( " + this.widgetOptionId + " )";
	}
	
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WidgetOption(id: ").append(this.widgetOptionId).append("; suggestedReferenceCode: ").append(this.suggestedReferenceCode);
		sb.append("; referenceCode: ").append(this.referenceCode);
		sb.append(")");
		return sb.toString();
	}
	
	
	@Override
	public boolean equals(Object that) {
		if ( !(that instanceof WidgetOptionDSO) ) {
			return false;
		}
		//Key thatKey = ((WidgetOptionDSO)that).getKey();
		String thatId = ((WidgetOptionDSO)that).getWidgetOptionId();
		
		
		if (this.widgetOptionId == null || thatId == null) {
			return false;
		}
		return this.widgetOptionId.equals(thatId);
		
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
	 * @return the longDescripton
	 */
	public String getLongDescripton() {
		return longDescripton;
	}


	/**
	 * @param longDescripton the longDescripton to set
	 */
	public void setLongDescripton(String longDescripton) {
		this.longDescripton = longDescripton;
	}


	/**
	 * @return the widgetId
	 */
	public String getWidgetId() {
		return widgetId;
	}


	/**
	 * @param widgetId the widgetId to set
	 */
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
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
