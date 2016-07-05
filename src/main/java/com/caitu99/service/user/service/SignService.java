package com.caitu99.service.user.service;


import com.caitu99.service.user.domain.Sign;

public interface SignService {
	
	public Sign getSign(long userId);
	
    public Sign signEvery(long userId);
    
    public Sign signGiftIntegral(Sign sign);
    
    Sign signGiftTubi(Sign sign);
    
}
