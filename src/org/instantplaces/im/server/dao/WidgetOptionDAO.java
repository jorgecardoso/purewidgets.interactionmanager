package org.instantplaces.im.server.dao;


import javax.persistence.Id;



import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

public class WidgetOptionDAO {

	@Parent
	private	Key<WidgetDAO> widgetKey;
	
	@Id
	private String widgetOptionId;
	
	@Unindexed
	private String suggestedReferenceCode;
	
	@Unindexed
	private String referenceCode;
	
	@Unindexed
	private String shortDescription;
	
	@Unindexed
	private String longDescripton;
	
	@NotSaved
	private WidgetDAO widget;
	
	public WidgetOptionDAO() {
		this(null, null);
	}
	
	
	public WidgetOptionDAO(WidgetDAO widget, String widgetOptionId) {
		this(widget, widgetOptionId, null, null);
	}
	
	
	public WidgetOptionDAO(WidgetDAO widget, String widgetOptionId, String suggestedReferenceCode, String referenceCode) {
		this.widgetOptionId = widgetOptionId;
		this.suggestedReferenceCode = suggestedReferenceCode;
		this.referenceCode = referenceCode;
		
		this.setWidget(widget);
	}

	
	public void setWidget(WidgetDAO w) {
		this.widget = w;
		if ( null != w ) {
			this.widgetKey = new Key<WidgetDAO>(w.getApplicationKey(), WidgetDAO.class, w.getWidgetId());
		}
	}

	public Key<WidgetOptionDAO> getKey() {
		return new Key<WidgetOptionDAO>(this.widgetKey, WidgetOptionDAO.class, this.widgetOptionId);
	}

	public WidgetDAO getWidget() {
		return this.widget;
	}


	public void setWidgetOptionId(String id) {
		this.widgetOptionId = id;
		
	}


	public String getWidgetOptionId() {
		return this.widgetOptionId;
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
	
//	
//	@Override
//	public boolean equals(Object that) {
//		if ( !(that instanceof WidgetOptionDAO) ) {
//			return false;
//		}
//		//Key thatKey = ((WidgetOptionDSO)that).getKey();
//		String thatId = ((WidgetOptionDAO)that).getWidgetOptionId();
//		
//		
//		if (this.widgetOptionId == null || thatId == null) {
//			return false;
//		}
//		return this.widgetOptionId.equals(thatId);
//		
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
	 * @return the widgetKey
	 */
	public Key<WidgetDAO> getWidgetKey() {
		return widgetKey;
	}


	/**
	 * @param widgetKey the widgetKey to set
	 */
	public void setWidgetKey(Key<WidgetDAO> widgetKey) {
		this.widgetKey = widgetKey;
	}

	
}
