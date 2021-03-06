package com.prem.springcache.config;

import com.prem.springcache.cache.CacheKeyRefMap;
import com.prem.springcache.constant.AppConstant;
import com.prem.springcache.service.ProxyService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;

@Configuration
@EnableScheduling
public class AppConfig implements AppConstant {

	@Autowired
	ProxyService proxyService;

    Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient();
	}

	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
        cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cmfb.setShared(true);
        return cmfb;
    }

	@Bean
    CommandLineRunner runner(){
        return args -> {
            logger.debug("Proxy service is running ...");
        };
    }

	@Scheduled(fixedRate=(CACHE_REFRESH_FREQUENCY *60*1000)) // 5 min
	public void updateAllCacheEntries() {
        logger.info("cache refresh - started");
	    proxyService.refreshAllCacheEntries();
        logger.info("cache refresh - completed");
	}

	@Scheduled(fixedRate=(CACHE_EVICT_FREQUENCY*60*1000), initialDelay = CACHE_EVICT_INIT_DELAY *60*1000) // 30 min
	public void removeAllCacheEntries() {
        logger.info("cache eviction - started");
		proxyService.clearAllCacheEntries();
        logger.info("cache eviction - completed");
	}

}
