/**
 * 
 */
package org.instantplaces.im.server.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.instantplaces.im.server.dso.ApplicationDSO;


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
	
	public static ApplicationArrayListREST fromDSO(ArrayList<ApplicationDSO> applicationDSOList) {
		
		ArrayList<ApplicationREST> applicationListREST = new ArrayList<ApplicationREST>();
		for ( ApplicationDSO aDSO : applicationDSOList ) {
			ApplicationREST aREST = ApplicationREST.fromDSO(aDSO);
			applicationListREST.add(aREST);
		}
		
		ApplicationArrayListREST applicationArrayList= new ApplicationArrayListREST();
		applicationArrayList.applications = applicationListREST;
		return applicationArrayList;
	}
}
