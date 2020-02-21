package com.noovitec.mpb.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Schedule;
import com.noovitec.mpb.repo.ScheduleRepo;

public interface ScheduleService {

	public Schedule save(Schedule schedule);
	public void delete(Long id);

	@Transactional
	@Service("scheduleServiceImpl")
	public class ScheduleServiceImp implements ScheduleService {

		final Logger log = LoggerFactory.getLogger(ScheduleServiceImp.class);
		ScheduleRepo scheduleRepo;
		@Autowired
		CrudService crudService;

		public ScheduleServiceImp(ScheduleRepo scheduleRepo) {
			this.scheduleRepo = scheduleRepo;
		}
		
		public Schedule save(Schedule schedule){
			schedule = (Schedule) crudService.merge(schedule);
			return scheduleRepo.save(schedule);
		}
		
		public void delete(Long id) {
			scheduleRepo.deleteById(id);
		}
	}
}
