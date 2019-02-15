package com.noovitec.mpb.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.noovitec.mpb.entity.Upc;
import com.noovitec.mpb.repo.UpcRepo;

@CrossOrigin
@RestController
@RequestMapping("/api")
class UpcRest {

	private final Logger log = LoggerFactory.getLogger(UpcRest.class);
	private UpcRepo upcRepo;

	public UpcRest(UpcRepo upcRepo) {
		this.upcRepo = upcRepo;
	}

	@GetMapping("/upc")
	Collection<Upc> getAll() {
		return upcRepo.findAll();
	}

	@GetMapping("/upc/available")
	Upc getAvailableCode() {
		Upc upc = upcRepo.getFirstAvailable();
		return upc;
	}
	
	@GetMapping("/upc/image/{code}")
	@ResponseBody HttpEntity<byte[]> generateImage(@PathVariable String code) throws WriterException, IOException {
		byte[] image = getQRCodeImage(code, 300, 300);
		HttpHeaders header = new HttpHeaders();
	    header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    header.set("Content-Disposition", "inline; filename=" + "filename.jpg");
	    header.setContentLength(image.length);
	    return new HttpEntity<byte[]>(image, header);
	}
	
	private byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
	    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
	    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
	    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
	    byte[] pngData = pngOutputStream.toByteArray(); 
	    return pngData;
	}
}