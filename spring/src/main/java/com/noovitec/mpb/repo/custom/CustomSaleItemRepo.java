package com.noovitec.mpb.repo.custom;

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

import com.noovitec.mpb.entity.SaleItem;

public interface CustomSaleItemRepo {
	public Page<?> getTotals(Pageable pageable, String numberName, Long saleId, Long customerId, Long itemId, 
			String status, String unitsFilter, boolean showAll);
	Page<SaleItem> findPageable(Pageable pageable, String numberName, Long saleId, Long customerId, Long itemId, String status,
			String unitsFilter, boolean showAll);

	@Repository
	public class SaleItemRepoImpl implements CustomSaleItemRepo {

		private final Logger log = LoggerFactory.getLogger(SaleItemRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> getTotals(Pageable pageable, String numberName, Long saleId, Long customerId, Long itemId, 
				String status, String unitsFilter, boolean showAll) {
			String q = "select distinct si.id from SaleItem si " 
					+ "join si.item i " 
					+ "join si.sale s " 
					+ "join s.customer cu " 
					+ "where si.id is not null ";
				if (numberName != null && !numberName.isEmpty()) {
					q += "and (upper(s.number) like concat('%',upper(:numberName),'%') ";
					q += "or upper(s.name) like concat('%',upper(:numberName),'%')) ";
				}
				if (saleId != null) {
					q += "and s.id = :saleId ";
				}
				if (customerId != null) {
					q += "and cu.id = :customerId ";
				}
				if (itemId != null) {
					q += "and i.id = :itemId ";
				}
				if (status !=null && !status.isBlank()) {
					q += "and si.status = :status ";
				}
				if ("ON_STOCK".equalsIgnoreCase(unitsFilter)) {
					q += "and si.unitsOnStock > 0 ";
				}
				if ("RFP_ONLY".equalsIgnoreCase(unitsFilter)) {
					q += "and i.unitsReadyProd > 0 ";
				}
				if (!showAll) {
					q += "and s.cancelled = false and s.paidInFull = false ";
				}
				Query query = entityManager.createQuery(q);
				if (numberName != null && !numberName.isEmpty()) {
					query.setParameter("numberName", numberName);
				}
				if (saleId != null) {
					query.setParameter("saleId", saleId);
				}
				if (customerId != null) {
					query.setParameter("customerId", customerId);
				}
				if (itemId != null) {
					query.setParameter("itemId", itemId);
				}
				if (status != null && !status.isBlank()) {
					query.setParameter("status", status);
				}
			List<Long> ids = query.getResultList();
			
			q = "select sum(si.units), sum(si.unitsProduced) from SaleItem si where si.id in :ids ";
			query = entityManager.createQuery(q);
			query.setParameter("ids", ids);
			long total = query.getResultStream().count();
			Object result = query.getSingleResult();
			Page<Object> page = new PageImpl<Object>(Arrays.asList(result), pageable, total);
			return page;
		}
		
		@Override
		public Page<SaleItem> findPageable(Pageable pageable, String numberName, Long saleId, Long customerId, Long itemId, 
				String status, String unitsFilter, boolean showAll) {
			String q = "select distinct si from SaleItem si " 
				+ "join si.item i " 
				+ "join si.sale s " 
				+ "join s.customer cu " 
				+ "where si.id is not null ";
			if (numberName != null && !numberName.isEmpty()) {
				q += "and (upper(s.number) like concat('%',upper(:numberName),'%') ";
				q += "or upper(s.name) like concat('%',upper(:numberName),'%')) ";
			}
			if (saleId != null) {
				q += "and s.id = :saleId ";
			}
			if (customerId != null) {
				q += "and cu.id = :customerId ";
			}
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}
			if (status !=null && !status.isBlank()) {
				q += "and si.status = :status ";
			}
			if ("ON_STOCK".equalsIgnoreCase(unitsFilter)) {
				q += "and si.unitsOnStock > 0 ";
			}
			if ("RFP_ONLY".equalsIgnoreCase(unitsFilter)) {
				q += "and i.unitsReadyProd > 0 ";
			}
			if (!showAll) {
				q += "and s.cancelled = false and s.paidInFull = false ";
			}
			q += "order by si.updated desc ";
			Query query = entityManager.createQuery(q);
			if (numberName != null && !numberName.isEmpty()) {
				query.setParameter("numberName", numberName);
			}
			if (saleId != null) {
				query.setParameter("saleId", saleId);
			}
			if (customerId != null) {
				query.setParameter("customerId", customerId);
			}
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (status != null && !status.isBlank()) {
				query.setParameter("status", status);
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