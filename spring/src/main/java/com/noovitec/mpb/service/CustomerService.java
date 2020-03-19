package com.noovitec.mpb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.repo.CustomerRepo;

public interface CustomerService {

	public void updateUnits(List<Long> customerIds);

	@Transactional
	@Service("customerServiceImpl")
	public class CustomerServiceImp implements CustomerService {

		private final Logger log = LoggerFactory.getLogger(CustomerServiceImp.class);
		private CustomerRepo customerRepo;

		public CustomerServiceImp(CustomerRepo customerRepo) {
			this.customerRepo = customerRepo;
		}
		

		public void updateUnits(List<Long> customerIds) {
			List<Customer> customers = customerIds==null?customerRepo.findAll():customerRepo.findByIds(customerIds);
			for(Customer customer: customers) {
				Long unitsSold = customerRepo.getUnitsSold(customer.getId());
				Long unitsShipped = customerRepo.getUnitsShipped(customer.getId());
				customer.setUnitsSold(unitsSold);
				customer.setUnitsShipped(unitsShipped);
				customerRepo.save(customer);
			}
		}

	}
}
