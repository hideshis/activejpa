/**
 * 
 */
package org.activejpa.jpa;

import javax.persistence.EntityManager;

/**
 * @author ganeshs
 *
 */
public class JPAContext {

	private final JPAConfig config;
	
	private final boolean readOnly;
	
	private EntityManager entityManager;
	
	public JPAContext(JPAConfig jpaConfig, boolean readOnly) {
		this.config = jpaConfig;
		this.readOnly = readOnly;
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = config.getEntityManagerProvider().getEntityManager();
			if (readOnly) {
				entityManager.setProperty("readOnly", readOnly);
			}
		}
		return entityManager;
	}
	
	public void close() {
		try {
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.close();
			}
		} finally {
			config.clearContext();
		}
	}
	
	public void beginTxn() {
		if (isTxnOpen()) {
			return;
		}
		getEntityManager().getTransaction().begin();
	}
	
	public void closeTxn(boolean rollback) {
		if (isTxnOpen()) {
			if (rollback || readOnly || entityManager.getTransaction().getRollbackOnly()) {
				entityManager.getTransaction().rollback();
			} else {
				entityManager.getTransaction().commit();
			}
		}
	}
	
	public boolean isTxnOpen() {
		return entityManager != null && entityManager.isOpen() && entityManager.getTransaction() != null && entityManager.getTransaction().isActive();
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
}
