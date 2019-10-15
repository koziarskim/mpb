package com.noovitec.mpb.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {

	Set<Long> seasons = new HashSet<Long>();
	String seasonName = "";
	Set<Long> customers = new HashSet<Long>();
	String customerName = "";
	Set<Long> items = new HashSet<Long>();
	String itemName = "";
	Set<Long> sales = new HashSet<Long>();
	String saleNumber = "";
	Set<Long> suppliers = new HashSet<Long>();
	String supplierName = "";
	Set<Long> components = new HashSet<Long>();
	String componentName = "";
	
}
