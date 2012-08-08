package org.instantplaces.im.server.dao;


import java.util.ConcurrentModificationException;

import org.instantplaces.im.server.logging.Log;


import com.googlecode.objectify.ObjectifyOpts;

/**
 * DAO that encapsulates a single transaction.  Create it and forget about it.
 * Also provides very convenient static methods for making GAE/Python-like transactions.
 * 
 * @author Jeff Schnitzer
 */
public class DaoT extends Dao  // DAO is your class derived from DAOBase as described above
{
       
        
        /** Alternate interface to Runnable for executing transactions */
        public static interface Transactable
        {
                void run(DaoT daot);
        }
        
        /**
         * Provides a place to put the result too.  Note that the result
         * is only valid if the transaction completes successfully; otherwise
         * it should be ignored because it is not necessarily valid.
         */
        abstract public static class Transact<T> implements Transactable
        {
                protected T result;
                public T getResult() { return this.result; }
        }
        
        /** Create a default DAOT and run the transaction through it */
        public static void runInTransaction(Transactable t)
        {
                DaoT daot = new DaoT();
                daot.doTransaction(t);
        }
        
        /**
         * Run this task through transactions until it succeeds without an optimistic
         * concurrency failure.
         */
        public static void repeatInTransaction(Transactable t)
        {
                while (true)
                {
                        try
                        {
                                runInTransaction(t);
                                break;
                        }
                        catch (ConcurrentModificationException ex)
                        {
                        	Log.warn(DaoT.class.getName(), "Optimistic concurrency failure for " + t + ": " + ex);
                        }
                }
        }
        
        public DaoT() {
        	Dao.beginTransaction();
        }
      
        
        /**
         * Executes the task in the transactional context of this DAO/ofy.
         */
        public void doTransaction(final Runnable task)
        {
                this.doTransaction(new Transactable() {
                        @Override
                        public void run(DaoT daot)
                        {
                                task.run();
                        }
                });
        }

        /**
         * Executes the task in the transactional context of this DAO/ofy.
         */
        public void doTransaction(Transactable task)
        {
                try
                {
                        task.run(this);
                        ofy().getTxn().commit();
                }
                finally
                {
                        if (ofy().getTxn().isActive())
                                ofy().getTxn().rollback();
                }
        }
}