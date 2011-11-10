package org.instantplaces.im.server.dao;

import java.util.ArrayList;

import javax.persistence.Id;

import org.instantplaces.im.server.Log;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Unindexed;

public class ReferenceCodeGeneratorDAO {

	@NotSaved
	private static final int MAX_CODE = 500;

	@Unindexed
	private ArrayList<Integer> codes;

	@NotSaved
	private PlaceDao place;

	@SuppressWarnings("unused")
	@Parent
	private Key<PlaceDao> placeKey;

	@SuppressWarnings("unused")
	@Id
	private Long referenceCodeGeneratorId;

	public ReferenceCodeGeneratorDAO(PlaceDao place) {
		codes = new ArrayList<Integer>(1000);
		this.rebuild();
		// Log.get().debug("ReferenceCodeManager: " + this.codes.toString());
		this.setPlace(place);
	}

	@SuppressWarnings("unused")
	private ReferenceCodeGeneratorDAO() {
	}

	public ArrayList<Integer> getCodes() {
		return this.codes;
	}

	public int getNextCode() {
		if (null == codes || codes.size() < 1) {
			Log.get().error("No more codes to serve!");
			return 0;
		}

		Integer i = codes.remove(0);
		return i.intValue();
	}

	public synchronized String getNextCodeAsString() {
		// TODO: Check nulls!!!!
		return String.format("%03d", this.getNextCode());
	}

	/**
	 * @return the place
	 */
	public PlaceDao getPlace() {
		return place;
	}

	public void rebuild() {
		this.codes.clear();
		for (int i = 0; i < MAX_CODE; i++) {
			codes.add(new Integer(i));
		}
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

	public void recycleCode(String code) {
		try {
			Integer c = Integer.valueOf(code);
			if (!this.codes.contains(c)) {
				this.codes.add(c);
			}

		} catch (NumberFormatException e) {
			Log.get().error(e.getMessage());
			e.printStackTrace();

		}
	}

	public void remove(String code) {
		try {
			Integer c = Integer.valueOf(code);
			this.codes.remove(c);

		} catch (NumberFormatException e) {
			Log.get().error(e.getMessage());
			e.printStackTrace();

		}
	}

	public void setCodes(ArrayList<Integer> codes) {
		this.codes = codes;

	}

	/**
	 * @param place
	 *            the place to set
	 */
	public void setPlace(PlaceDao place) {
		this.place = place;
		if (null != place) {
			this.placeKey = new Key<PlaceDao>(PlaceDao.class, place.getPlaceId());
		}
	}
}
