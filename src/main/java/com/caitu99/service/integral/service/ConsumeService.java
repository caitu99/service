package com.caitu99.service.integral.service;

import java.util.List;

import com.caitu99.service.integral.domain.Consume;

public interface ConsumeService {

	List<Consume> consumeAll(Long userId);

	int insert(Consume record);
}
