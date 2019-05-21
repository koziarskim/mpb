package com.noovitec.mpb.dto.projection;

public interface ItemAvailabilityProjection {
	
	Long getId();
	Long getUnitsScheduled();
	Long getUnitsToSchedule();
	Long getUnitsToProduction();
}
