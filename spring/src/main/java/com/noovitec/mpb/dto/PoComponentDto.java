package com.noovitec.mpb.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoComponentDto {

	private Long id;
	private String name;
	private Long unitsSold;
	private Long unitsProduced;
	private Long unitsInOrder;
	private Long unitsOnStock;
	private Long units;
	private BigDecimal unitPrice = BigDecimal.ZERO;
	private BigDecimal totalPrice = BigDecimal.ZERO;
}
