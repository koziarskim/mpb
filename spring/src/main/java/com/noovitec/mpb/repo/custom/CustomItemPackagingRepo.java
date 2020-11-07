package com.noovitec.mpb.repo.custom;

import java.util.ArrayList;
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

import com.noovitec.mpb.entity.ItemPackaging;

public interface CustomItemPackagingRepo {
	Page<ItemPackaging> findPageable(Pageable pageable, Long itemId, Long packagingId);

	@Repository
	public class ItemPackagingRepoImpl implements CustomItemPackagingRepo {

		private final Logger log = LoggerFactory.getLogger(ItemPackagingRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<ItemPackaging> findPageable(Pageable pageable, Long itemId, Long packagingId) {
			List<Long> ids = this.getIds(pageable, itemId, packagingId);
			Query query = entityManager.createQuery("select count(*) from ItemPackaging ip where ip.id in :ids");
			query.setParameter("ids", ids);
			long total = (long) query.getSingleResult();
			String q = "select distinct ip, i.number from ItemPackaging ip " 
					+ "join ip.item i " 
					+ "join ip.packaging p " 
					+ "where ip.id in :ids ";
			q += "order by i.number asc ";
			query = entityManager.createQuery(q);
			query.setParameter("ids", ids);
			@SuppressWarnings("unchecked")
			List<Object[]> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			List<ItemPackaging> entities = new ArrayList<ItemPackaging>();
			result.forEach(o -> entities.add((ItemPackaging) o[0]));
			Page<ItemPackaging> page = new PageImpl<ItemPackaging>(entities, pageable, total);
			return page;
		}
		
		private List<Long> getIds(Pageable pageable, Long itemId, Long packagingId) {
			String q = "select distinct ip.id from ItemPackaging ip " 
					+ "join ip.item i " 
					+ "join ip.packaging p " 
					+ "where ip.id is not null ";
			if (itemId != null) {
				q += "and i.id = :itemId ";
			}			
			if (packagingId != null) {
				q += "and p.id = :packagingId ";
			}			
			Query query = entityManager.createQuery(q);
			if (itemId != null) {
				query.setParameter("itemId", itemId);
			}
			if (packagingId != null) {
				query.setParameter("packagingId", packagingId);
			}
			@SuppressWarnings("unchecked")
			List<Long> ids = query.getResultList();
			if(ids.size()==0) {
				ids.add(0L);
			}
			return ids;
		}

	}
}