package com.noovitec.mpb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.noovitec.mpb.dto.KeyValueDto;
import com.noovitec.mpb.entity.Upc;

public interface UpcRepo extends JpaRepository<Upc, Long> {

	@Query(value = "select * from Upc u left join item i on i.upc_id = u.id where assigned = false and i.upc_id is null order by u.id asc limit 1", nativeQuery = true)
	Upc getFirstAvailable();
	
	@Query("select new com.noovitec.mpb.dto.KeyValueDto(u.id, u.code) from Upc u "
			+ "where u.id not in (select i.upc.id from Item i where i.upc.id is not null)")
	List<KeyValueDto> getKv();
	
}