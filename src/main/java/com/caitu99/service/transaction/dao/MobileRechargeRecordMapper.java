package com.caitu99.service.transaction.dao;

import com.caitu99.service.life.domain.PhoneRechargeRecord;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface MobileRechargeRecordMapper extends IEntityDAO<PhoneRechargeRecord, PhoneRechargeRecord> {
	int deleteByPrimaryKey(Long id);

	int insert(PhoneRechargeRecord record);

	int insertSelective(PhoneRechargeRecord record);

	PhoneRechargeRecord selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(PhoneRechargeRecord record);

	int updateByPrimaryKeyWithBLOBs(PhoneRechargeRecord record);

	int updateByPrimaryKey(PhoneRechargeRecord record);

	List<PhoneRechargeRecord> listAll(Page page);

	int countNum();
	
	Long sameDayPhoneRechargeAmount(Long userId);
}