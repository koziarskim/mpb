package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noovitec.mpb.entity.Attachment;

public interface AttachmentRepo extends JpaRepository<Attachment, Long> {
}