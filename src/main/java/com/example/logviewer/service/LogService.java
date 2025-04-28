package com.example.logviewer.service;

import com.example.logviewer.model.DateRange;
import com.example.logviewer.model.LogEntry;
import com.example.logviewer.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogRepository logRepository;

    public List<LogEntry> getLogs2(Long startTime, Long endTime, String project, String environment, String level,
                                   String searchText, String threadId, int page, int size) throws IOException {
        return logRepository.query(new DateRange(startTime, endTime), project, environment, level, threadId, searchText, page, size);
    }
}