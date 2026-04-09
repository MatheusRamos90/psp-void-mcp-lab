package com.matheushrs.psp_void_orch.controller;

import com.matheushrs.psp_void_orch.client.CoreApiClient;
import com.matheushrs.psp_void_orch.dto.LogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "Operations audit log")
public class LogController {

    private final CoreApiClient coreClient;

    @GetMapping
    @Operation(summary = "List all audit logs")
    public List<LogResponse> findAll() {
        return coreClient.findAllLogs();
    }
}
