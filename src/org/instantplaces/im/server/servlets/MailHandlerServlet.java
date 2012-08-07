package org.instantplaces.im.server.servlets;

import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;
import java.util.Properties; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session; 
import javax.mail.internet.MimeMessage; 
import javax.servlet.http.*; 

import org.instantplaces.im.server.Log;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDao;
import org.instantplaces.im.server.dao.WidgetOptionDao;
import org.instantplaces.im.server.rest.representation.json.WidgetInputRest;
import org.instantplaces.im.server.rest.resource.WidgetInputResource;

public class MailHandlerServlet extends HttpServlet { 
	
	private static final String SERVER_ADDRESS = ".appspotmail.com";
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, 
                       HttpServletResponse resp) 
            throws IOException { 
        
		String placeId = null;
		String userId = null;
		String referenceCode = null;
		
		ArrayList<String> parameters;
		
		Properties props = new Properties(); 
        Session session = Session.getDefaultInstance(props, null); 
        
        
        try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());
				
			Log.get().debug("Message ID: " + message.getMessageID() );
			Log.get().debug("Message Sent: " + message.getSentDate().toString() );
			
		
			userId = getSender(message);
				
			placeId = getPlace(message);
				

			String subject = message.getSubject().trim();
			Log.get().debug("Subject: " + subject);
			
			/*
			 * Extract the location.referencecode pattern
			 */
			String locationPlusReferenceCodePattern = "^(\\w+)\\.(\\w+)\\s*";
			Pattern pattern = Pattern.compile(locationPlusReferenceCodePattern, Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(subject);
		    
		    String locationPlusReferenceCode = null;
		    if ( matcher.find() && matcher.groupCount() > 1) {
		    	locationPlusReferenceCode = matcher.group().toLowerCase();
		    	Log.get().debug("Location.Reference: " + locationPlusReferenceCode);
		    	
		    	placeId = matcher.group(1);
		    	referenceCode = matcher.group(2);
		    	
		    	/*
		    	 * Keep only the rest of the string for the next processing phase
		    	 */
		    	subject = subject.substring(matcher.end());
		    } else {
		    	String referenceCodePattern = "^(\\w+)\\s*";
				pattern = Pattern.compile(referenceCodePattern, Pattern.CASE_INSENSITIVE);
			    matcher = pattern.matcher(subject);
			    if ( matcher.find() && matcher.groupCount() > 0) {
			    	referenceCode = matcher.group(1);
			    	Log.get().debug("Reference: " + referenceCode);
			    	/*
			    	 * Keep only the rest of the string for the next processing phase
			    	 */
			    	subject = subject.substring(matcher.end());
			    }
		    	
		    }
		    Log.get().debug("PlaceId: " + placeId);
		    Log.get().debug("ReferenceCode: " + referenceCode);
		    
		    /*
		     * Extract the parameters
		     */
		    String parameterPattern = "\\w+|\"[\\w\\s]*\"|\"[\\w\\s]*";
		    	 
		    pattern = Pattern.compile(parameterPattern, Pattern.CASE_INSENSITIVE);
		    matcher = pattern.matcher(subject);
		    
		    parameters = new ArrayList<String>();
		    while ( matcher.find() ) {
		    	parameters.add( matcher.group().trim() );
		    	Log.get().debug("Parameter: " + matcher.group() );
		    }
		    
		    this.saveInput(placeId, userId, referenceCode, parameters.toArray(new String[] {}));
		    
		    
				
				Object content = message.getContent();
				Log.get().debug("Content: " + content);
				
				
				Multipart mp = (Multipart)message.getContent();
				Log.get().debug("MultipartContent: " + mp);
				
				for (int i=0, n=mp.getCount(); i<n; i++) {
				  Part part = mp.getBodyPart(i);
				  Log.get().debug("Part: " + part);
				  
				  String disposition = part.getDisposition();
				  Log.get().debug("Disposition: " + disposition);
				  
				  String filename = part.getFileName();
				  Log.get().debug("Filename: " + filename);
				  
//				  if ( (disposition != null) ) {
//				    saveFile(part.getFileName(), part.getInputStream());
//				  }
				}
				
				
			
		} catch (MessagingException e) {
			Log.get().error("Error reading message.");
			
		} catch (ClassCastException cce) {
			Log.get().error("Error casting message.");
		}
    }


