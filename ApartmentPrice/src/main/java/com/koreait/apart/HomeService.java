package com.koreait.apart;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.koreait.apart.dto.ItemDTO;
import com.koreait.apart.dto.ResponseDTO;

@Service
public class HomeService {
	
	@Autowired
	private HomeMapper mapper;
	
	public List<LocationCdVO> selLocationCdList() {
		return mapper.selLocationCdList();
	}
	
	public String getData(SearchVO param) {
		String lawd_cd = param.getLocationCd();
		String deal_ym = String.format("%s%02d", param.getYear(), param.getMon());
		System.out.println("deal_ym : " + deal_ym);
		
		String serviceKey = "Y2UOCkD8Ilv2gViPGV33ddNTTQfRi92i8mRzUeQX%2BNgSiNTO3gp9hJZX4J6u8uXucMM6RdRBoGxMn6XHfsEzNA%3D%3D";
		String url = "http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev";
		String decodeServiceKey = null;
		try {
			decodeServiceKey = URLDecoder.decode(serviceKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.setContentType(new MediaType("application","xml",Charset.forName("UTF-8")));

        final HttpEntity<String> entity = new HttpEntity<String>(headers);
		
        UriComponents  builder = UriComponentsBuilder.fromHttpUrl(url)
		        .queryParam("LAWD_CD", lawd_cd)
		        .queryParam("DEAL_YMD", deal_ym)
		        .queryParam("serviceKey", decodeServiceKey)
		        .build(false);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters()
        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		
		System.out.println("builder.toString() : " + builder.toUriString());
		
		ResponseEntity<String> respEntity 
		= restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
		
		String result = respEntity.getBody();
		ResponseDTO dto = null;
		ObjectMapper om = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			dto = om.readValue(result, ResponseDTO.class);
		} catch (Exception e) {		
			e.printStackTrace();
		}
		
		List<ItemDTO> list = dto.getBody().getItems();
		
		if(list.size() > 0) { //DB에 insert
			for(ItemDTO item: list) {
				String s = item.getDealAmount();
				item.setDealAmount(s.replace(",", ""));
				mapper.insApartmentInfo(item);
			}
		}
		
		
		
		return "";
	}
	
}
