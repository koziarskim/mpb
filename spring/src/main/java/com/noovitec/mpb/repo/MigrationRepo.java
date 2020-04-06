package com.noovitec.mpb.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.custom.CustomMigrationRepo;

public interface MigrationRepo extends PagingAndSortingRepository<Item, Long>, CustomMigrationRepo {

}