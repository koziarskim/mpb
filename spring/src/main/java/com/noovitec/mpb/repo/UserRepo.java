package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.noovitec.mpb.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

	public User getByUsername(@Param("username") String username);

}