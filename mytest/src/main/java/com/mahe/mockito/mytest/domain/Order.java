package com.mahe.mockito.mytest.domain;

public class Order {
	
	private Long orderId;
    private String productName;
    private Integer quantity;
    private String requestor;
    
    
    
	public Order(String productName, Integer quantity, String requestor) {
		super();
		this.productName = productName;
		this.quantity = quantity;
		this.requestor = requestor;
	}
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public String getRequestor() {
		return requestor;
	}
	
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
    
}
