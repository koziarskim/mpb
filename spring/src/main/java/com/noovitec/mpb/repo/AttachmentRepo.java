package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.Attachment;

public interface AttachmentRepo extends JpaRepository<Attachment, Long> {
	
	@Query("select a from Attachment a where a.id in :attachmentIds")
	public List<Attachment> findByIds(Long[] attachmentIds);
	
}