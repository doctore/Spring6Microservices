package com.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class BaseControllerTest {

    @Autowired
    protected ApplicationContext context;

}
