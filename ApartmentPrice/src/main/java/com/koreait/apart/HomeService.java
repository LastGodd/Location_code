package com.koreait.apart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
	
	@Autowired
	private HomeMapper mapper;
	
	public List<LocationCdVO> selLocationCdList() {
		return mapper.selLocationCdList();
	}
}
