package com.noovitec.mpb.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.dto.CalendarEventDto;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.ShipmentRepo;

public interface ShipmentService {

	public Shipment save(Shipment shipment) throws IOException;
	public List<CalendarEventDto> findEvents();
	
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
		
		public List<CalendarEventDto> findEvents() {
			List<Shipment> shipments = shipmentRepo.getReadyToShip();
			List<CalendarEventDto> events = new ArrayList<CalendarEventDto>();
			shipments.forEach(shipment-> {
				CalendarEventDto dto = new CalendarEventDto();
				dto.setId(shipment.getId());
				dto.setHeading1(shipment.getNumber());
				dto.setHeading2(shipment.getCustomer()==null?"":shipment.getCustomer().getName());
				
				dto.setLine1(shipment.getCustomer()==null?"":shipment.getCustomer().getName());
				if(shipment.getShippingAddress()!=null){
					dto.setLine2(shipment.getShippingAddress().getDc()+", "+shipment.getShippingAddress().getCity()+", "+shipment.getShippingAddress().getState());
				}
				dto.setLine3(shipment.getLoadNumber());
				dto.setLine4(String.valueOf(shipment.getTotalPallets()));
				DateTimeFormatter df = DateTimeFormatter.ofPattern("YYYY-MM-dd");
				DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
				dto.setStart(shipment.getShippingDate().format(df)+" "+shipment.getShippingTime().format(tf));
				dto.setEnd(shipment.getShippingDate().format(df)+" "+shipment.getShippingTime().plusHours(1).format(tf));
				events.add(dto);
			});
			return events;
		}
		
	}
}
