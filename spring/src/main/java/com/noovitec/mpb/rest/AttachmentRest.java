package com.noovitec.mpb.rest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	@GetMapping("/attachment/{id}")
	@ResponseBody
	HttpEntity<byte[]> downloadImage(@PathVariable Long id) throws IOException {
		Attachment attachment = attachmentService.getWithDocContent(id);
		if(attachment.getDocContent()==null) {
			return null;
		}
		byte[] data = attachment.getDocContent().getData();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + attachment.getFileName());
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}
	
	 @PostMapping("/file")
	 ResponseEntity<?> uploadFile(@RequestParam MultipartFile file, @RequestParam String type) throws IllegalStateException, IOException {
		Attachment attachment = attachmentService.store(file, type);
		return ResponseEntity.ok().body(attachment);
	 }

}