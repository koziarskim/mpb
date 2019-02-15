package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {

	@Query("select c from Category c where type = :type")
	public List<Category> findAllByType(String type);

}