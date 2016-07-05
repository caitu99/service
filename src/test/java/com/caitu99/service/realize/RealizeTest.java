package com.caitu99.service.realize;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.realization.domain.RealizePlatform;
import com.caitu99.service.realization.service.RealizePlatformService;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.realization.service.UserTermService;

public class RealizeTest extends AbstractJunit {

	@Autowired
	private RealizeService realizeService;
	
	@Test
	public void job(){
		realizeService.realizeJob(111116L,"BOCOM_SHOP");
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Autowired
	RealizePlatformService realizationPlatformService;
	
	@Autowired
	UserTermService userTermService;
	
	@Test
	public void test(){
		userTermService.selectRealizationList(262L,"200300000",true);
	}
	@Test
	public void testPlatformList(){
		String version = "200300000";
		String support = ",2,";
		List<RealizePlatform> list = realizationPlatformService.selectBySupport(version, support);
		System.out.println(list.size());
	}
}
