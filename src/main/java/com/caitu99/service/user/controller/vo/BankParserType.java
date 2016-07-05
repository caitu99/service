package com.caitu99.service.user.controller.vo;

import java.util.ArrayList;
import java.util.List;

import com.caitu99.service.utils.html.parser.BankIntegral;

public class BankParserType {
	private List<BankIntegral> parserBill = new ArrayList<BankIntegral>();
	//type=0，表示积分独立；type=1，表示积分归一
	private Integer type;
	
	public List<BankIntegral> getParserBill() {
		return parserBill;
	}
	public void setParserBill(List<BankIntegral> parserBill) {
		this.parserBill = parserBill;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
