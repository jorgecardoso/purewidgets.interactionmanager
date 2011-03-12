package org.instantplaces.im.server.dso;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.rest.WidgetOptionREST;


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
	
	@Persistent
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
		Log.get().debug("Converting to REST " + this.toString());
		WidgetOptionREST woREST = new WidgetOptionREST();
		woREST.setId(this.id);
		woREST.setSuggestedReferenceCode(this.referenceCode);
		woREST.setSuggestedReferenceCode(this.suggestedReferenceCode);
		
		Log.get().debug("Converted: " + woREST.toString());
		return woREST;
	}
	
	@Override
	public boolean equals(Object that) {
		if ( !(that instanceof WidgetOptionDSO) ) {
			return false;
		}
		//Key thatKey = ((WidgetOptionDSO)that).getKey();
		String thatId = ((WidgetOptionDSO)that).getId();
		
		
		if (this.id == null || thatId == null) {
			return false;
		}
		return this.id.equals(thatId);
		
	}
}
