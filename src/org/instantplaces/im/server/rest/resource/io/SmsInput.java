package org.instantplaces.im.server.rest.resource.io;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.WidgetInputDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;
import org.instantplaces.im.server.rest.representation.json.WidgetInputRest;
import org.instantplaces.im.server.rest.resource.WidgetInputResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;


/**
 * The SmsInput receives input from an Http interface from SMSServer (http://smslib.org/doc/smsserver/interfaces/http/)
 * 
 *  It uses the default url parameter names defined in http://smslib.org/doc/smsserver/interfaces/http/
 */
public class SmsInput extends ServerResource {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.instantplaces.im.server.resource.GenericResource#doGet()
	 */
	@Get
	public Object doGet() {
		String text = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("text", "");
		String originator = this.getRequest().getOriginalRef().getQueryAsForm().getFirstValue("originator", "");
		
		text = text.trim();
		Log.get().debug("Input SMS text: " + text);
		Log.get().debug("Originator : " + originator);
		
		/*
		 * Remove operator signature
		 */
		String operatorSignaturePattern = "<.*>$";
		Pattern pattern = Pattern.compile(operatorSignaturePattern, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(text);
		
	    if ( matcher.find() ) {
	    	
	    	text = text.replace(matcher.group(), "");
	    }
		
		/*
		 * Extract the location.referencecode pattern
		 */
		String locationPlusReferenceCodePattern = "\\w+\\.\\w+\\s*";
		pattern = Pattern.compile(locationPlusReferenceCodePattern, Pattern.CASE_INSENSITIVE);
	    matcher = pattern.matcher(text);
	    
	    String locationPlusReferenceCode = null;
	    if ( matcher.find() ) {
	    	locationPlusReferenceCode = matcher.group().toLowerCase();
	    	Log.get().debug("Location.Reference: " + locationPlusReferenceCode);
	    	/*
	    	 * Keep only the rest of the string for the next processing phase
	    	 */
	    	text = text.substring(matcher.end());
	    }
	    
	    if ( null == locationPlusReferenceCode ) {
	    	Log.get().warn("Could not read location.referencecode pattern");
	    	return "";
	    }
	    
	    /*
	     * Extract the location and the referencecode separately.
	     */
	    String []lr = locationPlusReferenceCode.trim().split("\\.");
	    
	    String location = lr[0];
	    String referenceCode = lr[1];
		
	    Log.get().debug("Location: " + location);
	    Log.get().debug("ReferenceCode: " + referenceCode);
	    
	    /*
	     * Extract the parameters
	     */
	    String parameterPattern = "\\w+|\"[\\w\\s]*\"|\"[\\w\\s]*";
	    	 
	    pattern = Pattern.compile(parameterPattern, Pattern.CASE_INSENSITIVE);
	    matcher = pattern.matcher(text);
	    
	    ArrayList<String> parameters = new ArrayList<String>();
	    while ( matcher.find() ) {
	    	parameters.add( matcher.group().trim() );
	    	Log.get().debug("Parameter: " + matcher.group() );
	    }
	    
	    
	    WidgetInputResource.saveInput(location, originator, obfuscate(originator), referenceCode, parameters.toArray(new String[] {}), "SMS");
		return "";

	}
	
	private String obfuscate(String phoneNumber) {
		String name = phoneNumber;
		
		if ( phoneNumber.length() > 9 ) {
			name = phoneNumber.substring(3);
		}
		name = name.substring(0, 2) + "..." + name.substring(5);
		return name;
	}
	
}
