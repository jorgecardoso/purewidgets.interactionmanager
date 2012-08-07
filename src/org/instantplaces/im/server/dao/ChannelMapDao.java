package org.instantplaces.im.server.dao;

import java.util.ArrayList;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class ChannelMapDao {
	

    @Id 
    private String placeApplicationId;

    /*
     * Client ids are strings formed as : 'placeid'+'applicationid'+'-'+currentId
     */
    @Unindexed
    private ArrayList<String> clientIds;

    
	@SuppressWarnings("unused")
	private ChannelMapDao() {
	}
	
    public ChannelMapDao(String placeId, String applicationId) {
    	this(placeId+applicationId);
    	
    }
    
    public ChannelMapDao(String placeApplicationId) {
    	this.placeApplicationId = placeApplicationId;
    	this.clientIds = new ArrayList<String>();
    }
    
    public void add(String clientId) {
    	if ( null == this.clientIds ) {
    		this.clientIds = new ArrayList<String>();
    	}
    	this.clientIds.add(clientId);
    }
    
    public void remove(String clientId) {
    	if ( null != this.clientIds ) {
    		this.clientIds.remove(clientId);
    	}
    }
    
    public static String getPlaceApplicationString(String clientId) {
    
    	int index = clientId.lastIndexOf("-");
    	
    	return clientId.substring(0, index);
    }
    
    
    public String getNextClientId() {
    	return this.placeApplicationId + "-" + Math.random()*1000000;
    }

	/**
	 * @return the clientIds
	 */
	public ArrayList<String> getClientIds() {
		return clientIds;
	}

	/**
	 * @param clientIds the clientIds to set
	 */
	public void setClientIds(ArrayList<String> clientIds) {
		this.clientIds = clientIds;
	}

	public boolean contains(String clientId) {
		if ( null == this.clientIds ) {
    		return false;
    	}
    	return this.clientIds.contains(clientId);
	}
    
    
}
