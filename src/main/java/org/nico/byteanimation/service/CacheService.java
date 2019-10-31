package org.nico.byteanimation.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.nico.byteanimation.component.CacheComponent;
import org.nico.byteanimation.component.OssComponent;
import org.nico.byteanimation.consts.CacheKeys;
import org.nico.byteanimation.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

	@Autowired
	private CacheComponent cacheComponent;
	
	@Autowired
	private OssComponent ossComponent;
	
	public String getNativeUrl(String key) {
		String url = cacheComponent.get(CacheKeys.OSS_URL + key);
		if(StringUtils.isBlank(url)) {
			url = ossComponent.getNativeFileUrl(key);
			cacheComponent.setEx(CacheKeys.OSS_URL + key, url, 60 * 60 * 3 + RandomUtils.random(5 * 60), TimeUnit.SECONDS);
		}
		return url;
	}
	
	public String getProcessedUrl(String key) {
		String url = cacheComponent.get(CacheKeys.OSS_URL + key);
		if(StringUtils.isBlank(url)) {
			url = ossComponent.getProcessedFileUrl(key);
			cacheComponent.setEx(CacheKeys.OSS_URL + key, url, 60 * 60 * 3 + RandomUtils.random(5 * 60), TimeUnit.SECONDS);
		}
		return url;
	}
}
