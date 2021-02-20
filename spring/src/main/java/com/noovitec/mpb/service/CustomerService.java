package com.noovitec.mpb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Customer;
import com.noovitec.mpb.repo.CustomerRepo;

public interface CustomerService {
	
	public List<Customer> findAll();
	public Customer save(Customer customer);

	@Transactional
	@Service("customerServiceImpl")
	public class CustomerServiceImp implements CustomerService {

		private final Logger log = LoggerFactory.getLogger(CustomerServiceImp.class);
		private CustomerRepo customerRepo;

		public CustomerServiceImp(CustomerRepo customerRepo) {
			this.customerRepo = customerRepo;
		}
		
		public List<Customer> findAll() {
			return customerRepo.findAll();
		}
		
		public Customer save(Customer customer) {
			return customerRepo.save(customer);
		}
	}
}
