package com.mahe.mockito.mytest.business;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mahe.mockito.mytest.domain.Order;
import com.mahe.mockito.mytest.integration.ServiceAA;
import com.mahe.mockito.mytest.integration.ServiceB;
import com.mahe.mockito.mytest.integration.ServiceC;
import com.mahe.mockito.mytest.integration.InSufficientInventoryException;
import com.mahe.mockito.mytest.integration.ServiceA;


public class OrderLogic 
{
    ServiceA a;
    ServiceAA aa;
    ServiceB b;
    ServiceC c;
	
    public void processOrder(Order order) throws CanNotProcessException{

    	ExecutorService es = Executors.newFixedThreadPool(2);
    	
        //call Service A{
            //after response from Service A, call service AA
            //out of Service A is the input to Service AA
        //}
    	
    	Future<String> holdIdFuture = es.submit(new Callable<String>() {
			
			public String call() throws InSufficientInventoryException {
				String supplier = a.getSupplierName(order.getProductName());
		    	String holdId;
				holdId = aa.holdItems(order, supplier);
				return holdId;
			}
		});
            
        //call Service B{
        //}
    	Future<String> creditFuture = es.submit(new Callable<String>() {
    		@Override
    		public String call() throws Exception {
    			String credit = b.getCreditWorth(order.getRequestor());
    			return credit;
    		}
		});
    	
    	es.shutdown();
    	String holdId = null;
    	String credit = null;
		try {
			holdId = holdIdFuture.get();
			credit = creditFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new CanNotProcessException(e);
		}
    	
    	
        //call Service C{
            //output of service AA && output Service B is input for Service C
        //}
    	c.executeOrder(holdId, credit);
    
    }

}
