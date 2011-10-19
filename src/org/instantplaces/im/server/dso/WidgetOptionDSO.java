package org.instantplaces.im.server.dso;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
	private String widgetOptionId;
	
	@Persistent
	private String suggestedReferenceCode;
	
	@Persistent
	private String referenceCode;
	
	@Persistent
	private WidgetDSO widget;
	
	@Persistent(mappedBy = "widgetOption")
	private ArrayList <WidgetInputDSO> inputs; 
	
	@Persistent
	private String shortDescription;
	
	@Persistent
	private String longDescripton;
	
	public WidgetOptionDSO() {
		this(null, null);
	}
	
	
	public WidgetOptionDSO(String id, WidgetDSO widget) {
		this(id, null, null, widget);
	}
	
	
	public WidgetOptionDSO(String id, String suggestedReferenceCode, String referenceCode, WidgetDSO widget) {
		this.widgetOptionId = id;
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
	
	
	public void addWidgetInput(WidgetInputDSO input) {
		if (!this.inputs.contains(input)) {
			this.inputs.add(input);
		}
	}
	public void removeWidgetInput(WidgetInputDSO input) {
		this.inputs.remove(input);
	}


	public WidgetInputDSO[] getWidgetInputs() {
		return this.inputs.toArray(new WidgetInputDSO[0]);
	}

	public ArrayList<WidgetInputDSO> getWidgetInputsAsArrayList() {
		return this.inputs;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WidgetOption(id: ").append(this.widgetOptionId).append("; suggestedReferenceCode: ").append(this.suggestedReferenceCode);
		sb.append("; referenceCode: ").append(this.referenceCode);
		sb.append(")");
		return sb.toString();
	}
	
	
	public static WidgetOptionDSO fromREST(WidgetOptionREST widgetOptionREST) {
		Log.get().debug("Converting WidgetOptionREST to DSO");
		
		WidgetOptionDSO woDSO = new WidgetOptionDSO();
		
		woDSO.setWidgetOptionId(widgetOptionREST.getWidgetOptionId());
		woDSO.setSuggestedReferenceCode(widgetOptionREST.getSuggestedReferenceCode());
		woDSO.setReferenceCode(widgetOptionREST.getReferenceCode());
		woDSO.setLongDescripton(widgetOptionREST.getLongDescription());
		woDSO.setShortDescription(widgetOptionREST.getShortDescription());
		
		return woDSO;
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
	
	public static WidgetOptionDSO getWidgetOptionDSOByReferenceCode(PersistenceManager pm, String referenceCode ) {
		Log.get().debug("Fetching WidgetOptionDSO with referenceCode(" + referenceCode + ") from Data Store.");
		
		Query query = pm.newQuery(WidgetOptionDSO.class);
	    query.setFilter("referenceCode == idParam");
	    query.declareParameters("String idParam");
	    
	    try {
	        List<WidgetOptionDSO> results = (List<WidgetOptionDSO>) query.execute(referenceCode);
	        if (!results.isEmpty()) {
	        	Log.get().debug("Found " + results.size() + " widget options. Returning first.");
	        	WidgetOptionDSO widgetOption = results.get(0);
	        	return widgetOption;
	        } else {
	        	Log.get().debug("Widget option not found.");
	        }
	    } catch (Exception e) {
	    	Log.get().error("Could not access data store.");
	    	Log.get().error(e.getMessage());
	    	e.printStackTrace();
	    }  finally {
	        query.closeAll();
	    }
	    return null;
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
}
