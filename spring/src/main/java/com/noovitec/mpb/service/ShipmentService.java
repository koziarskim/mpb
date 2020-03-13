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

import com.noovitec.mpb.dto.ShipmentEventDto;
import com.noovitec.mpb.entity.Shipment;
import com.noovitec.mpb.repo.ShipmentRepo;

public interface ShipmentService {

	public Shipment save(Shipment shipment) throws IOException;
	public List<ShipmentEventDto> findEvents();
	
	@Transactional
	@Service("shipmentServiceImpl")
	public class ShipmentServiceImp implements ShipmentService {

		private final Logger log = LoggerFactory.getLogger(ShipmentServiceImp.class);
		private ShipmentRepo shipmentRepo;
		@Autowired
		CrudService crudService;
		@Autowired
		AttachmentService attachmentService;
		
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
		
		public List<ShipmentEventDto> findEvents() {
			List<Shipment> shipments = shipmentRepo.getReadyToShip();
			List<ShipmentEventDto> events = new ArrayList<ShipmentEventDto>();
			shipments.forEach(shipment-> {
				ShipmentEventDto dto = new ShipmentEventDto();
				dto.setId(shipment.getId());
				dto.setNumber(shipment.getNumber());
				dto.setCustomer(shipment.getCustomer()==null?"":shipment.getCustomer().getName());
				if(shipment.getShippingAddress()!=null){
					dto.setDc(shipment.getShippingAddress().getDc());
					dto.setCity(shipment.getShippingAddress().getCity());
					dto.setState(shipment.getShippingAddress().getState());
				}
				dto.setLoad(shipment.getLoadNumber());
				dto.setPallets(shipment.getTotalPallets());
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
