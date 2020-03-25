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

import com.noovitec.mpb.entity.Invoice;

public interface CustomInvoiceRepo {
	Page<Invoice> findPagable(Pageable pageable);

	@Repository
	public class CustomInvoiceRepoImpl implements CustomInvoiceRepo {

		private final Logger log = LoggerFactory.getLogger(CustomInvoiceRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Invoice> findPagable(Pageable pageable) {
			String q = "select distinct inv from Invoice inv "
					+ "where inv.id is not null ";
			Order order = pageable.getSort().iterator().next();
			q += "order by inv."+order.getProperty() + " "+order.getDirection();
			Query query = entityManager.createQuery(q);
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<Invoice> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<Invoice> page = new PageImpl<Invoice>(result, pageable, total);
			return page;
		}
	}
}