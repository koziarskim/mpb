package com.noovitec.mpb.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.noovitec.mpb.entity.Attachment;
import com.noovitec.mpb.repo.AttachmentRepo;


@RestController
@RequestMapping("/api")
class AttachmentRest {

	private final Logger log = LoggerFactory.getLogger(AttachmentRest.class);
	private AttachmentRepo attachmentRepo;

	public AttachmentRest(AttachmentRepo attachmentRepo) {
		this.attachmentRepo = attachmentRepo;
	}

	@GetMapping("/attachment/{id}")
	@ResponseBody
	HttpEntity<byte[]> downloadImage(@PathVariable Long id) throws IOException {
		Optional<Attachment> attachment = attachmentRepo.findById(id);
		byte[] data = attachment.get().getData();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + "filename.jpg");
		header.setContentLength(data.length);
		return new HttpEntity<byte[]>(data, header);
	}

	@GetMapping("/attachment")
	Collection<Attachment> getAll() {
		return attachmentRepo.findAll();
	}
}