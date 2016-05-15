package com.mahe.mockito.mytest.business;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.mahe.mockito.mytest.domain.Order;
import com.mahe.mockito.mytest.integration.ServiceA;
import com.mahe.mockito.mytest.integration.ServiceAA;
import com.mahe.mockito.mytest.integration.ServiceB;
import com.mahe.mockito.mytest.integration.ServiceC;
import com.mahe.mockito.mytest.integration.InSufficientInventoryException;



@RunWith(MockitoJUnitRunner.class)
public class OrderLogicTest{
	
	@Mock private ServiceA mockServiceA;
	@Mock private ServiceAA mockServiceAA;
	@Mock private ServiceB mockServiceB;
	@Mock private ServiceC mockServiceC;
	
	@InjectMocks private OrderLogic mockOrderLogic;

	final String productName = "Inspiron";
	final String supplierName = "Dell";
	final String requestor = "Mahendran";
	final Order order = new Order(productName, 8, requestor);
	final String EXCELLENT = "EXCELLENT";
	final String holdId = "SAMPLE_ID";
	
	@Test
	public void processOrder_success() throws Exception{
		
		when(mockServiceA.getSupplierName(productName)).thenReturn(supplierName);
		when(mockServiceAA.holdItems(order, supplierName)).thenReturn(holdId);
		
		when(mockServiceB.getCreditWorth(requestor)).thenReturn(EXCELLENT);
		when(mockServiceC.executeOrder(holdId, EXCELLENT)).thenReturn(Boolean.TRUE);
		
		mockOrderLogic.processOrder(order);
		
		verify(mockServiceC, only()).executeOrder(holdId, EXCELLENT);
	}
	
	@Test(expected=CanNotProcessException.class)
	public void processOrder_insufficientInventory() throws Exception{
		
		when(mockServiceA.getSupplierName(productName)).thenReturn(supplierName);
		when(mockServiceAA.holdItems(order, supplierName)).thenThrow(new InSufficientInventoryException());
		
		mockOrderLogic.processOrder(order);
		verify(mockServiceC, never()).executeOrder(holdId, EXCELLENT);
	}
	
	
	@Test
	public void processOrder_verifyParallel() throws Exception{
		
		when(mockServiceA.getSupplierName(productName)).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(1_000L);
				return supplierName;
			}
		});
		when(mockServiceAA.holdItems(order, supplierName)).thenReturn(holdId);
		
		when(mockServiceB.getCreditWorth(requestor)).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(2_000L);
				return EXCELLENT;
			}
		});
		
		when(mockServiceC.executeOrder(holdId, EXCELLENT)).thenReturn(Boolean.TRUE);
		
		Instant start = Instant.now();
		mockOrderLogic.processOrder(order);
		Instant end = Instant.now();
		Duration timeTaken = Duration.between(start, end);
		Assert.assertThat("Looks like test is not mocking sleep", timeTaken.toMillis(), Matchers.greaterThanOrEqualTo(2_000L));
		Assert.assertThat("Looks like running serially", timeTaken.toMillis(), Matchers.lessThan(3_000L));
	}
    
}
