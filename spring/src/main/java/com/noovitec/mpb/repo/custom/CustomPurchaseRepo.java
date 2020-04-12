package com.noovitec.mpb.repo.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Purchase;

public interface CustomPurchaseRepo {
	Page<Purchase> findPagable(Pageable pageable, String purchaseName, Long componentId);

	@Repository
	public class CustomPurchaseRepoImpl implements CustomPurchaseRepo {

		private final Logger log = LoggerFactory.getLogger(CustomPurchaseRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Purchase> findPagable(Pageable pageable, String purchaseName, Long componentId) {
			String q = "select p from Purchase p " 
					+ "join p.purchaseComponents pc "
					+ "join pc.component c "
					+ "where p.id is not null ";
			if (purchaseName !=null && !purchaseName.isBlank()) {
				q += "and (upper(p.number) like concat('%',upper(:purchaseName),'%') "
						+ "or upper(p.name) like concat('%',upper(:purchaseName),'%')) ";
			}
			if (componentId !=null) {
				q += "and c.id = :componentId ";
			}
			Order order = pageable.getSort().iterator().next();
			q += "order by p."+order.getProperty() + " "+order.getDirection();
			Query query = entityManager.createQuery(q);
			if (purchaseName !=null && !purchaseName.isBlank()) {
				query.setParameter("purchaseName", purchaseName);
			}
			if (componentId !=null) {
				query.setParameter("componentId", componentId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<Purchase> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
			Page<Purchase> page = new PageImpl<Purchase>(result, pageable, total);
			return page;
		}
	}
}