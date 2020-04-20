package com.noovitec.mpb.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.repo.AttachmentRepo;
import com.noovitec.mpb.service.AttachmentService;


@RestController
@RequestMapping("/api")
class AttachmentRest {

	private final Logger log = LoggerFactory.getLogger(AttachmentRest.class);
	private AttachmentService attachmentService;
	@Autowired
	private AttachmentRepo attachmentRepo;

	public AttachmentRest(AttachmentService attachmentService) {
		this.attachmentService = attachmentService;
	}

	@GetMapping("/file/attachment/{attachmentId}")
	@ResponseBody
	ResponseEntity<?> downloadFile(@PathVariable Long attachmentId) throws IOException {
		Path path = attachmentService.load(attachmentId);
		if(path == null) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("image/FileNotFound.jpg");
			byte[] targetArray = new byte[is.available()];
			is.read(targetArray);
			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=FileNotFound.jpg")
					.body(targetArray);
		}
		String mimeType = Files.probeContentType(path);
		Resource resource = new UrlResource(path.toUri());
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(mimeType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	
	 @PostMapping("/file/upload")
	 ResponseEntity<?> uploadFile(@RequestParam MultipartFile file, @RequestParam String type, @RequestParam Long entityId) throws IllegalStateException, IOException {
		Attachment attachment = attachmentService.store(file, type, entityId, null);
		return ResponseEntity.ok().body(attachment);
	 }
	 
	 @GetMapping("/attachment/migrate")
	 ResponseEntity<?> migrate() throws IOException {
		attachmentService.migrateFiles();
		return ResponseEntity.ok().body("OK");
	 }

}