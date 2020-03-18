package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Notification;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
	

}