/*
 * TODO: this code should be refactored to use the widgetinputresource instead of duplicating
 */
	
	private  void saveInput(String placeReferenceCode, String userEmail, String refCode,
			String[] parameters) {



		/*
		 * TODO: if reference code is not provided, try to find the application anyway
		 */
		List<PlaceDao> places = Dao.getPlaces();
		
		for ( PlaceDao place : places ) {
			if ( place.getPlaceReferenceCode().equalsIgnoreCase(placeReferenceCode) ) {
				Dao.beginTransaction();
				WidgetOptionDao widgetOption = null; 
				
				List<WidgetOptionDao> options = Dao.getWidgetOptions(place.getPlaceId());
				
				for ( WidgetOptionDao option : options ) {
					if ( option.getReferenceCode().equals(refCode)) {
						widgetOption = option;
						break;
					}
				}
				
					
				if (widgetOption == null) {
					Log.get().debug("No widgets are using this reference code.");
					Dao.commitOrRollbackTransaction();
				} else {
					/*
					 * Obfuscate the email address
					 */
					String userIdentity = userEmail.hashCode()+"";
					String name = userEmail;
//					if ( userEmail.length() > 9 ) {
//						name = userEmail.substring(3);
//					}
//					name = name.substring(0, 2) + "..." + name.substring(5);
					
					/*
					 * Create the widgetinputrest representation to use the widgetinputresource to save and forward the input
					 */
					WidgetInputRest widgetInputRest = new WidgetInputRest();
					
					widgetInputRest.setPlaceId(place.getPlaceId());
					widgetInputRest.setApplicationId(widgetOption.getWidgetKey().getParent().getName());
					widgetInputRest.setWidgetId(widgetOption.getWidgetKey().getName());
					widgetInputRest.setWidgetOptionId(widgetOption.getWidgetOptionId());
					widgetInputRest.setInputMechanism("Email");
					widgetInputRest.setNickname(name);
					widgetInputRest.setReferenceCode(widgetOption.getReferenceCode());
					widgetInputRest.setUserId(userIdentity);
					widgetInputRest.setParameters(parameters);
					
					
					if ( !Dao.commitOrRollbackTransaction() ) {
						Log.get().error("Could not save input to datastore");
					}
					
					WidgetInputResource.handleInput(widgetInputRest, widgetInputRest.getPlaceId(), widgetInputRest.getApplicationId(), widgetInputRest.getWidgetId());
				}
				
				
			}
			
		}



	}
	
	
	/**
	 * @param message
	 * @throws MessagingException
	 */
	private String getSender(MimeMessage message) throws MessagingException {

		
		Address []senders = message.getFrom();
			
		
		if ( null != senders ) {
			for ( Address senderAddress : senders ) {
				if ( null != senderAddress ) {
					
					String sender = senderAddress.toString();
					
					String emailBetweenMinorGreater = "<(.+)>";
					Pattern pattern = Pattern.compile(emailBetweenMinorGreater, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(sender);
				   
				    if ( matcher.find() && matcher.groupCount() > 0) {
				    	sender = matcher.group(1);
				    }
				    Log.get().debug("Sender: " + sender);
				    
				    return sender;
				    
					
				} else {
					Log.get().warn("Null sender!");
				}
			}
		}
		return null;
	}

	/**
	 * @param message
	 * @throws MessagingException
	 */
	private String getPlace(MimeMessage message) throws MessagingException {
		
		String place ="";
		Address []recipients = message.getRecipients(Message.RecipientType.TO);
		if ( null != recipients ) {
			for ( Address recipientAddress : recipients ) {
				if ( null != recipientAddress ) {
					
					String recipient = recipientAddress.toString();			
				
					Log.get().debug("Recipient start: " + recipient);
					
					String emailBetweenMinorGreater = "<(.+)>";
					Pattern pattern = Pattern.compile(emailBetweenMinorGreater, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(recipient);
				   
				    if ( matcher.find() && matcher.groupCount() > 0) {
				    	recipient = matcher.group(1);
				    }
				    
				    Log.get().debug("Recipient end: " + recipient);
				    
				    /*
				     * Found our address
				     */
				    if ( recipient.contains(SERVER_ADDRESS) ) {
				    	String userPattern = "(.*)@";
						pattern = Pattern.compile(userPattern, Pattern.CASE_INSENSITIVE);
						matcher = pattern.matcher(recipient);
						if ( matcher.find() && matcher.groupCount() > 0 ) {
					    	place = matcher.group(1);
					    }
				    } else {
				    	Log.get().warn(recipient + " does not contain " + SERVER_ADDRESS);
				    }
				} else {
					Log.get().warn("Null recipient!");
				}
			}
		}
		Log.get().debug("Found place: " + place);
		
		return place;
	}
}
