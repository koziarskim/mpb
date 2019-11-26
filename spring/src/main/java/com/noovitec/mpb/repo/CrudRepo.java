package com.noovitec.mpb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.BaseEntity;

public interface CrudRepo extends PagingAndSortingRepository<BaseEntity, Long> {


}