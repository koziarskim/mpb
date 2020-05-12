package com.noovitec.mpb.repo.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.entity.Purchase;

public interface CustomPurchaseRepo {
	Page<Purchase> findPagable(Pageable pageable, String purchaseName, Long componentId, Long supplierId, String status, String freightTerms);

	@Repository
	public class CustomPurchaseRepoImpl implements CustomPurchaseRepo {

		private final Logger log = LoggerFactory.getLogger(CustomPurchaseRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Purchase> findPagable(Pageable pageable, String purchaseName, Long componentId, Long supplierId, String status, String freightTerms) {
			String q = "select distinct p, (p.unitsPurchased - p.unitsReceived) as pending from Purchase p " 
					+ "left join p.purchaseComponents pc "
					+ "left join pc.component c "
					+ "left join p.supplier su "
					+ "where p.id is not null ";
			if (purchaseName !=null && !purchaseName.isBlank()) {
				q += "and (upper(p.number) like concat('%',upper(:purchaseName),'%') "
						+ "or upper(p.name) like concat('%',upper(:purchaseName),'%')) ";
			}
			if (componentId !=null) {
				q += "and c.id = :componentId ";
			}
			if (supplierId !=null) {
				q += "and su.id = :supplierId ";
			}
			if(freightTerms != null) {
				q += "and su.freightTerms = :freightTerms ";
			}
			if(status != null) {
				if("OPEN".equalsIgnoreCase(status)) {
					q += "and p.unitsPurchased > 0 and p.unitsReceived = 0 ";
				}
				if("PARTIAL".equalsIgnoreCase(status)) {
					q += "and p.unitsReceived > 0 and p.unitsReceived < p.unitsPurchased ";
				}
				if("RECEIVED".equalsIgnoreCase(status)) {
					q += "and p.unitsReceived > 0 and p.unitsReceived >= p.unitsPurchased ";
				}
			}
			q += "order by p.updated desc";
			Query query = entityManager.createQuery(q);
			if (purchaseName !=null && !purchaseName.isBlank()) {
				query.setParameter("purchaseName", purchaseName);
			}
			if (componentId !=null) {
				query.setParameter("componentId", componentId);
			}
			if(freightTerms != null) {
				query.setParameter("freightTerms", freightTerms);
			}
			if (supplierId !=null) {
				query.setParameter("supplierId", supplierId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<Object[]> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize()).getResultList();
			List<Purchase> entities = new ArrayList<Purchase>();
			result.forEach(o -> entities.add((Purchase) o[0]));
			Page<Purchase> page = new PageImpl<Purchase>(entities, pageable, total);
			return page;
		}
	}
}