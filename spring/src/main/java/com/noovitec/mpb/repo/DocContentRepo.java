package com.noovitec.mpb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.entity.DocContent;

public interface DocContentRepo extends JpaRepository<DocContent, Long> {
	
	@Query("select dc from DocContent dc "
			+ "join Attachment a on a.docContentId=dc.id "
			+ "where a.id = :attachmentId")
	public DocContent getByAttachmentId(Long attachmentId);

}