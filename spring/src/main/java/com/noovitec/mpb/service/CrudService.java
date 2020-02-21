package com.noovitec.mpb.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.BaseEntity;
import com.noovitec.mpb.repo.CrudRepo;

public interface CrudService {
	
	public BaseEntity merge(BaseEntity baseEntity);
	public BaseEntity save(BaseEntity baseEntity);
	public BaseEntity get(Long id);
	public void delete(Long id);
	public void flush();
	public void persist(BaseEntity baseEntity);
	
	@Transactional
	@Service
	public class CrudServiceImp implements CrudService {
		
		@Autowired
		EntityManager em;
		@Autowired
		CrudRepo crudRepo;

		public BaseEntity merge(BaseEntity baseEntity) {
			return em.merge(baseEntity);
		}
		
		public BaseEntity save(BaseEntity baseEntity) {
			return crudRepo.save(baseEntity);
		}

		public BaseEntity get(Long id) {
			return crudRepo.findById(id).get();
		}
		
		public void delete(Long id) {
			crudRepo.deleteById(id);
		}
		
		public void flush() {
			em.flush();
		}
		
		public void persist(BaseEntity baseEntity) {
			em.persist(baseEntity);
		}
	}
}
