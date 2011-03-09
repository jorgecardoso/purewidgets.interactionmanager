package org.instantplaces.interactionmanager.client;



import org.instantplaces.interactionmanager.shared.Contact;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class InteractionManager implements EntryPoint {
	
	
	
	RequestBuilder rb;
	String url = "/rest/?output=json";
	
	Label l; 
	

	  
	@Override
	public void onModuleLoad() {
		//Log.setCurrentLogLevel(Log.LOG_LEVEL_DEBUG);
		l = new Label("sdfsd");
		RootPanel.get().add(l);
		
		ContactJSON c = ContactJSON.getNew();//ContactJSON.fromJSON("{\"name\":\"jorge\", \"age\":3, \"height\":4}");
		c.setName("jorge");
		c.setEmail("jorge@ieee.com");
		
		rb = new RequestBuilder(RequestBuilder.PUT, url);
		rb.setHeader("Content-type", "application/json");
		try {
		      Request request = rb.sendRequest(c.toJSON(), new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	Log.error("Couldn't retrieve JSON");
		        	l.setText("Couldn't retrieve JSON");
		        }

		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()) {
		        	  Log.info(response.getText());
		        	  if (response.getText() != null && response.getText().length()>0) {
		        	 Contact c = ContactJSON.fromJSON(response.getText());
		        	 
		        	 l.setText(c.getName());
		        	  } else {
		        		  Log.warn("Received empty object");
		        	  }
		          } else {
		        	  if (response.getText() != null && response.getText().length()>0) {
		        		  ErrorJSON c = ErrorJSON.fromJSON(response.getText());
				        	 
				         l.setText(c.getErrorMessage());
				        }
		        	  Log.warn("Oops " + response.getStatusText());
		        	  
		            }
		        }
		      });
		    } catch (RequestException e) {
		    	l.setText(e.getMessage());
		    }
		    
		/*
		rb = new RequestBuilder(RequestBuilder.GET, url);
		try {
		      Request request = rb.sendRequest(null, new RequestCallback() {
		        public void onError(Request request, Throwable exception) {
		        	Log.error("Couldn't retrieve JSON");
		        	l.setText("Couldn't retrieve JSON");
		        }

		        public void onResponseReceived(Request request, Response response) {
		          if (200 == response.getStatusCode()) {
		        	  Log.info(response.getText());
		        	 Contact c = ContactJSON.fromJSON(response.getText());
		        	 l.setText(c.getName());
		          } else {
		        	  Log.warn("Oops " + response.getStatusText());
		        	  l.setText(response.getStatusText());
		            }
		        }
		      });
		    } catch (RequestException e) {
		    	l.setText(e.getMessage());
		    }
		    */
	}

}
