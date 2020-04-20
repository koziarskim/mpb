package com.noovitec.mpb.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.noovitec.mpb.app.MpbRequestContext;
import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.repo.AttachmentRepo;

public interface AttachmentService {

	public Attachment getById(Long attachmentId);
	public Attachment store(MultipartFile file, String type, Long entityId, Attachment attachment) throws IllegalStateException, IOException;
	public Path load(Long attachmentId) throws JsonParseException, JsonMappingException, IOException;
	public void migrateFiles() throws IOException;
	
	@Transactional
	@Service("attachmentServiceImpl")
	public class AttachmentServiceImp implements AttachmentService {

		private final static String defaultSystemPath = "/home/koziarskim/mpb/mpb_file_store";
		private final Logger log = LoggerFactory.getLogger(AttachmentServiceImp.class);
		private AttachmentRepo attachmentRepo;
	    @Autowired
		MpbRequestContext mpbRequestContext;

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

		public Path load(Long attachmentId) throws JsonParseException, JsonMappingException, IOException {
			Attachment attachment = this.getById(attachmentId);
			String systemPath = mpbRequestContext.getSetting().getFileStoreDir();
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
			String systemPath = mpbRequestContext.getSetting().getFileStoreDir();
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
		
		public void migrateFiles() throws IOException {
        	List<Attachment> attachments = attachmentRepo.findAll();
        	for(Attachment attachment: attachments) {
    			String systemPath = mpbRequestContext.getSetting().getFileStoreDir();
    			File oldDir = new File(systemPath+attachment.getFilePath());
    			if(!oldDir.exists()) {
//    				log.info("Dir not found: "+oldDir.getPath());
    				continue;
    			}
    			log.info("Copy: "+oldDir.getPath());
    			String[] paths = attachment.getFilePath().split("/");
    			int id = Integer.valueOf(paths[3]).intValue();
    			int extra = 0;
    			if(attachment.getType().equalsIgnoreCase("Component")) {
    				extra = 1600;
    			}
    			if(attachment.getType().equalsIgnoreCase("Item")) {
    				extra = 300;
    			}
    			if(attachment.getType().equalsIgnoreCase("Sale")) {
    				extra = 1700;
    			}
    			String newPath = attachment.getFilePath().replace(String.valueOf(id), String.valueOf(id+extra));
    			File newDir = new File(systemPath+newPath);
    			this.copyFolder(oldDir.toPath(), newDir.toPath());
				attachment.setFilePath(newPath);
				attachmentRepo.save(attachment);
        	}
		}
		
		public void copyFolder(Path src, Path dest) {
		    try {
		        Files.walk( src ).forEach( s -> {
		            try {
		                Path d = dest.resolve( src.relativize(s) );
		                if( Files.isDirectory( s ) ) {
		                    if( !Files.exists( d ) )
		                        Files.createDirectory( d );
		                    return;
		                }
		                Files.copy( s, d, StandardCopyOption.REPLACE_EXISTING);// use flag to override existing
		            } catch( Exception e ) {
		                e.printStackTrace();
		            }
		        });
		    } catch( Exception ex ) {
		        ex.printStackTrace();
		    }
		}
	}
}
