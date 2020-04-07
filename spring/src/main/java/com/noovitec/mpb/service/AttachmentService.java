package com.noovitec.mpb.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.repo.AttachmentRepo;

public interface AttachmentService {

	public Attachment getById(Long attachmentId);
	public Attachment store(MultipartFile file, String type, Long entityId, Attachment attachment) throws IllegalStateException, IOException;
	public Path load(Long attachmentId) throws MalformedURLException;

	@Transactional
	@Service("attachmentServiceImpl")
	public class AttachmentServiceImp implements AttachmentService {

		private final static String defaultSystemPath = "/home/koziarskim/mpb/mpb_file_store";
		private final static String systemPathVariable = System.getenv("MPB_FILE_STORE_DIR");
		private final Logger log = LoggerFactory.getLogger(AttachmentServiceImp.class);
		private AttachmentRepo attachmentRepo;

		public AttachmentServiceImp(AttachmentRepo attachmentRepo) {
			this.attachmentRepo = attachmentRepo;
		}
		
		public Attachment getById(Long attachmentId) {
			if(attachmentId == null) {
				return null;
			}
			Attachment attachment = attachmentRepo.getOne(attachmentId);
			return attachment;
		}

		public Path load(Long attachmentId) throws MalformedURLException {
			Attachment attachment = this.getById(attachmentId);
			String systemPath = systemPathVariable;
			log.info("System Path: "+systemPath);
			if(systemPath == null) {
				systemPath = defaultSystemPath;
			}
			Path path = Paths.get(systemPath+attachment.getFilePath()+"/"+attachment.getName());
			if(!Files.exists(path)) {
				return null;
			}
			return path;
		}
		
		public Attachment store(MultipartFile file, String type, Long entityId, Attachment attachment) throws IllegalStateException, IOException {
			String fileName = file.getOriginalFilename();
			String systemPath = systemPathVariable;
			log.info("System Path: "+systemPath);
			if(systemPath == null) {
				systemPath = defaultSystemPath;
			}
			String dirPath = null;
			if(type.equalsIgnoreCase("Item") || type.contentEquals("Component")) {
				String sharedDirPath = "/Shared/"+type+"/"+entityId+"/";
				File sharedDir = new File(systemPath+sharedDirPath);
			    if (! sharedDir.exists()){
			    	sharedDir.mkdirs();
			    	dirPath = sharedDirPath;
			    }
			}
			if(dirPath==null) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				dirPath = "/"+year+"/"+type+"/"+entityId+"/";
			}
			String fullPath = systemPath+dirPath;
			File directory = new File(fullPath);
		    if (! directory.exists()){
		        directory.mkdirs();
		    }
			file.transferTo(new File(fullPath+fileName));
			if(attachment==null) {
				attachment = new Attachment();
			}
			attachment.setFilePath(dirPath);
			attachment.setMimeType(file.getContentType());
			attachment.setName(fileName);
			attachment.setType(type);
			attachmentRepo.save(attachment);
			return attachment;
		}
		
	}
}
