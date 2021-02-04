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

import com.noovitec.mpb.entity.ComponentAdjustment;

public interface CustomComponentAdjustmentRepo {
	Page<ComponentAdjustment> findPage(Pageable pageable, Long componentId);

	@Repository
	public class CustomComponentAdjustmentRepoImpl implements CustomComponentAdjustmentRepo {

		private final Logger log = LoggerFactory.getLogger(CustomComponentAdjustmentRepoImpl.class);

		@PersistenceContext
		EntityManager entityManager;

		@Override
		public Page<ComponentAdjustment> findPage(Pageable pageable, Long componentId) {
			String q = "select distinct ca from ComponentAdjustment ca "
					+ "left join ca.component c "
					+ "where ca.id is not null ";
			if (componentId != null) {
				q += "and c.id = :componentId ";
			}
			q += "order by ca.date asc ";
			Query query = entityManager.createQuery(q);
			if (componentId != null) {
				query.setParameter("componentId", componentId);
			}
			long total = query.getResultStream().count();
			@SuppressWarnings("unchecked")
			List<ComponentAdjustment> result = query.setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
				.setMaxResults(pageable.getPageSize()).getResultList();
			Page<ComponentAdjustment> page = new PageImpl<ComponentAdjustment>(result, pageable, total);
			return page;
		}
	}
}