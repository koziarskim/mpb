package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Group;
import com.noovitec.mpb.entity.User;

public interface UserRepo extends JpaRepository<User, String> {
	
	List<Group> findAllById(String id);
}