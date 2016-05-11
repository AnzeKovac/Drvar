package com.kovac.drvar;



public class Narocilo {
	
	private int id;
	private String name;
	private String adress;
	private String date;
	private String type;
	private String length;
	private String amount;
	private String status;
	
	
	
	
	public Narocilo(int id, String name, String adress, String date, String type, String length, String amount, String status) {
		super();
		this.id = id;
		this.name = name;
		this.adress = adress;
		this.date = date;
		this.type = type;
		this.length = length;
		this.amount = amount;
		this.status = status;
	}
	
	public Narocilo(){}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}


	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}


	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}



	@Override
	public String toString() {
	return "Order ["+id+", "+name+", "+adress+", "+date+", "+type+", "+length+", "+amount+", "+status+"]";
	}

}



