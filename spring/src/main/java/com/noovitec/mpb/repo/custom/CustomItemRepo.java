package com.noovitec.mpb.repo.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import com.noovitec.mpb.entity.Item;

public interface CustomItemRepo {
	Page<Item> findPagable(Pageable pageable, String searchKey, String searchType);

	@Repository
	public class CustomItemRepoImpl implements CustomItemRepo {

		private final Logger log = LoggerFactory.getLogger(CustomItemRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<Item> findPagable(Pageable pageable, String searchKey, String searchType) {
			int count = Integer.valueOf(entityManager.createQuery("select count(*) from Item").getSingleResult().toString());
			String q = "select distinct i from Item i "
					+ "left join i.itemComponents ic "
					+ "left join ic.component c "
					+ "where i.id is not null ";
			if(searchType.equals("item") && !searchKey.isBlank()) {
				q += "and upper(i.name) like concat('%',upper(:searchKey),'%') "
					+ "or upper(i.number) like concat('%',upper(:searchKey),'%') ";
			}
			if(searchType.equals("component") && !searchKey.isBlank()) {
				q += "and upper(c.name) like concat('%',upper(:searchKey),'%') "
						+ "or upper(c.number) like concat('%',upper(:searchKey),'%') ";
			}
			q += "order by i.name asc";
			Query query = entityManager.createQuery(q);
			if(searchType.equals("item") && !searchKey.isBlank()) {
				query.setParameter("searchKey", searchKey);
			}
			if(searchType.equals("component") && !searchKey.isBlank()) {
				query.setParameter("searchKey", searchKey);
			}
			@SuppressWarnings("unchecked")
			List<Item> list = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
					.setMaxResults(pageable.getPageSize()).getResultList();
			Page<Item> page = new PageImpl<Item>(list, pageable, count);
			return page;
		}
	}
}