package com.example.logviewer.controller;

import com.example.logviewer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

@Controller
public class LogController {
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public String viewLogs(
            @RequestParam(required = false, defaultValue = "space-api") String project,
            @RequestParam(required = false, defaultValue = "prod") String environment,
            @RequestParam(required = false) String logFile,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false, defaultValue = "") String threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) throws IOException {
        model.addAttribute("logs", logService.getLogs2(project, environment, logFile, searchText, threadId, page, size));
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("project", Optional.ofNullable(project).orElse(""));
        model.addAttribute("environment", Optional.ofNullable(environment).orElse(""));
        model.addAttribute("logFile", Optional.ofNullable(logFile).orElse(""));
        model.addAttribute("searchText", Optional.ofNullable(searchText).orElse(""));
        model.addAttribute("threadId", Optional.ofNullable(threadId).orElse(""));
        return "logs";
    }

}