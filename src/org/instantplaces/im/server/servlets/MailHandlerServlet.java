package org.instantplaces.im.server.servlets;

import java.io.IOException; 
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
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

import org.instantplaces.im.server.logging.Log;
import org.instantplaces.im.server.dao.Dao;
import org.instantplaces.im.server.dao.PlaceDaoTmp;
import org.instantplaces.im.server.dao.WidgetOptionDao;
import org.instantplaces.im.server.rest.representation.json.WidgetInputRest;
import org.instantplaces.im.server.rest.resource.WidgetInputResource;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

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
        
        /*
         * Get destination address
         */
        String destAddress = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/")+1);
        Log.debug(this, "Received email message to: " + destAddress);
        
        
        /*
         * Extract the user part of the destination address
         */
		placeId = destAddress.substring(0, destAddress.indexOf("@"));
		
		Log.debugFinest(this, "Extracted user part/placeid: " + placeId);
		
        
		/*
		 * Read the contents
		 */
        try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());
				
			Log.debug(this, "Message ID: " + message.getMessageID() );
			Log.debug(this, "Message Sent: " + message.getSentDate().toString() );
			
		
			userId = getSender(message);
			Log.debug(this, "Sender: " + userId);	
			

			String subject = message.getSubject().trim();
			Log.debug(this, "Subject: " + subject);
			
			
			/*
			 * Extract the location.referencecode pattern
			 * 
			 * Although we use the user part of the destination email address as the placeid,
			 * we allow users to override this by using a placeid.refcode syntax in the email subject
			 */
			String locationPlusReferenceCodePattern = "^(\\w+)\\.(\\w+)\\s*";
			Pattern pattern = Pattern.compile(locationPlusReferenceCodePattern, Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(subject);
		    
		    String locationPlusReferenceCode = null;
		    if ( matcher.find() && matcher.groupCount() > 1) {
		    	locationPlusReferenceCode = matcher.group().toLowerCase();
		    	Log.debugFinest(this, "Found location.referencecode in subject: " + locationPlusReferenceCode);
		    	
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
			    	Log.debug(this, "Found referencecode in subject: " + referenceCode);
			    	/*
			    	 * Keep only the rest of the string for the next processing phase
			    	 */
			    	subject = subject.substring(matcher.end());
			    }
		    	
		    }
		    Log.debug(this, "Using placeid: " + placeId);
		    Log.debug(this, "Using referencecode: " + referenceCode);
		    
		    /*
		     * Extract the parameters
		     */
		    String parameterPattern = "\\w+|\"[\\w\\s]*\"|\"[\\w\\s]*";
		    	 
		    pattern = Pattern.compile(parameterPattern, Pattern.CASE_INSENSITIVE);
		    matcher = pattern.matcher(subject);
		    
		    parameters = new ArrayList<String>();
		    while ( matcher.find() ) {
		    	parameters.add( matcher.group().trim() );
		    	Log.debugFinest("Found parameter in subject: " + matcher.group() );
		    }
		    
		    
		    /*
		     * Extract the attachments
		     */
			
			Object content = message.getContent();
			Log.debugFinest("Content: " + content);
				
			Multipart mp = null;
			try {
				mp = (Multipart)message.getContent();
			} catch (ClassCastException cce) {
				Log.error(this, "Error casting message.", cce);
			}
			
			if ( null != mp ) {
				
				Log.debugFinest(this, "MultipartContent: " + mp);
				
				for (int i=0, n=mp.getCount(); i<n; i++) {
				  Part part = mp.getBodyPart(i);
				  Log.debugFinest(this, "Part: " + part);
				  
				  String disposition = part.getDisposition();
				  Log.debugFinest(this, "Disposition: " + disposition);
				  
				  String contentType = part.getContentType();
				  Log.debugFinest(this, "ContentType: " + contentType);
				  
				  String filename = part.getFileName();
				  Log.debug(this, "Filename: " + filename);
				  if ( null != filename ) {
					 String url =  saveBlob(req, part );
					 if ( null != url ) {
						 parameters.add(url);
					 }
				  }
				}
				
				
			}
			
			WidgetInputResource.saveInput(placeId, userId, obfuscate(userId),  referenceCode, parameters.toArray(new String[] {}), "Email");
			
		} catch (MessagingException e) {
			Log.error(this, "Error reading message.", e);
		} 
    }


	private String saveBlob(HttpServletRequest req, Part part) {
		try {
		 // Get a file service
		  FileService fileService = FileServiceFactory.getFileService();

		  // Create a new Blob file with mime-type "text/plain"
		  AppEngineFile file;
		
		  file = fileService.createNewBlobFile(part.getContentType(), part.getFileName());
		  

		  // Open a channel to write to it
		  boolean lock = true;
		  FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

		  
		  InputStream is = part.getInputStream();
		  byte []buffer = new byte[1024];
		  
		  while ( is.read(buffer) > 0 ) {
			  writeChannel.write(ByteBuffer.wrap(buffer));
		  }
		  
		  writeChannel.closeFinally();
		  
		  BlobKey blobKey = fileService.getBlobKey(file);
		  return "http://" + req.getServerName() + "/serveblob/?blob-key=" + blobKey.getKeyString();
		} catch (IOException e) {
			Log.error(this, "Could not write file: ", e);
			
		} catch (MessagingException me ) {
			Log.error(this, "Could not write file: ", me);
			
		}	
		return null;
	}

	private String obfuscate(String email) {
		email = email.trim();
		if ( null == email || email.length() < 4 ) {
			return email;
		}
		int indexAt = email.indexOf("@");
		String user = email.substring(0, indexAt);
		
		int toRemove = 1;
		if ( user.length() > 5 ) {
			toRemove = 3;
		}
		
		return user.substring(0, user.length()-toRemove) + "..."+ email.substring(indexAt);
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
				    Log.debugFinest("Sender: " + sender);
				    
				    return sender;
				    
					
				} else {
					Log.get().warn("Null sender!");
				}
			}
		}
		return null;
	}
