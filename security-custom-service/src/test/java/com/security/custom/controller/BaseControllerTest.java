package com.security.custom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class BaseControllerTest {

    @Autowired
    protected ApplicationContext context;

    // To avoid Hazelcast instance creation
    @MockitoBean
    @Qualifier("cacheManager")
    private CacheManager mockCacheManager;

}
