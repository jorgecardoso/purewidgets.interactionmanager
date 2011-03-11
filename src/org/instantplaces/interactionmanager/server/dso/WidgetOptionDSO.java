package org.instantplaces.interactionmanager.server.dso;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.interactionmanager.server.rest.WidgetOptionREST;
import org.instantplaces.interactionmanager.shared.Widget;
import org.instantplaces.interactionmanager.shared.WidgetOption;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class WidgetOptionDSO {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String id;
	
	@Persistent
	private String suggestedReferenceCode;
	
	@Persistent
	private String referenceCode;
	
	@Persistent(defaultFetchGroup = "true")
	private WidgetDSO widget;
	
	
	
	public WidgetOptionDSO() {
		this(null, null);
	}
	
	public WidgetOptionDSO(String id, WidgetDSO widget) {
		this(id, null, null, widget);
	}
	
	public WidgetOptionDSO(String id, String suggestedReferenceCode, String referenceCode, WidgetDSO widget) {
		this.id = id;
		this.suggestedReferenceCode = suggestedReferenceCode;
		this.referenceCode = referenceCode;
		this.widget = widget;
	}

	public void setWidget(WidgetDSO w) {
		this.widget = w;	
	}


	public WidgetDSO getWidget() {
		return this.widget;
	}


	public void setId(String id) {
		this.id = id;
		
	}


	public String getId() {
		return this.id;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	@Override
	public boolean equals(Object that) {
		if ( !(that instanceof WidgetOptionDSO) ) {
			return false;
		}
		Key thatKey = ((WidgetOptionDSO)that).getKey();
		String thatId = ((WidgetOptionDSO)that).getId();
		
		
		if (this.id == null || thatId == null) {
			return false;
		}
		return this.id.equals(thatId);
		
	}

	public void setSuggestedReferenceCode(String suggestedReferenceCode) {
		this.suggestedReferenceCode = suggestedReferenceCode;
	}

	public String getSuggestedReferenceCode() {
		return suggestedReferenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getReferenceCode() {
		return referenceCode;
	} 	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WidgetOption(id: ").append(this.id).append("; suggestedReferenceCode: ").append(this.suggestedReferenceCode);
		sb.append("; referenceCode: ").append(this.referenceCode);
		sb.append(")");
		return sb.toString();
	}
	
	public WidgetOptionREST toREST() {
		WidgetOptionREST woREST = new WidgetOptionREST();
		woREST.setId(this.id);
		woREST.setSuggestedReferenceCode(this.referenceCode);
		woREST.setSuggestedReferenceCode(this.suggestedReferenceCode);
		
		
		return woREST;
	}
	/*
	 	public WidgetOptionDSO toDSO() {
		log.info("Converting WidgetOptionREST to DSO");
		WidgetOptionDSO woDSO = new WidgetOptionDSO();
		
		woDSO.setId(this.id);
		woDSO.setSuggestedReferenceCode(this.suggestedReferenceCode);
		woDSO.setReferenceCode(this.referenceCode);
		
		return woDSO;
	}
	 * */
}
