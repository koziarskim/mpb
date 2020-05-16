package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.repo.CustomerRepo;

public interface CustomerService {

	@Transactional
	@Service("customerServiceImpl")
	public class CustomerServiceImp implements CustomerService {

		private final Logger log = LoggerFactory.getLogger(CustomerServiceImp.class);
		private CustomerRepo customerRepo;

		public CustomerServiceImp(CustomerRepo customerRepo) {
			this.customerRepo = customerRepo;
		}
	}
}
