package com.noovitec.mpb.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noovitec.mpb.entity.Schedule;
import com.noovitec.mpb.entity.ScheduleEvent;
import com.noovitec.mpb.repo.ScheduleEventRepo;
import com.noovitec.mpb.repo.ScheduleRepo;

public interface ScheduleEventService {

	public ScheduleEvent save(ScheduleEvent scheduleEvent);
	public void delete(Long id);

	@Transactional
	@Service("scheduleEventServiceImpl")
	public class ScheduleEventServiceImp implements ScheduleEventService {

		private final Logger log = LoggerFactory.getLogger(ScheduleEventServiceImp.class);
		private ScheduleEventRepo scheduleEventRepo;
		@Autowired
		private CrudService crudService;
		@Autowired
		private ScheduleRepo scheduleRepo;

		public ScheduleEventServiceImp(ScheduleEventRepo scheduleEventRepo) {
			this.scheduleEventRepo = scheduleEventRepo;
		}
		
		public ScheduleEvent save(ScheduleEvent scheduleEvent){
			scheduleEvent = (ScheduleEvent) crudService.merge(scheduleEvent);
			if(scheduleEvent.getSchedule().getId()==null) {
				List<Schedule> schedules = scheduleRepo.findByDate(scheduleEvent.getSchedule().getDate());
				if(schedules!=null) {
					scheduleEvent.setSchedule(schedules.get(0));
				}
			}
			return scheduleEventRepo.save(scheduleEvent);
		}
		
		public void delete(Long id) {
			scheduleEventRepo.deleteById(id);
		}
	}
}