//
//	/**
//	 * @param message
//	 * @throws MessagingException
//	 */
//	private String getPlace(MimeMessage message) throws MessagingException {
//		
//		String place ="";
//		Address []recipients = message.getRecipients(Message.RecipientType.TO);
//		
//		Log.get().debug("Delivered to: " + message.getHeader("Delivered-To")); 
//		if ( null != recipients ) {
//			for ( Address recipientAddress : recipients ) {
//				if ( null != recipientAddress ) {
//					
//					String recipient = recipientAddress.toString();			
//				
//					Log.get().debug("Recipient start: " + recipient);
//					
//					String emailBetweenMinorGreater = "<(.+)>";
//					Pattern pattern = Pattern.compile(emailBetweenMinorGreater, Pattern.CASE_INSENSITIVE);
//					Matcher matcher = pattern.matcher(recipient);
//				   
//				    if ( matcher.find() && matcher.groupCount() > 0) {
//				    	recipient = matcher.group(1);
//				    }
//				    
//				    Log.get().debug("Recipient end: " + recipient);
//				    
//				    /*
//				     * Found our address
//				     */
//				    if ( recipient.contains(SERVER_ADDRESS) ) {
//				    	String userPattern = "(.*)@";
//						pattern = Pattern.compile(userPattern, Pattern.CASE_INSENSITIVE);
//						matcher = pattern.matcher(recipient);
//						if ( matcher.find() && matcher.groupCount() > 0 ) {
//					    	place = matcher.group(1);
//					    }
//				    } else {
//				    	Log.get().warn(recipient + " does not contain " + SERVER_ADDRESS);
//				    }
//				} else {
//					Log.get().warn("Null recipient!");
//				}
//			}
//		}
//		Log.get().debug("Found place: " + place);
//		
//		return place;
//	}
}
