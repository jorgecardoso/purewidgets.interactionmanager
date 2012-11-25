package org.instantplaces.im.server.rest.representation.json;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class ApplicationOnscreenRest {
	/**
	 * Is this app currently on-screen?
	 */
	private boolean onScreen;

	
	public ApplicationOnscreenRest() {
		this.onScreen = false;
	}		
	
	/**
	 * @return the onScreen
	 */
	public boolean isOnScreen() {
		return onScreen;
	}

	/**
	 * @param onScreen the onScreen to set
	 */
	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}	
}
