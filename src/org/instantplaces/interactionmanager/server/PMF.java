package org.instantplaces.interactionmanager.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * See http://code.google.com/intl/pt-PT/appengine/docs/java/datastore/jdo/
 * 
 * @author Jorge C. S. Cardoso
 *
 */
public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}