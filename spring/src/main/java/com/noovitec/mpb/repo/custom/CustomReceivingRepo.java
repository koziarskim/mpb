package com.noovitec.mpb.repo.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Receiving;

public interface CustomReceivingRepo {

	public Receiving getLastByComponent(Long componentId);
	
	@Repository
	public class CustomReceivingRepoImpl implements CustomReceivingRepo {

		private final Logger log = LoggerFactory.getLogger(CustomReceivingRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;


		public Receiving getLastByComponent(Long componentId) {
			String q = "select r from Receiving r "
					+ "join r.purchaseComponent pc "
					+ "join pc.component c "
					+ "where c.id = :componentId "
					+ "order by r.updated desc";
			Query query = entityManager.createQuery(q);
			query.setParameter("componentId", componentId);
			query.setMaxResults(1);
			Receiving receiving = (Receiving) query.getSingleResult();
			return receiving;
		}
	}
}