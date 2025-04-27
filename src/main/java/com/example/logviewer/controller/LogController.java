package com.example.logviewer.controller;

import com.example.logviewer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

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
 /*       if (!StringUtils.hasText(logFileName)) {
            // 自动选择目录下的第一份日志文件
            List<String> logFiles = logService.listLogFiles(project, environment);
            if (!logFiles.isEmpty()) {
                logFileName = logFiles.get(0);
            }
        }*/
        model.addAttribute("logs", logService.getLogs2(project, environment, logFile, searchText, threadId, page, size));
//        model.addAttribute("logFiles", logService.listLogFiles(project, environment));
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("project", project);
        model.addAttribute("environment", environment);
        model.addAttribute("logFile", logFile);
        model.addAttribute("searchText", searchText);
        model.addAttribute("threadId", threadId);
        return "logs";
    }

}