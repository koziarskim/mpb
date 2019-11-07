package com.noovitec.mpb.repo.custom;

import java.util.List;

public interface CustomSaleItemRepo {

	List<Long> findIds(String numberName, Long customerId, Long itemId);
}