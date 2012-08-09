package org.instantplaces.im.server.dao;

import java.util.ArrayList;

import javax.persistence.Id;

import org.instantplaces.im.server.Log;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

@Cached
public class ReferenceCodeGeneratorDAO {

	@NotSaved
	private static final int CHARACTER_COUNT = 3;

	@Unindexed
	private ArrayList<String> codes;

	@NotSaved
	private String symbols = "0123456789abcdefghijklmnopqrstuvwxyz";

	
	@NotSaved
	private PlaceDaoTmp place;

	@SuppressWarnings("unused")
	@Parent
	private Key<PlaceDaoTmp> placeKey;

	@SuppressWarnings("unused")
	@Id
	private Long referenceCodeGeneratorId;

	public ReferenceCodeGeneratorDAO(PlaceDaoTmp place) {
		codes = new ArrayList<String>(100);
		this.rebuild();
		// Log.get().debug("ReferenceCodeManager: " + this.codes.toString());
		this.setPlace(place);
	}

	@SuppressWarnings("unused")
	private ReferenceCodeGeneratorDAO() {
	}

	public ArrayList<String> getCodes() {
		return this.codes;
	}

	public String getNextCode() {
		if (null == codes || codes.size() < 1) {
			Log.get().error("No more codes to serve!");
			return null;
		}

		String i = codes.remove(0);
		return i;
	}

	public synchronized String getNextCodeAsString() {
		// TODO: Check nulls!!!!
		return  this.getNextCode();
	}

	/**
	 * @return the place
	 */
	public PlaceDaoTmp getPlace() {
		return place;
	}

	public void rebuild() {
		this.codes.clear();
		

		int maxIndex = this.symbols.length()-1;
		int []indexes = new int[CHARACTER_COUNT];
		for (int i = 0; i < indexes.length; i++ ) {
			indexes[i] = 0;
		}
		//indexes[0] = 10; // we enforce an alphabetic character on the code, so we might as well start with one.
		
		boolean end = false;
		
		StringBuilder sb = new StringBuilder();
		
		while ( !end ) {
			sb.setLength(0);
			
			int digitCount = 0;
			int letterCount = 0;
			for ( int i = 0; i < CHARACTER_COUNT; i++ ) {
				/*
				 * Count the number of digits and the number of letters
				 */
				if ( indexes[i] < 10 ) {
					digitCount++;
				} 
				if ( indexes[i] >= 10 ) {
					letterCount++;
				}
				sb.append( symbols.charAt(indexes[i]) );
			}
			
			/*
			 * We want to make sure that every reference code has at least one digit and one letter
			 */
			if (digitCount < CHARACTER_COUNT && letterCount < CHARACTER_COUNT) {
				codes.add(sb.toString());
			}
			
			
			for ( int i = 0; i < CHARACTER_COUNT; i++ ) {
				indexes[i]++;
				
				if ( indexes[i] > maxIndex ) {
					indexes[i] = 0;
				} else {
					break;
				}
			}
			
			end = true;
			for ( int i = 0; i < CHARACTER_COUNT; i++ ) {
				if ( indexes[i] < maxIndex ) {
					end = false;
				}
			}		
			
		}
		/*
		for (int i = 0; i < MAX_CODE; i++) {
			codes.add(new Integer(i));
		}*/
	}

	/*
	 * public static ReferenceCodeGenerator getFromDSO(PersistenceManager pm) {
	 * try { Extent e = pm.getExtent(ReferenceCodeGenerator.class);
	 * 
	 * Iterator iter=e.iterator();
	 * 
	 * 
	 * //List<Generator> results = (List<Generator>) query.execute();
	 * 
	 * if ( iter.hasNext() ) {
	 * Log.get().debug("Retrieved generator from data store.");
	 * 
	 * return (ReferenceCodeGenerator)iter.next();
	 * 
	 * } } catch (Exception e) { Log.get().error("Could not access data store."
	 * + e.getMessage()); } return null; }
	 */
	/*
	 * private static void loadGeneratorFromDatastore() { PersistenceManager pm
	 * = PMF.get().getPersistenceManager();
	 * 
	 * //Query query = pm.newQuery(Generator.class);
	 * //query.setFilter("id == idParam");
	 * //query.declareParameters("String idParam");
	 * Log.get().debug("Retrieving generator from data store."); try { Extent e
	 * = pm.getExtent(Generator.class);
	 * 
	 * Iterator iter=e.iterator();
	 * 
	 * 
	 * //List<Generator> results = (List<Generator>) query.execute();
	 * 
	 * if ( iter.hasNext() ) {
	 * Log.get().debug("Retrieved generator from data store.");
	 * 
	 * gen = (Generator)iter.next();
	 * 
	 * } else {
	 * Log.get().debug("Generator not found in data store, creating..."); gen =
	 * new Generator(); ArrayList<Integer>codes = new ArrayList<Integer>(1000);
	 * for (int i = 0; i < 1000; i++) { codes.add(new Integer(i)); }
	 * gen.setCodes(codes); pm.makePersistent(gen); } } catch (Exception e) {
	 * Log.get().error("Could not access data store." + e.getMessage()); }
	 * finally { // query.closeAll(); } }
	 */

	public void recycleCode(WidgetOptionDao widgetOption) {
		if ( !widgetOption.isRecyclable() ) {
			return;
		}
		if ( !this.codes.contains(widgetOption.getReferenceCode()) ) {
				this.codes.add(widgetOption.getReferenceCode());
			}

	}

	public void remove(String code) {

			this.codes.remove(code);

		
	}

	public void setCodes(ArrayList<String> codes) {
		this.codes = codes;

	}

	/**
	 * @param place
	 *            the place to set
	 */
	public void setPlace(PlaceDaoTmp place) {
		this.place = place;
		if (null != place) {
			this.placeKey = new Key<PlaceDaoTmp>(PlaceDaoTmp.class, place.getPlaceId());
		}
	}
}
