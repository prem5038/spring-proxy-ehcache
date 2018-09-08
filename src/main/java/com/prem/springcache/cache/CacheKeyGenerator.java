package com.prem.springcache.cache;

import org.apache.catalina.connector.RequestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Component("cacheKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {

    Logger logger = LoggerFactory.getLogger(CacheKeyGenerator.class);

  public Object generate(Object target, Method method, Object... params) {
      List<Object> args = Arrays.asList(params);

      CacheKeyReference cacheKeyReference = new CacheKeyReference();
      cacheKeyReference.setRequestBody(args.get(0));
      cacheKeyReference.setHttpMethod(args.get(1).toString().trim());

      if (args.get(2) instanceof RequestFacade) {
          cacheKeyReference.setRequestFacade((RequestFacade) args.get(2));
      }

      if(args.get(3) instanceof HttpHeaders){
          cacheKeyReference.setHttpHeaders((HttpHeaders) args.get(3));
      }

      logger.debug("CACHE KEY CONTENT : "+cacheKeyReference.toString());
      logger.debug("CACHE KEY CHEKSUM : "+cacheKeyReference.toChecksumString());

      CacheKeyRefMap.put(cacheKeyReference.toChecksumString(), cacheKeyReference);

      return cacheKeyReference.toChecksumString();
  }





}