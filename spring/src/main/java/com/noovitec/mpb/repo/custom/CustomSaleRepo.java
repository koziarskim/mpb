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

import com.noovitec.mpb.entity.Sale;

public interface CustomSaleRepo {
	Page<Sale> findPagable(Pageable pageable, String searchKey, String searchType, boolean hideProd, boolean hideShip);

	@Repository
	public class CustomSaleRepoImpl implements CustomSaleRepo {

		private final Logger log = LoggerFactory.getLogger(CustomSaleRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Sale> findPagable(Pageable pageable, String searchKey, String searchType, boolean hideProd, boolean hideShip) {
			String q = "select distinct s from Sale s "
					+ "left join s.saleItems si "
					+ "left join si.item i "
					+ "where s.id is not null ";
			if(searchType.equals("sale") && !searchKey.isBlank()) {
				q += "and (upper(s.name) like concat('%',upper(:searchKey),'%') "
					+ "or upper(s.number) like concat('%',upper(:searchKey),'%')) ";
			}
			if(searchType.equals("item") && !searchKey.isBlank()) {
				q += "and (upper(i.name) like concat('%',upper(:searchKey),'%') "
						+ "or upper(i.number) like concat('%',upper(:searchKey),'%')) ";
			}
			if(hideProd) {
				q += "and s.unitsSold > s.unitsProduced ";
			}
			if(hideShip) {
				q += "and s.unitsSold > s.unitsShipped ";
			}
			q += "order by s.updated desc";
			Query query = entityManager.createQuery(q);
			if(searchType.equals("sale") && !searchKey.isBlank()) {
				query.setParameter("searchKey", searchKey);
			}
			if(searchType.equals("item") && !searchKey.isBlank()) {
				query.setParameter("searchKey", searchKey);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<Sale> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<Sale> page = new PageImpl<Sale>(result, pageable, total);
			return page;
		}
	}
}