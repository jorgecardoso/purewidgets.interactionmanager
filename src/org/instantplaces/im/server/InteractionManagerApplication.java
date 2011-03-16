package org.instantplaces.im.server;

import org.instantplaces.im.server.resource.WidgetInputResource;
import org.instantplaces.im.server.resource.WidgetResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Router;

public class InteractionManagerApplication extends Application {
	
		
	@Override
    public Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
		// new instance of HelloWorldResource.
        Router router = new Router(getContext());
        
        //this.getTunnelService().setExtensionsTunnel(true);
        this.getTunnelService().setMediaTypeParameter("output");
        
        //this.getMetadataService().clearExtensions();
        
        this.getMetadataService().addExtension("jsonp", MediaType.APPLICATION_JAVASCRIPT, true);
        
        //this.getMetadataService().
        
        //this.getMetadataService().addCommonExtensions()
        //router.attachDefault(HelloWorldResource.class);

        router.attach("/place/{placeid}/application/{appid}/widget/{widgetid}", WidgetResource.class);
        router.attach("/place/{placeid}/application/{appid}/widget", WidgetResource.class);
        
        router.attach("/place/{placeid}/application/{appid}/widget/{widgetid}/input", WidgetInputResource.class);
        router.attach("/place/{placeid}/application/{appid}/input", WidgetInputResource.class);
     
        return router;
	}    
}
