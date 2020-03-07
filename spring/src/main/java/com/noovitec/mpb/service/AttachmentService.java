package com.noovitec.mpb.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.DocContent;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.DocContentRepo;

public interface AttachmentService {

	public Attachment save(Attachment attachment, byte[] data) throws IOException;
	public Attachment getWithDocContent(Long attachmentId);
	public Attachment getById(Long attachmentId);

	@Transactional
	@Service("attachmentServiceImpl")
	public class AttachmentServiceImp implements AttachmentService {

		private final Logger log = LoggerFactory.getLogger(AttachmentServiceImp.class);
		private AttachmentRepo attachmentRepo;
		@Autowired
		private DocContentRepo docContentRepo;

		public AttachmentServiceImp(AttachmentRepo attachmentRepo) {
			this.attachmentRepo = attachmentRepo;
		}
		
		public Attachment getWithDocContent(Long attachmentId) {
			if(attachmentId == null) {
				return null;
			}
			Attachment attachment = attachmentRepo.getOne(attachmentId);
			DocContent docContent = docContentRepo.getByAttachmentId(attachmentId);
			attachment.setDocContent(docContent);
			return attachment;
		}

		public Attachment getById(Long attachmentId) {
			if(attachmentId == null) {
				return null;
			}
			Attachment attachment = attachmentRepo.getOne(attachmentId);
			return attachment;
		}

		public Attachment save(Attachment attachment, byte[] data) throws IOException {
			DocContent docContent = null;
			if(attachment == null) {
				attachment = new Attachment();
			}
			if(attachment.getId() != null) {
				docContent = docContentRepo.getByAttachmentId(attachment.getId());
			}
			if(docContent == null) {
				docContent = new DocContent();
			}
			docContent.setData(data);
			docContent = docContentRepo.save(docContent);
			attachment.setDocContentId(docContent.getId());
			return attachmentRepo.save(attachment);
		}
		
	}
}
