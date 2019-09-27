package com.noovitec.mpb.dto.projection;

//TODO: Do we need this?
public interface ItemAvailabilityProjection {
	
	Long getId();
	Long getUnitsScheduled();
	Long getUnitsToSchedule();
	Long getUnitsToProduction();
	
	void setUnitsScheduled(Long units);
}
