package com.example.logviewer.controller;

import com.example.logviewer.core.RoutingBlockQueue;
import com.example.logviewer.model.TableAnalysis;
import com.example.logviewer.model.TableSummaryAnalysis;
import com.example.logviewer.repository.TableRepository;
import com.example.logviewer.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class LogController {
    private final LogService logService;
    private final TableRepository tableRepository;
    private final RoutingBlockQueue routingBlockQueue;

    @Autowired
    public LogController(LogService logService, TableRepository tableRepository, RoutingBlockQueue routingBlockQueue) {
        this.logService = logService;
        this.tableRepository = tableRepository;
        this.routingBlockQueue = routingBlockQueue;
    }

    @GetMapping("/logs")
    public String viewLogs(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false, defaultValue = "space-api") String project,
            @RequestParam(required = false, defaultValue = "prod") String environment,
            @RequestParam(required = false) String level,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false, defaultValue = "") String threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model) throws IOException {
        // Set default time range to last 15 minutes if not specified
        if (startTime == null || endTime == null) {
            endTime = System.currentTimeMillis();
            startTime = endTime - (15 * 60 * 1000); // 15 minutes in milliseconds
        }
        model.addAttribute("logs", logService.getLogs2(startTime, endTime, project, environment, level, searchText, threadId, page, size));
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("project", Optional.ofNullable(project).orElse(""));
        model.addAttribute("environment", Optional.ofNullable(environment).orElse(""));
        model.addAttribute("level", Optional.ofNullable(level).orElse(""));
        model.addAttribute("searchText", Optional.ofNullable(searchText).orElse(""));
        model.addAttribute("threadId", Optional.ofNullable(threadId).orElse(""));
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "logs";
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String tableName) {
        try {
            tableRepository.deleteTables(tableName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/analysis")
    public String analysis(Model model) {
        TableSummaryAnalysis summaryAnalysis = tableRepository.summaryAnalysis();
        List<TableAnalysis> tableAnalysis = tableRepository.getTableSummaryAnalysis();
        model.addAttribute("summaryAnalysis", summaryAnalysis);
        model.addAttribute("tableAnalysis", tableAnalysis);
        model.addAttribute("queueStatus", routingBlockQueue.queueStatus());
        return "analysis";
    }

    @GetMapping("/no-permission")
    public String noPermission(Model model) {
        return "no-permission";
    }

}