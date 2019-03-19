package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

}