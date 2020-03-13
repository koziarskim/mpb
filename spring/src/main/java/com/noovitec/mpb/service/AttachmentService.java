package com.noovitec.mpb.service;

import java.io.File;
import java.io.IOException;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.DocContent;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.DocContentRepo;

public interface AttachmentService {

	public Attachment save(Attachment attachment, byte[] data) throws IOException;
	public Attachment getWithDocContent(Long attachmentId);
	public Attachment getById(Long attachmentId);
	public Attachment store(MultipartFile file, String type) throws IllegalStateException, IOException;

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
		
		public Attachment store(MultipartFile file, String type) throws IllegalStateException, IOException {
			String fileName = file.getOriginalFilename();
			String filePath = "/"+fileName;
			String fullPath = System.getenv("MPB_FILE_STORE_DIR")+filePath;
			file.transferTo(new File(fullPath));
			Attachment attachment = new Attachment();
			attachment.setFilePath(filePath);
			attachment.setMimeType(file.getContentType());
			attachment.setName(fileName);
			attachment.setType(type);
			attachmentRepo.save(attachment);
			return attachment;
		}
		
	}
}
