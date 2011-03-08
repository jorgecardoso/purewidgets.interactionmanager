package org.instantplaces.interactionmanager.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Router;

public class InteractionManagerApplication extends Application {
	
	private static final Logger log; 
	static {
		log = Logger.getLogger("InteractionManagerApplication");
		//log.setLevel(Level.ALL);
	}
	
	@Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
		// new instance of HelloWorldResource.
        Router router = new Router(getContext());
        
        this.getTunnelService().setExtensionsTunnel(true);
        this.getTunnelService().setMediaTypeParameter("output");
        this.getMetadataService().addExtension("jsonp", MediaType.APPLICATION_JAVASCRIPT);
        

        //router.attachDefault(HelloWorldResource.class);
        router.attach("/rest/", HelloWorldResource.class);
        //router.attach("/places/{placeid}/applications/{appid}/widgets/{widgetid}", WidgetResource.class);  
        
     // Attach the handlers to the root router  
       // router.attach("/users/{user}", account);  
        //router.attach("/users/{user}/orders", orders);  
        //router.attach("/users/{user}/orders/{order}", order);  
        return router;
	}    
}
