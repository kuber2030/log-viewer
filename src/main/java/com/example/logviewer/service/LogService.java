package com.example.logviewer.service;

import com.example.logviewer.model.DateRange;
import com.example.logviewer.model.LogEntry;
import com.example.logviewer.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogRepository logRepository;
    // Regex to parse log line
    private static String logPattern = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[([^\\]]+)\\] (\\w+) +([^ ]+) - \\[([^:]+):(\\d+)\\] - (.+)$";
    private static Pattern pattern = Pattern.compile(logPattern);
    private static Pattern preCheckPattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[([^\\]]+)\\]");

    /*@Value("${log.directory:/mnt/ldshop/logs}")*/
    private String logDirectory;
/*    public List<LogEntry> getLogs(String project, String environment, String logFileName, String searchText, String threadId, int page, int size) throws IOException {
        Path logPath = Paths.get(logDirectory, project, logFileName);

        if (!Files.exists(logPath) || !Files.isRegularFile(logPath)) {
            throw new IllegalArgumentException("Log file does not exist: " + logPath);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        // Read and filter logs in reverse order
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(logPath.toFile(), StandardCharsets.UTF_8)) {
            List<String> lines = new ArrayList<>();
            Map<Integer, String> timeHoles = new HashMap<>();
            String line;
            int start = page * size;
            int end = start + size;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (index >= start && index < end) {
                    // 预检查每一列，如果不以时间开头，则继续向上读取，知道遇到一行能够读取到时间，这种情况，通常是打印异常堆栈导致的
                    Matcher matcher = preCheckPattern.matcher(line);
                    if (!matcher.find()) {
                        // 注意： 这一行不符合标准的日志格式，需要继续向上读取，直到遇到一行能够读取到时间
                        timeHoles.put(index, "NO_TIME");
                    } else { // 正常标准的日志
                        // check previous line：
                        if (index > 1 && "NO_TIME".equals(timeHoles.get(index-1))) {
                            timeHoles.put(index-1, matcher.group(1));
                            System.out.println("补齐时间空洞 " + matcher.group(1) + line);
                        }
                    }
                    lines.add(line);
                } else if (index >= end) {
                    break;
                }
                index++;
            }
            // 如果最后一行存在时间空洞，则需要一直往上查找了
            int lastIndex = index - 1;
            if (timeHoles.get(lastIndex) != null && timeHoles.get(lastIndex).equals("NO_TIME")) {
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = preCheckPattern.matcher(line);
                    if (matcher.find()) {
                        System.out.println("已经为最后一行找到时间 " + matcher.group(1));
                        timeHoles.put(lastIndex, matcher.group(1));
                        break;
                    }
                }
            }
            // 填补先前的行
            for (int i = lastIndex - 1; i >= 0 ; i--) {
                if (timeHoles.get(i) != null && timeHoles.get(i).equals("NO_TIME")) {
                    timeHoles.put(i, timeHoles.get(i+1));
                }
            }
            System.out.println(timeHoles);

            AtomicInteger idx = new AtomicInteger(page * size);
            List<LogEntry> collect = lines.stream()
                    .map(new Function<String, LogEntry>() {
                        private String lastThreadId = "unknown-thread";
                        @Override
                        public LogEntry apply(String line) {
                            try {
//                                System.out.println("---" + line);
                                int current = idx.getAndIncrement();
                                Matcher matcher = pattern.matcher(line);
                                if (matcher.matches()) {
                                    Date timestamp = simpleDateFormat.parse(matcher.group(1));
                                    String threadId = matcher.group(2);
                                    String level = matcher.group(3);
                                    String logger = matcher.group(4);
                                    String file = matcher.group(5);
                                    int lineNumber = Integer.parseInt(matcher.group(6));
                                    String message = matcher.group(7);

                                    // Update last successful values
                                    lastThreadId = threadId;
                                    return new LogEntry(timestamp, message, threadId, level);
                                } else {
                                    // Use last successful values for unparseable lines
                                    String timestamp = timeHoles.get(current);
                                    return new LogEntry(simpleDateFormat.parse(timestamp), line, lastThreadId, "ERROR");
                                }

                            } catch (Exception e) {
                                // Use last successful values for exceptions
                                logger.error("日志解析失败", e);
                                return new LogEntry(null, line, lastThreadId, "ERROR");
                            }
                        }
                    })
                    .collect(Collectors.toList());
             Collections.reverse(collect);
             return collect;
        } catch (Exception e) {
            throw new IOException("Error processing log file", e);
        }
    }*/

    public List<LogEntry> getLogs2(String project, String environment, String logFileName, String searchText, String threadId, int page, int size) throws IOException {
        return logRepository.query(new DateRange(1745573389838L, 1745586311323L), project, environment, logFileName, searchText, threadId);
    }

    public List<String> listLogFiles(String project, String environment) throws IOException {
        String envSuffix = environment.equalsIgnoreCase("prod") ? "-prod-" : "-test-";
        Path logPath = Paths.get(logDirectory, project);

        if (!Files.exists(logPath) || !Files.isDirectory(logPath)) {
            throw new IllegalArgumentException("Invalid project or environment");
        }

        try (Stream<Path> files = Files.list(logPath)) {
            return files
                    .filter(path -> path.getFileName().toString().contains(envSuffix))
                    .sorted(Comparator.<Path>comparingLong(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis();
                        } catch (IOException e) {
                            return 0L;  // 错误处理，返回默认值
                        }
                    }).reversed())
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        }
    }
}