package com.caitu99.service.backstage.domain;

import com.caitu99.service.transaction.domain.AccountDetail;

/**
 * Created by liuzs on 2016/3/15.
 * 地推业务员统计
 */
public class PushStatistics extends AccountDetail {

    private Long totalIntegral;//财币量

    private Long stalls;//有效用户树
    

	public Long getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(Long totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public Long getStalls() {
        return stalls;
    }

    public void setStalls(Long stalls) {
        this.stalls = stalls;
    }
}
