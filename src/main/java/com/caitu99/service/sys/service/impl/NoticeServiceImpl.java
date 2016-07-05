package com.caitu99.service.sys.service.impl;

import com.caitu99.service.sys.dao.NoticeMapper;
import com.caitu99.service.sys.domain.Notice;
import com.caitu99.service.sys.domain.Page;
import com.caitu99.service.sys.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public int insert(Notice record) {
        return noticeMapper.insert(record);
    }

    @Override
    public List<Notice> listAll(Page page) {
        return noticeMapper.listAll(page);
    }

    @Override
    public int countNum() {
        return noticeMapper.countNum();
    }

    @Override
    public int fDelete(Long id) {

        return noticeMapper.fDelete(id);
    }

    @Override
    public int updateByPrimaryKey(Notice record) {

        return noticeMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Notice> select(Long userId) {
    	List<Notice> list = noticeMapper.select(userId);
        return list;
    }

}
