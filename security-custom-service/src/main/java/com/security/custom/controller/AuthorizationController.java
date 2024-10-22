package com.security.custom.controller;

import com.security.custom.configuration.rest.RestRoutes;
import com.security.custom.service.AuthorizationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(value = RestRoutes.AUTHORIZATION.ROOT)
@Validated
public class AuthorizationController extends BaseController {

    public final AuthorizationService service;


    @Autowired
    public AuthorizationController(@Lazy final AuthorizationService service) {
        this.service = service;
    }

}
