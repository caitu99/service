package com.caitu99.service.utils.string;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class IdCardValidator {

    public static void main(String[] args) {
        System.out.println(valideIdCard("111111111111111111111"));
    }

    public static boolean valideIdCard(String idCard) {

        String idCardPattern = "^\\d{17}(\\d|X)$";  // 前17位为数字，最后一位为数字或X
        /*
        11	 北京市
        12	 天津市
        13	 河北省
        14	 山西省
        15	 内蒙古自治区
        21	 辽宁省
        22	 吉林省
        23	 黑龙江省
        31	 上海市
        32	 江苏省
        33	 浙江省
        34	 安徽省
        35	 福建省
        36	 江西省
        37	 山东省
        41	 河南省
        42	 湖北省
        43	 湖南省
        44	 广东省
        45	 广西壮族自治区
        46	 海南省
        50	 重庆市
        51	 四川省
        52	 贵州省
        53	 云南省
        54	 西藏自治区
        61	 陕西省
        62	 甘肃省
        63	 青海省
        64	 宁夏回族自治区
        65	 新疆维吾尔自治区
        71	 台湾省
        81	 香港特别行政区
        82	 澳门特别行政区
         */
        String provinces = "11, 12, 13, 14, 15, 21, 22, 23, 31, 32, 33, 34, 35, 36, 37, 41, 42, 43, 44, 45, 46, 50, 51, 52, 53, 54, 61, 62, 63, 64, 65, 71, 81, 82";

        // 验证长度
        if (idCard.length() != 18) {
            return false;
        }
        // 验证格式
        if (!Pattern.matches(idCardPattern, idCard)) {
            return false;
        }
        // 验证省级代码
        if (!provinces.contains(idCard.substring(0,2))) {
            return false;
        }
        // 验证年月日
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date birthday = df.parse(idCard.substring(6, 14));
            Date min = df.parse("19000101");
            Date max = df.parse(df.format(new Date()));
            if (birthday.before(min) || birthday.after(max)) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

        // 验证校验位
        /*
        关于身份证号码最后一位的校验码的算法如下：
     　  ∑(a[i]*W[i]) mod 11 ( i = 2, 3, ..., 18 )
        　 　"*" ： 表示乘号
         　　i：  表示身份证号码每一位的序号，从右至左，最左侧为18，最右侧为1。
         　　a[i]： 表示身份证号码第 i 位上的号码
         　　W[i]： 表示第 i 位上的权值 W[i] = 2^(i-1) mod 11
     　　设：R = ∑(a[i]*W[i]) mod 11 ( i = 2, 3, ..., 18 )
     　　C = 身份证号码的校验码
    　　则R和C之间的对应关系如下表：
    　　　R：0 1 2 3 4 5 6 7 8 9 10
    　　　C：1 0 X 9 8 7 6 5 4 3 2
    　　由此看出 X 就是 10，罗马数字中的 10 就是X，所以在新标准的身份证号码中可能含有非数字的字母X。
         */
        char residues[] = {'1', '0', 'X', '9', '8', '7','6', '5', '4', '3', '2'};
        long sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Integer.valueOf(idCard.substring(i, i+1)) * (Math.pow(2,(18-1-i))%11);
        }
        return idCard.charAt(17) == residues[(int)(sum % 11)];
    }
    
    /**
     * 身份证加密(3**********6)
     * @Description: (方法职责详细描述,可空)  
     * @Title: encryption 
     * @param identityCard
     * @return
     * @date 2016年1月20日 上午11:41:58  
     * @author xiongbin
     */
    public static String encryption(String identityCard){
    	StringBuffer temp = new StringBuffer();
		temp.append(identityCard.substring(0,1));
		for(int j=1;j<identityCard.length()-1;j++){
			temp.append("*");
		}
		temp.append(identityCard.subSequence(identityCard.length()-1, identityCard.length()));
		
		return temp.toString();
    }
}