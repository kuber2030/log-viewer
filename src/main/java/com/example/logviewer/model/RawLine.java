package com.example.logviewer.model;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/24 16:53
 */
public class RawLine {

    private String project;
    private String line;

    private String fileName;
    private boolean endLine;


    public RawLine(String project, String fileName, String line, boolean endLine) {
        this.project = project;
        this.fileName = fileName;
        this.line = line;
        this.endLine=endLine;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getEnv() {
        return fileName.contains("-prod-") ? "prod" : fileName.contains("-test-") ? "test" : "unknown";
    }

    @Override
    public String toString() {
        return "RawLine{" +
                "project='" + project + '\'' +
                ", line='" + line + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
