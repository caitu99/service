package com.caitu99.service.transaction.api;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.transaction.controller.api.WithdrawController;

public class WithdrawControllerTest extends AbstractJunit{

	@Autowired
	WithdrawController controller;
	
	@Test
	public void testWithdraw() {
		Long userId = 262L;
		Long userBankCardId = 7L;
		Long amount = 201L;
		String payPass = "111111";
		String result = controller.withdraw(userId, userBankCardId, amount, payPass);
		System.out.println(result);
	}

	@Test
	public void testQueryWithdraw() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsWithdraw() {
		fail("Not yet implemented");
	}

}
