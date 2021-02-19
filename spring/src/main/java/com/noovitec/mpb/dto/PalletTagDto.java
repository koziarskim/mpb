package com.noovitec.mpb.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PalletTagDto {

	private Long id;
	private String customerName;
    private String locationName;
    private String dc;
    private String street;
    private String line1;
    private String city;
    private String state;
    private String zip;
    private String saleNumber;
    private String itemNumber;
    private String sku;
    private int casePack;
    private LocalDate expiration;
    private int cases;
    private String ti;
    private String hi;
    private int pageFrom;
    private int pageTo;
}
