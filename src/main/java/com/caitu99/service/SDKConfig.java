/**
 *
 * Licensed Property to China UnionPay Co., Ltd.
 * 
 * (C) Copyright of China UnionPay Co., Ltd. 2010
 *     All Rights Reserved.
 *
 * 
 * Modification History:
 * =============================================================================
 *   Author         Date          Description
 *   ------------ ---------- ---------------------------------------------------
 *   xshu       2014-05-28       MPI基本参数工具类
 * =============================================================================
 */
package com.caitu99.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 软件开发工具包 配制
 * 
 * @author xuyaowen
 * 
 */
@Service("sDKConfig")
public class SDKConfig {
	
	@Value("${acpsdk.frontTransUrl}")
	public String frontTransUrl;
	
	@Value("${acpsdk.backTransUrl}")
	public String backTransUrl;
	
	@Value("${acpsdk.singleQueryUrl}")
	public String singleQueryUrl;

	@Value("${acpsdk.batchTransUrl}")
	public String batchTransUrl;

	@Value("${acpsdk.fileTransUrl}")
	public String fileTransUrl;

	@Value("${acpsdk.appTransUrl}")
	public String appTransUrl;

	@Value("${acpsdk.signCert.path}")
	public String signCertPath;

	@Value("${acpsdk.signCert.pwd}")
	public String signCertPwd;

	@Value("${acpsdk.signCert.type}")
	public String signCertType;

	@Value("${acpsdk.validateCert.dir}")
	public String validateCertDir;

	@Value("${acpsdk.encryptCert.path}")
	public String encryptCertPath;

	@Value("${acpsdk.encryptTrackCert.path}")
	public String encryptTrackCertPath;

	@Value("${acpsdk.singleMode}")
	public String singleMode;
	
	@Value("${acpsdk.memId}")
	public String memId;

	@Value("${acpsdk.backUrl}")
	public String backUrl;
	
	
	
}
