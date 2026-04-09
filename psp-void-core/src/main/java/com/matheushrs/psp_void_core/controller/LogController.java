package com.matheushrs.psp_void_core.controller;

import com.matheushrs.psp_void_core.dto.LogResponse;
import com.matheushrs.psp_void_core.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService service;

    @GetMapping
    public List<LogResponse> findAll() {
        return service.findAll();
    }
}
