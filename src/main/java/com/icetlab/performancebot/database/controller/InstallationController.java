package com.icetlab.performancebot.database.controller;

import com.icetlab.performancebot.database.service.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstallationController {
    @Autowired
    private InstallationService service;
}
