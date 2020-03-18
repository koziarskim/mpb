package com.noovitec.mpb.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.int2of5.ITF14Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Upc;
import com.noovitec.mpb.repo.UpcRepo;


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

	@GetMapping("/upc/kv")
	List<KeyValueDto> getKv() {
		List<KeyValueDto> dtos = upcRepo.getKv();
		return dtos;
	}

	@GetMapping("/upc/image/{upcId}")
	@ResponseBody
	HttpEntity<byte[]> generateImage(@PathVariable Long upcId) throws WriterException, IOException {
//		byte[] image = getQRCodeImage(code, 300, 50);
		Upc upc = upcRepo.getOne(upcId);
		byte[] image = generateItemBarcode(upc.getCode());
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		header.set("Content-Disposition", "inline; filename=" + upc.getCode()+".jpg");
		header.setContentLength(image.length);
		return new HttpEntity<byte[]>(image, header);
	}

	private byte[] generateItemBarcode(String msg) throws IOException {
		ByteArrayOutputStream ous = new ByteArrayOutputStream();
		AbstractBarcodeBean bean = null;
		if (msg.length() == 12) {
			bean = new UPCABean();
			bean.setHeight(8);
		}
		if (msg.length() == 14) {
			bean = new ITF14Bean();
		}
		String format = "image/png";
		int dpi = 150;
		BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi, BufferedImage.TYPE_BYTE_BINARY, false,0);
		bean.generateBarcode(canvas, msg);
		canvas.finish();
		return ous.toByteArray();
	}
	
	private byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		MultiFormatWriter qrCodeWriter = new MultiFormatWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.CODE_128, width, height);
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		byte[] pngData = pngOutputStream.toByteArray();
		return pngData;
	}
}