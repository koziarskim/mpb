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
	boolean seasonAll = true;
	Set<Long> customers = new HashSet<Long>();
	String customerName = "";
	boolean customerAll = true;
	Set<Long> items = new HashSet<Long>();
	String itemName = "";
	boolean itemAll = true;
	Set<Long> sales = new HashSet<Long>();
	String saleNumber = "";
	boolean saleAll = true;
	Set<Long> suppliers = new HashSet<Long>();
	String supplierName = "";
	boolean supplierAll = true;
	Set<Long> components = new HashSet<Long>();
	String componentName = "";
	boolean componentAll = true;
}
