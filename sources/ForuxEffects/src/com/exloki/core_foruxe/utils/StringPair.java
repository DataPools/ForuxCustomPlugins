package com.exloki.core_foruxe.utils;

public class StringPair
{
	private String zero;
	private String one;
	
	public StringPair(String zero, String one)
	{
		this.zero = zero;
		this.one = one;
	}
	
	public void setZero(String zero)
	{
		this.zero = zero;
	}
	
	public void setOne(String one)
	{
		this.one = one;
	}
	
	public boolean isZeroSet()
	{
		return this.zero != null && !this.zero.isEmpty();
	}
	
	public boolean isOneSet()
	{
		return this.one != null && !this.one.isEmpty();
	}
	
	public String getZero()
	{
		return zero;
	}
	
	public String getOne()
	{
		return one;
	}
}
