package com.prem.springcache.controller;

import com.prem.springcache.cache.CacheKeyReference;
import com.prem.springcache.service.ProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;


@RestController
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    Logger logger = LoggerFactory.getLogger(ProxyController.class);


    @RequestMapping("/**")
    public String processRequest(@RequestBody @Nullable String body,
                             HttpMethod method,
                             HttpServletRequest request,
                             @RequestHeader HttpHeaders headers
    ) throws URISyntaxException, IOException {
        CacheKeyReference cacheKeyReference = new CacheKeyReference();
        cacheKeyReference.setRequestBody(body);
        cacheKeyReference.setHttpMethod(method.name());
        cacheKeyReference.setRequestURI(request.getRequestURI());
        cacheKeyReference.setQueryString(request.getQueryString());
        cacheKeyReference.setHttpHeaders(headers);
        return proxyService.getServerResponse(cacheKeyReference);
    }






}
