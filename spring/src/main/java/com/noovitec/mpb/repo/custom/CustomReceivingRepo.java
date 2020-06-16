package com.noovitec.mpb.repo.custom;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.noovitec.mpb.entity.Receiving;

public interface CustomReceivingRepo {

	public Page<?> findPagable(Pageable pageable, boolean totals, Long purchaseId, Long componentId, Long supplierId, String invoiceNumber, String packingList,
			LocalDate receivedFrom, LocalDate receivedTo);
	public Receiving getLastByComponent(Long componentId);
	
	@Repository
	public class CustomReceivingRepoImpl implements CustomReceivingRepo {

		private final Logger log = LoggerFactory.getLogger(CustomReceivingRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<?> findPagable(Pageable pageable, boolean totals, Long purchaseId, Long componentId, Long supplierId, String invoiceNumber, String packingList,
				LocalDate receivedFrom, LocalDate receivedTo) {
			Query query = getQuery(pageable, totals, purchaseId, componentId, supplierId, invoiceNumber, packingList,
				receivedFrom, receivedTo);
			long total = query.getResultStream().count();
			if(totals) {
				@SuppressWarnings("unchecked")
				List<Object> result = query.getResultList();
				Page<Object> page = new PageImpl<Object>(result, pageable, total);
				return page;
			}else {
				@SuppressWarnings("unchecked")
				List<Receiving> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
				Page<Receiving> page = new PageImpl<Receiving>(result, pageable, total);
				return page;
			}
		}
		
		
		private Query getQuery(Pageable pageable, boolean totals, Long purchaseId, Long componentId, Long supplierId, String invoiceNumber, String packingList,
				LocalDate receivedFrom, LocalDate receivedTo) {
			String q = "";
			if(totals) {
				q += "select distinct sum(r.totalPrice), sum(r.units), sum(r.unitPrice) ";
			}else {
				q += "select distinct r ";
			}
			q += "from Receiving r " 
					+ "left join r.purchaseComponent pc " 
					+ "left join pc.purchase p " 
					+ "left join pc.component c "
					+ "left join p.supplier su " 
					+ "where r.id is not null ";
			if (purchaseId !=null) {
				q += "and p.id = :purchaseId ";
			}
			if (componentId !=null) {
				q += "and c.id = :componentId ";
			}
			if (supplierId !=null) {
				q += "and su.id = :supplierId ";
			}
			if (invoiceNumber !=null && !invoiceNumber.isBlank()) {
				q += "and upper(r.invoiceNumber) = upper(:invoiceNumber) ";
			}
			if (packingList !=null && !packingList.isBlank()) {
				q += "and upper(r.number) = upper(:packingList) ";
			}
			if(receivedFrom != null) {
				q += "and r.receivingDate >= :receivedFrom ";
			}
			if(receivedTo !=null) {
				q += "and r.receivingDate <= :receivedTo ";
			}
			if(!totals) {
				q += "order by r.updated desc";
			}
			Query query = entityManager.createQuery(q);
			if (purchaseId != null) {
				query.setParameter("purchaseId", purchaseId);
			}
			if (componentId !=null) {
				query.setParameter("componentId", componentId);
			}
			if(supplierId != null) {
				query.setParameter("supplierId", supplierId);
			}
			if (invoiceNumber !=null && !invoiceNumber.isBlank()) {
				query.setParameter("invoiceNumber", invoiceNumber);
			}
			if (packingList !=null && !packingList.isBlank()) {
				query.setParameter("packingList", packingList);
			}
			if(receivedFrom !=null) {
				query.setParameter("receivedFrom", receivedFrom);
			}
			if(receivedTo !=null) {
				query.setParameter("receivedTo", receivedTo);
			}
			return query;
		}

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