package org.instantplaces.im.server.rest.representation.json;


import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.instantplaces.im.shared.WidgetOption;


@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class WidgetOptionRest implements WidgetOption {
	

	private String widgetOptionId;
	

	private String suggestedReferenceCode;
	

	private String referenceCode;
	

	private String shortDescription;
	

	private String longDescription;
	
	
	public WidgetOptionRest() {
		
	}
	

	@Override
	public void setWidgetOptionId(String id) {
		this.widgetOptionId = id;
	}

	@Override
	public String getWidgetOptionId() {
		return this.widgetOptionId;
	}

	@Override
	public void setSuggestedReferenceCode(String refCode) {
		this.suggestedReferenceCode = refCode;
	}

	@Override
	public String getSuggestedReferenceCode() {
		return this.suggestedReferenceCode;
	}

	@Override
	public void setReferenceCode(String refCode) {
		this.referenceCode = refCode;		
	}

	@Override
	public String getReferenceCode() {
		return this.referenceCode;
	}
	
	@Override
	public String toString() {
		return "WidgetOption ( " + this.widgetOptionId + " )";
	
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


	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WidgetOption(id: ").append(this.widgetOptionId).append("; suggestedReferenceCode: ").append(this.suggestedReferenceCode);
		sb.append("; referenceCode: ").append(this.referenceCode);
		sb.append(")");
		return sb.toString();
		
	}


}
