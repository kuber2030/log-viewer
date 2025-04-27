package com.example.logviewer.exception;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 13:15
 */
public class LogEntryWriteException extends RuntimeException{

    public LogEntryWriteException() {
        super();
    }
    public LogEntryWriteException(String message) {
        super(message);
    }
    public LogEntryWriteException(String message, Throwable cause) {
        super(message, cause);
    }

}
