package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Season;

public interface SeasonRepo extends JpaRepository<Season, Long> {
}