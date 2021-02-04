package com.noovitec.mpb.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.dto.CalendarEventDto;
import com.noovitec.mpb.entity.Address;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.entity.ShipmentItem;
import com.noovitec.mpb.repo.ShipmentRepo;

public interface ShipmentService {

	public Shipment save(Shipment shipment) throws IOException;
	public List<CalendarEventDto> findEvents(LocalDate startDate, LocalDate endData);
	public byte[] generateXls(List<Long> shipmentIds) throws IOException;
	
	@Transactional
	@Service("shipmentServiceImpl")
	public class ShipmentServiceImp implements ShipmentService {

		private final Logger log = LoggerFactory.getLogger(ShipmentServiceImp.class);
		private ShipmentRepo shipmentRepo;
		@Autowired
		CrudService crudService;
		@Autowired
		AttachmentService attachmentService;
		@Autowired
		NotificationService notificationService;
		@Autowired
		InvoiceService invoiceService;
		
		public ShipmentServiceImp(ShipmentRepo shpmentRepo) {
			this.shipmentRepo = shpmentRepo;
		}
		
		public Shipment save(Shipment shipment) throws IOException {
			if (shipment == null) {
				shipment = new Shipment();
			}
			shipment = (Shipment) crudService.merge(shipment);
			shipment = shipmentRepo.save(shipment);
			return shipment;
		}
		
		public List<CalendarEventDto> findEvents(LocalDate startDate, LocalDate endDate) {
			List<Shipment> shipments = shipmentRepo.getReadyToShip(startDate, endDate);
			List<CalendarEventDto> events = new ArrayList<CalendarEventDto>();
			shipments.forEach(shipment-> {
				CalendarEventDto dto = new CalendarEventDto();
				dto.setId(shipment.getId());
				dto.setHeading1(shipment.getNumber());
				dto.setHeading2(shipment.getCustomer()==null?"":shipment.getCustomer().getName());
				
				if(shipment.getShippingAddress()!=null){
					dto.setLine1(shipment.getShippingAddress().getDc()+", "+shipment.getShippingAddress().getCity()+", "+shipment.getShippingAddress().getState());
				}
				dto.setLine2(shipment.getLoadNumber());
				dto.setLine3(String.valueOf(shipment.getTotalPallets()));
				DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd");
				DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
				dto.setStart(shipment.getShippingDate().format(df)+" "+shipment.getShippingTime().format(tf));
				dto.setEnd(shipment.getShippingDate().format(df)+" "+shipment.getShippingTime().plusHours(1).format(tf));
				if(shipment.getShippedDate()!=null) {
					dto.setType("SHIPMENT_SHIPPED");
				}else {
					dto.setType("SHIPMENT_READY");
				}
				events.add(dto);
			});
			return events;
		}
		
		public byte[] generateXls(List<Long> shipmentIds) throws IOException {
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY");
			Map<String, String> ft = new HashMap<String, String>();
			ft.put("TPB", "TP Bill");
			ft.put("PRP", "Pre Paid");
			ft.put("TPO", "TP Bill Other");
			ft.put("COL","Collect");
			ft.put("CPU", "Customer Pickup");
			ft.put("DEL", "Delivered");
			Map<String, String> stats = new HashMap<String, String>();
			stats.put("INP", "Progress");
			stats.put("REA", "Ready");
			stats.put("SHP", "Shipped");
			List<Shipment> shipments = shipmentRepo.findAllById(shipmentIds);
			XSSFWorkbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Shipment");

			Row rowHeader = sheet.createRow(0);	 
			CellStyle headerStyle = workbook.createCellStyle();
			XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
			headerFont.setFontName("Arial");
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);
			addCell(0, "ED", rowHeader);
			addCell(1, "Shipment #", rowHeader);
			addCell(2, "Customer", rowHeader);
			addCell(3, "Sale #", rowHeader);
			addCell(4, "Item #", rowHeader);
			addCell(5, "Item Name", rowHeader);
			addCell(6, "Units", rowHeader);
			addCell(7, "Cases", rowHeader);
			addCell(8, "Pallets", rowHeader);
			addCell(9, "Freight", rowHeader);
			addCell(10, "Shipped Date", rowHeader);
			addCell(11, "Load #", rowHeader);
			addCell(12, "Shipment Address", rowHeader);
			addCell(13, "Status", rowHeader);
			
			XSSFFont cellFont = ((XSSFWorkbook) workbook).createFont();
			cellFont.setFontName("Arial");
			cellFont.setBold(false);
			CellStyle cellStyle = workbook.createCellStyle();
			int count = 1;
			for(Shipment ship: shipments) {
				for(ShipmentItem si: ship.getShipmentItems()) {
					Row row = sheet.createRow(count);
					cellStyle.setFont(cellFont);
					addCell(0, String.valueOf(count), row);
					addCell(1, ship.getNumber(), row);
					addCell(2, ship.getCustomer().getName(), row);
					addCell(3, si.getSaleItem().getSale().getNumber(), row);
					addCell(4, si.getSaleItem().getItemPackaging().getItem().getNumber(), row);
					addCell(5, si.getSaleItem().getItemPackaging().getItem().getName(), row);
					addCell(6, String.valueOf(si.getUnits()), row);
					addCell(7, String.valueOf(si.getCases()), row);
					addCell(8, String.valueOf(si.getPallets()), row);
					addCell(9, ft.get(ship.getFreightTerms()), row);
					addCell(10, ship.getShippedDate().format(dateFormat), row);
					addCell(11, ship.getLoadNumber(), row);
					Address address = ship.getShippingAddress();
					String addressLabel = "";
					if(ship.getShippingAddress()!=null) {
						addressLabel = address.getDc()+", "+address.getStreet()+", "+address.getCity()+", "+address.getState()+", "+address.getZip();
					}
					addCell(12, addressLabel, row);
					addCell(13, stats.get(ship.getStatus()), row);
					count++;
				}
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			workbook.close();
			return baos.toByteArray();
		}

		private void addCell(int column, String value, Row row) {
			Cell cell = row.createCell(column);
			cell.setCellValue(value);
		}
		
	}
}
