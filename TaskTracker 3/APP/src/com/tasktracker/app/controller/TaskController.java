package com.tasktracker.app.controller;

import com.tasktracker.app.model.ModelContainer;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.model.User;

import java.util.Collection;

public class TaskController
{

    public TaskController()
    {
    }

    private static ModelContainer getTaskContainer()
    {
        return ModelContainer.getInstance();
    }

    public static Collection<Task> getRootTasks()
    {
        return getTaskContainer().getRootTasks();
    }

    public static Collection<Task> getSubTasks(Integer parentTaskID)
    {
        return getTaskContainer().getSubTasks(parentTaskID);
    }

    public static void addTask(Task task) throws TaskTrackerLogicException
    {
        Integer taskID = task.getTaskID();
        if (getTaskContainer().getTask(taskID) != null)
            throw new TaskTrackerLogicException("Task with same ID already exists.");

        Integer taskParentID = task.getParentID();
        if (taskParentID != null && getTaskContainer().getTask(taskParentID) == null)
            throw new TaskTrackerLogicException("Parent Task with ID : " + taskParentID + " is not exists.");

        getTaskContainer().addTask(task);
    }

    public static void deleteTask(Integer taskID) throws TaskTrackerLogicException
    {
        if (getTaskContainer().getTask(taskID) == null)
            throw new TaskTrackerLogicException("Task with ID : " + taskID + " is not exists.");

        getTaskContainer().deleteTask(taskID);
    }

    public static void addUser(User user) throws TaskTrackerLogicException
    {
        String userName = user.getName();
        if (getTaskContainer().getUser(userName) != null)
            throw new TaskTrackerLogicException("User with same name already exists: " + userName);

        getTaskContainer().addUser(user);
    }

    public static void deleteUser(String name) throws TaskTrackerLogicException
    {
        if (getTaskContainer().getUser(name) == null)
            throw new TaskTrackerLogicException("User with name : " + name + " is not exists.");

        getTaskContainer().deleteUser(name);
    }

    public static void changeUserName(String oldName, String newName) throws TaskTrackerLogicException
    {
        if (getTaskContainer().getUser(newName) != null)
            throw new TaskTrackerLogicException("User with same name already exists: " + newName);

        getTaskContainer().changeUserName(oldName, newName);
    }
}

