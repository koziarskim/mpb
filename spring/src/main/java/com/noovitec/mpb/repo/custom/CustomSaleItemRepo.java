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
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.SaleItem;

public interface CustomSaleItemRepo {
	Page<SaleItem> findPageable(Pageable pageable, String numberName, Long customerId, Long itemId, boolean includeAll);

	@Repository
	public class SaleItemRepoImpl implements CustomSaleItemRepo {

		private final Logger log = LoggerFactory.getLogger(SaleItemRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<SaleItem> findPageable(Pageable pageable, String numberName, Long customerId, Long itemId, boolean hideShip) {
			String q = "select distinct si from SaleItem si " + "join si.item i " + "join si.sale s " + "join s.customer cu " + "where si.id is not null ";
			if (hideShip) {
				q += "and si.units <> si.unitsShipped ";
			}
			if (numberName != null && !numberName.isEmpty()) {
				q += "and (upper(s.number) like concat('%',upper(:numberName),'%') ";
				q += "or upper(s.name) like concat('%',upper(:numberName),'%')) ";
			}
			if (customerId != null) {
				q += "and cu.id = :customerId ";
			}
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}
			q += "order by si.updated desc ";
			Query query = entityManager.createQuery(q);
			if (numberName != null && !numberName.isEmpty()) {
				query.setParameter("numberName", numberName);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<SaleItem> result = query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize())
					.getResultList();
			Page<SaleItem> page = new PageImpl<SaleItem>(result, pageable, total);
			return page;
		}
	}
}