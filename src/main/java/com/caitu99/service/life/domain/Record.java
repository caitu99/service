package com.caitu99.service.life.domain;

import java.lang.reflect.Type;

/**
 * Created by zsj on 2015/11/13.
 */
public class Record {
    private Integer type;
    private  Boolean used;

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}
