package com.neo.satya.to_doweekly;

/**
 * Created by satya on 2016-09-09.
 */
public class Tasks {

    // info to be displayed on each item of listView
    private int taskId;
    private String taskName;
    private String taskCreationTime;
    private String taskCreationDate;
    private String taskCreationDay;
    private int taskStatus;

   // constructor for initialization

    public Tasks(int taskId, String taskName, String taskCreationTime, String taskCreationDay, String taskCreationDate, int taskStatus) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskCreationTime = taskCreationTime;
        this.taskCreationDate = taskCreationDate;
        this.taskCreationDay = taskCreationDay;
        this.taskStatus = taskStatus;
    }

    // getter and setter methods

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {return taskName;}

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskCreationTime() {
        return taskCreationTime;
    }

    public void setTaskCreationTime(String taskCreationTime) {this.taskCreationTime = taskCreationTime;}

    public String getTaskCreationDate() {
        return taskCreationDate;
    }

    public void setTaskCreationDate(String taskCreationDate) {this.taskCreationDate = taskCreationDate;}

    public String getTaskCreationDay() {
        return taskCreationDay;
    }

    public void setTaskCreationDay(String taskCreationDay) {this.taskCreationDay = taskCreationDay;}

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }
}
