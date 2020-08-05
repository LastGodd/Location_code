package com.koreait.apart;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeMapper {
	public List<LocationCdVO> selLocationCdList();
}
