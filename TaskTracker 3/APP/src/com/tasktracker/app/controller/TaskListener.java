package com.tasktracker.app.controller;

import com.tasktracker.app.model.Task;

public interface TaskListener
{
    public void taskCreated(Task task);

    public void taskChanged(Task task);

    public void taskDeleted(Integer taskID);
}
