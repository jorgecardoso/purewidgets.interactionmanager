/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * @author "Jorge C. S. Cardoso"
 *
 */

@XmlRootElement(name="applications")
public class ApplicationArrayListREST {
	@XmlElement(name = "application")
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
