package com.prem.springcache.controller;

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

    @Value("${remote.server.name}")
    String remoteServerName;

    @Value("${remote.server.port}")
    int remoteServerPort;

    @Autowired
    OkHttpClient okHttpClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CacheManager cacheManager;

    Logger logger = LoggerFactory.getLogger(ProxyController.class);


    @RequestMapping("/**")
    @Cacheable(value = "primeCache",keyGenerator = "customKeyGen")
    public String mirrorRest(@RequestBody @Nullable String body,
                             HttpMethod method,
                             HttpServletRequest request,
                             @RequestHeader HttpHeaders headers
    ) throws URISyntaxException, IOException {

        URI uri = new URI("http", null, remoteServerName, remoteServerPort, request.getRequestURI(), request.getQueryString(), null);
        Map<String, String> reqHeader = new HashMap<>();
        headers.keySet().forEach(k -> reqHeader.put(k, this.flattenList(headers.get(k))));
        logger.debug("Cache Miss. Requesting server url: "+uri.toString());
        Request clientRequest = new Request.Builder()
                .headers(Headers.of(reqHeader))
                .url(uri.toString())
                .build();
        Response response = okHttpClient.newCall(clientRequest).execute();
        return response.body().string();
    }

    @RequestMapping("/cache")
    public void printCacheContent(){
        System.out.println("Printing Cache content: ");
        System.out.println("***********************************************************");
        Cache cache = cacheManager.getCache("primeCache");
        Ehcache ehcache = (Ehcache) cache.getNativeCache();
        List<Object> cacheKeys = ehcache.getKeys();

        cacheKeys.forEach(key -> {
            System.out.println("Key = " + key.toString()+", Value Length = "+cache.get(key).toString().length());
        });
        System.out.println("***********************************************************");
    }

    @GetMapping("/proxy/path/**")
    public ResponseEntity<?> proxyPath(ProxyExchange<?> proxy) throws Exception {
        String path = proxy.path("/proxy/path/");
        return proxy.uri(remoteServerName+":"+remoteServerPort+path).get();
    }

    private String flattenList(List<String> values) {
        StringBuffer sb = new StringBuffer();
        values.forEach(v -> sb.append(v + ", "));
        String flattenedValue = sb.toString();
        return flattenedValue.substring(0, flattenedValue.length() - 2);
    }

}
