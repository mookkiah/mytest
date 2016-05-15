package com.mahe.mockito.mytest.integration;

import com.mahe.mockito.mytest.domain.Order;

public interface ServiceAA {

	public String holdItems(Order order, String supplier)
			throws InSufficientInventoryException;

}