package com.tasktracker.client.controller;


import com.tasktracker.shared.model.Task;

public interface TaskListener
{
    public void taskCreated(Task task);

    public void taskChanged(Task task);

    public void taskDeleted(Integer taskID);
}
