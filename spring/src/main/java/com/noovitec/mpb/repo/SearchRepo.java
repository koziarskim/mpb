package com.noovitec.mpb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.User;
import com.noovitec.mpb.repo.custom.CustomSearchRepo;

public interface SearchRepo extends PagingAndSortingRepository<User, Long>, CustomSearchRepo {

}