package com.prem.springcache.cache;

import org.apache.catalina.connector.RequestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

@Component("customKeyGen")
public class CustomKeyGenerator implements KeyGenerator {

    Logger logger = LoggerFactory.getLogger(CustomKeyGenerator.class);

  public Object generate(Object target, Method method, Object... params) {
      logger.debug("Calculating custom cache key ...");
      String schema = null;
      String host = null;
      String requestURI = null;
      String queryString = null;
      String requestURL = null;
      String requestBodyStr = "";
      String requestBodyChecksum = "";
      String customKey = null;
      String customKeyChecksum = "";
      List<Object> args = Arrays.asList(params);

      Object requestBody = args.get(0);
      if(requestBody!=null && requestBody instanceof String){
          logger.debug("request body found");
          requestBodyStr = requestBody.toString();
          requestBodyChecksum = this.getMD5Hash(requestBodyStr);
      }

      String httpMethod = args.get(1).toString().trim();

      Object requestFacadeObject = args.get(2);

      if (requestFacadeObject instanceof RequestFacade) {
          RequestFacade requestFacade = (RequestFacade) requestFacadeObject;
          requestURI = requestFacade.getRequestURI() != null ? requestFacade.getRequestURI():"";
          queryString = requestFacade.getQueryString() != null ? requestFacade.getQueryString() : "";
      }


      customKey = method.getName()+"_"+httpMethod+"_"+requestBodyChecksum+"_"+requestURI+queryString;
      customKeyChecksum = this.getMD5Hash(customKey);
      logger.debug("Custom key content: "+customKey);
      logger.debug("Returning Custom key checksum (as cache key): "+customKeyChecksum);
      return customKeyChecksum;
  }

    private String getMD5Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return bytesToHex(hash); // make it printable
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }



}