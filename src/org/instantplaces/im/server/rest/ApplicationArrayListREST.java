/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */


public class ApplicationArrayListREST {

	public ArrayList<ApplicationREST> applications;
	
	public ApplicationArrayListREST() {
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ApplicationREST a : applications) {
			sb.append(a.toString());
		}
		return sb.toString();
	}
}
