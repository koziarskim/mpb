package com.noovitec.mpb.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.entity.Component;
import com.noovitec.mpb.entity.Item;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.repo.ComponentRepo;
import com.noovitec.mpb.repo.ItemRepo;
import com.noovitec.mpb.service.AttachmentService;
import com.noovitec.mpb.service.MigrationService;

@RestController
@RequestMapping("/api")
class MigrationRest {

	private final Logger log = LoggerFactory.getLogger(MigrationRest.class);
	private MigrationService migrationService;
	@Autowired
	ItemRepo itemRepo;
	@Autowired
	ComponentRepo componentRepo;
	@Autowired
	AttachmentRepo attachmentRepo;
	@Autowired
	AttachmentService attachmentService;


	public MigrationRest(MigrationService migrationService) {
		this.migrationService = migrationService;
	}

	@GetMapping("/migrate/db")
	ResponseEntity<?> migrate() throws IOException {
		Iterable<Item> items = itemRepo.findAll();
		for(Item item: items) {
			if(item.getAttachment()!=null) {
				Attachment attachment = attachmentService.getWithDocContent(item.getAttachment().getId());
				if(attachment.getDocContent()!=null) {
					this.saveAttachment(attachment, item.getId(), Item.class.getSimpleName());
				}
			}
		}
		Iterable<Component> components = componentRepo.findAll();
		for(Component component: components) {
			if(component.getAttachment()!=null) {
				Attachment attachment = attachmentService.getWithDocContent(component.getAttachment().getId());
				if(attachment.getDocContent()!=null) {
					this.saveAttachment(attachment, component.getId(), Component.class.getSimpleName());
				}
			}
		}
		return ResponseEntity.ok().body("OK");
	}
	
	private void saveAttachment(Attachment attachment, Long id, String type) throws IOException {
		byte[] data = attachment.getDocContent().getData();
		String fileName = "image.jpg";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String filePath = "/"+year+"/"+type+"/"+id+"/";
		String systemPath = System.getenv("MPB_FILE_STORE_DIR");
		log.info("System Path: "+systemPath);
		if(systemPath == null) {
			systemPath = "/home/koziarskim/mpb/mpb_file_store";
		}
		String fullPath = systemPath+filePath;
		File directory = new File(fullPath);
	    if (! directory.exists()){
	        directory.mkdirs();
	    }
		Path path = Paths.get(fullPath+fileName);
		Files.write(path, data);
		  
		attachment.setFilePath(filePath);
		attachment.setMimeType("image/jpeg");
		attachment.setName(fileName);
		attachment.setType(type);
		attachmentRepo.save(attachment);
	}
}