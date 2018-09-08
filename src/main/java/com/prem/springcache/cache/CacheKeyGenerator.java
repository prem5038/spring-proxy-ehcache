package com.prem.springcache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Component("cacheKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {

    Logger logger = LoggerFactory.getLogger(CacheKeyGenerator.class);

  public Object generate(Object target, Method method, Object... params) {
      List<Object> args = Arrays.asList(params);
      CacheKeyReference cacheKeyReference = null;
      if(args!=null && !args.isEmpty()) {
          cacheKeyReference = (CacheKeyReference) args.get(0);
      }
      logger.debug("CACHE KEY CONTENT : "+cacheKeyReference.toString());
      logger.debug("CACHE KEY CHEKSUM : "+cacheKeyReference.toChecksumString());
      CacheKeyRefMap.put(cacheKeyReference.toChecksumString(), cacheKeyReference);
      return cacheKeyReference.toChecksumString();
  }

}