package com.caitu99.service.lianlianpay.config;

/**
* 商户配置信息
* @author guoyx e-mail:guoyx@lianlian.com
* @date:2013-6-25 下午01:45:40
* @version :1.0
*
*/
public interface PartnerConfig{
    // 银通公钥
    String YT_PUB_KEY     = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";
    // 商户私钥
    String TRADER_PRI_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJU0PsWxvlRX9LyWZHj68y9HHdugw3HRqwskCRib5HJQcFKpgq42+gVFKX4c2H48vG6amzexFQUloXMcNlZMIrgbKI+/f2rjEEo9RjiWjVdCLe0g4jVpApPcVsL4TFee+z3Rrm+NxlFAHU3ijNhncIhBATJoOUemJU+Dnv5HTDYTAgMBAAECgYBs2u8pQOKBmqI9rOOkuEIQLiyEfifZtIS73hCc2a+0DyfDx1RGUmnaynjZP6Zbg2hesYGpAhM57Bh8aQrjryxsUhMo3Awo5d+0yD4gk3raWvZ8p6s5jAFLOnhz63r4zsjqRrx9SMIu3Xp67u0EFF0xaW5YlsUJ1LmkkX/TI22CcQJBAN7MXn/PSvFdr3W+ksxXJk1sopTd+06lkc//7swfji2Yvi25oKjrOEEyqpJgezs75n1BHYB0XYTN9bdcWrZ2jg0CQQCrcEwb7ZAqXK3GpZ2JN1k9vMOBZ8YztHywpZtNR6F1vpVF1pF3HFrtf3Uvg4Bn5ZMiS0Fpx2rExaCP3/h0DuyfAkEAiS7rTOU1bvLRk71ZJErRAcFPRjx8fcuCwcEDp1oSsE6pYvw2SWw0AikRT/nqRum2HQ+X+70qzBgJIPLTxB+xjQJALC9uPkh1PqXSV/95YdM1GfdbwC954vuio3ibVUif8ZPkLzLFHRjeypVuI4fWAXEnAdC5lETEXOC+qDZGd8sc6wJBAJ6FB/u/7RHC8joyXr7AmOk0SQ2d8mz/yAxo6arvLVzWFkPy5PKgMjTK42Y5PNbvTRNSR0OsxBNMxvLlCJxgv4A=";
    // MD5 KEY
    String MD5_KEY        = "20160608_caitu99_20160608173111";
    // 接收异步通知地址
    String NOTIFY_URL     = "http://ip:port/wepdemo/notify.htm";
    // 支付结束后返回地址
    String URL_RETURN     = "http://ip:port/wepdemo/urlReturn.jsp";
    // 商户编号
    String OID_PARTNER    = "201605311000881513";
    // 签名方式 RSA或MD5
    String SIGN_TYPE      = "MD5";
    // 接口版本号，固定1.0
    String VERSION        = "1.0";

    // 业务类型，连连支付根据商户业务为商户开设的业务类型； （101001：虚拟商品销售、109001：实物商品销售、108001：外部账户充值）

    String BUSI_PARTNER   = "101001";
	
	
	/*// 银通公钥
    String YT_PUB_KEY     = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";
    // 商户私钥
    String TRADER_PRI_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOilN4tR7HpNYvSBra/DzebemoAiGtGeaxa+qebx/O2YAdUFPI+xTKTX2ETyqSzGfbxXpmSax7tXOdoa3uyaFnhKRGRvLdq1kTSTu7q5s6gTryxVH2m62Py8Pw0sKcuuV0CxtxkrxUzGQN+QSxf+TyNAv5rYi/ayvsDgWdB3cRqbAgMBAAECgYEAj02d/jqTcO6UQspSY484GLsL7luTq4Vqr5L4cyKiSvQ0RLQ6DsUG0g+Gz0muPb9ymf5fp17UIyjioN+ma5WquncHGm6ElIuRv2jYbGOnl9q2cMyNsAZCiSWfR++op+6UZbzpoNDiYzeKbNUz6L1fJjzCt52w/RbkDncJd2mVDRkCQQD/Uz3QnrWfCeWmBbsAZVoM57n01k7hyLWmDMYoKh8vnzKjrWScDkaQ6qGTbPVL3x0EBoxgb/smnT6/A5XyB9bvAkEA6UKhP1KLi/ImaLFUgLvEvmbUrpzY2I1+jgdsoj9Bm4a8K+KROsnNAIvRsKNgJPWd64uuQntUFPKkcyfBV1MXFQJBAJGs3Mf6xYVIEE75VgiTyx0x2VdoLvmDmqBzCVxBLCnvmuToOU8QlhJ4zFdhA1OWqOdzFQSw34rYjMRPN24wKuECQEqpYhVzpWkA9BxUjli6QUo0feT6HUqLV7O8WqBAIQ7X/IkLdzLa/vwqxM6GLLMHzylixz9OXGZsGAkn83GxDdUCQA9+pQOitY0WranUHeZFKWAHZszSjtbe6wDAdiKdXCfig0/rOdxAODCbQrQs7PYy1ed8DuVQlHPwRGtokVGHATU=";
    // MD5 KEY
    String MD5_KEY        = "201408071000001546_test_20140815";
    // 接收异步通知地址
    String NOTIFY_URL     = "http://ip:port/wepdemo/notify.htm";
    // 支付结束后返回地址
    String URL_RETURN     = "http://ip:port/wepdemo/urlReturn.jsp";
    // 商户编号
    String OID_PARTNER    = "201408071000001546";
    // 签名方式 RSA或MD5
    String SIGN_TYPE      = "MD5";
    // 接口版本号，固定1.0
    String VERSION        = "1.0";

    // 业务类型，连连支付根据商户业务为商户开设的业务类型； （101001：虚拟商品销售、109001：实物商品销售、108001：外部账户充值）

    String BUSI_PARTNER   = "101001";*/
	
	
}
