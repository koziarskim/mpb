package com.noovitec.mpb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.User;
import com.noovitec.mpb.repo.interfaces.SearchRepoCustom;

public interface SearchRepo extends PagingAndSortingRepository<User, Long>, SearchRepoCustom {

}