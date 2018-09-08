package com.prem.springcache.controller;

import com.prem.springcache.cache.CacheKeyReference;
import com.prem.springcache.service.ProxyService;
import net.sf.ehcache.Ehcache;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        return proxyService.getServerResponse(body, method, request, headers);
    }






}
