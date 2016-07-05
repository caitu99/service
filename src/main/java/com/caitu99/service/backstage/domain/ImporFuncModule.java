package com.caitu99.service.backstage.domain;

/**
 * Created by liuzs on 2016/2/17.
 * 重要功能模块使用统计
 */
public class ImporFuncModule {

    private Long userNumber;//使用人数

    private Long usageCount;//使用次数

    private int useTime;//单次平均使用时长

    private Long dealNumber;//成交数量

    public Long getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Long userNumber) {
        this.userNumber = userNumber;
    }

    public Long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Long usageCount) {
        this.usageCount = usageCount;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public Long getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(Long dealNumber) {
        this.dealNumber = dealNumber;
    }
}
