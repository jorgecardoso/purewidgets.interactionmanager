package org.instantplaces.im.server;

import org.instantplaces.im.server.rest.resource.ApplicationResource;
import org.instantplaces.im.server.rest.resource.PlaceResource;
import org.instantplaces.im.server.rest.resource.WidgetInputResource;
import org.instantplaces.im.server.rest.resource.WidgetResource;
import org.instantplaces.im.server.rest.resource.channel.ChannelResource;
import org.instantplaces.im.server.rest.resource.cron.CronCheckTasksResource;
import org.instantplaces.im.server.rest.resource.cron.CronDeleteOldAppsResource;
import org.instantplaces.im.server.rest.resource.cron.CronDeleteOldInput;
import org.instantplaces.im.server.rest.resource.cron.CronDeleteVolatileResource;
import org.instantplaces.im.server.rest.resource.cron.CronRebuildCodesResource;
import org.instantplaces.im.server.rest.resource.io.SmsInput;
import org.instantplaces.im.server.rest.resource.task.TaskLogInputStatistics;
import org.instantplaces.im.server.rest.resource.task.TaskNotifyServerApp;
import org.instantplaces.im.server.rest.resource.task.TaskRemoveChannelClientId;
import org.instantplaces.im.server.rest.resource.task.TaskStoreChannelClientId;
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
        
        /*
         * WidgetInput
         */
        router.attach("/place/{placeid}/application/{appid}/widget/{widgetid}/input", WidgetInputResource.class);
        router.attach("/place/{placeid}/application/{appid}/input", WidgetInputResource.class);
     
        /*
         * Widget
         */
        router.attach("/place/{placeid}/application/{appid}/widget/{widgetid}", WidgetResource.class);
        router.attach("/place/{placeid}/application/{appid}/widget", WidgetResource.class);
        
      
        /*
         * Application
         */
        router.attach("/place/{placeid}/application/{appid}", ApplicationResource.class);
        router.attach("/place/{placeid}/application", ApplicationResource.class);
              
        /*
         * Place
         */
        router.attach("/place", PlaceResource.class);
        
        /*
         * Cron
         */
        router.attach("/cron/delete-volatile", CronDeleteVolatileResource.class);
        router.attach("/cron/rebuild-codes", CronRebuildCodesResource.class);
        router.attach("/cron/delete-old-apps", CronDeleteOldAppsResource.class);
        router.attach("/cron/check-tasks", CronCheckTasksResource.class);
        router.attach("/cron/delete-old-input", CronDeleteOldInput.class);
        
        /*
         * Tasks
         */
           
        router.attach("/task/log-input-statistics", TaskLogInputStatistics.class);
        router.attach("/task/notify-server-app", TaskNotifyServerApp.class);
        
        router.attach("/task/store-channel-clientid", TaskStoreChannelClientId.class);
        router.attach("/task/remove-channel-clientid", TaskRemoveChannelClientId.class);
        /*
         * IO modules
         */
        router.attach("/io/sms", SmsInput.class);
        
        /*
         * Channel
         */
        router.attach("/place/{placeid}/application/{appid}/channel", ChannelResource.class);
        
        return router;
	}    
}
