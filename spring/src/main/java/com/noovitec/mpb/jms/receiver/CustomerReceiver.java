package com.noovitec.mpb.jms.receiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.app.MpbTenantContext;
import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.entity.Notification;
import com.noovitec.mpb.jms.message.JmsCustomerMessage;
import com.noovitec.mpb.repo.CustomerRepo;
import com.noovitec.mpb.service.NotificationService;

public interface CustomerReceiver {
	public void updateHandler(JmsCustomerMessage message);
	
	@Transactional
	@Service("customerReceiverImpl")
	public class CustomerReceiverImpl implements CustomerReceiver{
		
		private final Logger log = LoggerFactory.getLogger(CustomerReceiverImpl.class);
		@Autowired
		private NotificationService notificationService;
		@Autowired
		private CustomerRepo customerRepo;
	
		public void updateHandler(JmsCustomerMessage message) {
			List<String> emails = null;
			Map<String, String> body = new HashMap<String, String>();
			Customer customer = null;
			//Customer shipped
			long oldUnitsShipped = message.getOldUnitsShipped();
			long unitsShipped = message.getUnitsShipped();
			long unitsSold = message.getUnitsSold();
			if(oldUnitsShipped != unitsShipped && unitsSold > 0 &&  unitsShipped >= unitsSold) {
				customer = customerRepo.getOne(message.getId());
				emails = new ArrayList<>(Arrays.asList("hpyzikiewicz@marketplacebrands.com"));
				body.put("customerName", customer.getName());
				notificationService.sendMail(emails, body, Notification.TYPE.CUSTOMER_SHIPPED);
			}
		}
	}
	
	@Component
	public class CustomerListener {
		
		@Autowired
		private CustomerReceiver customerReceiver;
		
		@JmsListener(destination = "customerUpdated", containerFactory = "myFactory")
		public void updateEvent(JmsCustomerMessage message) {
			try {
				MpbTenantContext.setCurrentTenant(message.getTenant());
				customerReceiver.updateHandler(message);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
