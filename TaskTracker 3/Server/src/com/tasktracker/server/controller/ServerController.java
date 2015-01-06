package com.tasktracker.server.controller;

import com.tasktracker.server.ModelContainer;
import com.tasktracker.shared.message.Message;
import com.tasktracker.shared.model.Task;
import com.tasktracker.shared.model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class ServerController
{
    private static List<Integer> lockedTasks;

    public ServerController()
    {
        lockedTasks = new ArrayList<Integer>();
    }

    private static ModelContainer getTaskContainer()
    {
        return ModelContainer.getInstance();
    }

    public static void responseRootTasks(ObjectOutputStream oos) throws IOException
    {
        oos.writeObject(getRootTasks());
        oos.flush();
        oos.reset();
    }

    public static void responseSubTasks(Integer parentTaskID, ObjectOutputStream oos) throws IOException
    {
        oos.writeObject(getSubTasks(parentTaskID));
        oos.flush();
        oos.reset();
    }

    public static Collection<Task> getRootTasks()
    {
        return getTaskContainer().getRootTasks();
    }

    public static Collection<Task> getSubTasks(Integer parentTaskID)
    {
        return getTaskContainer().getSubTasks(parentTaskID);
    }

    public static void responseAddTask(Message request, ObjectOutputStream oos) throws TaskTrackerLogicException, IOException
    {
        Iterator<String> it = request.getParameters().iterator();
        String name = it.next();
        String parentTaskID = it.next();

        Task newTask = new Task(getTaskContainer().getNextTaskID(), name, parentTaskID.equals("") ? null : Integer.valueOf(parentTaskID));
        addTask(newTask);

        oos.writeObject(newTask.getTaskID());
        oos.flush();
        oos.reset();
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

    public static void responseDeleteTask(Integer taskID) throws TaskTrackerLogicException, IOException
    {
        deleteTask(taskID);
    }

    public static void deleteTask(Integer taskID) throws TaskTrackerLogicException
    {
        if (getTaskContainer().getTask(taskID) == null)
            throw new TaskTrackerLogicException("Task with ID : " + taskID + " is not exists.");

        if (lockedTasks.contains(taskID))
        {
            throw new TaskTrackerLogicException("Task with ID "+taskID+" already locked");
        }

        getTaskContainer().deleteTask(taskID);
    }

    public static void responseChangeTask(Message request) throws TaskTrackerLogicException, IOException
    {
        Iterator<String> it = request.getParameters().iterator();
        Integer taskID = Integer.valueOf(it.next());
        if (lockedTasks.contains(taskID))
        {
            throw new TaskTrackerLogicException("Task with ID "+taskID+" already locked");
        }
        String name = it.next();
        String parentTaskID = it.next();
        String status = it.next();
        String prioroty = it.next();
        String startDate = it.next();
        String endDate = it.next();
        String description = it.next();

        Task task = getTaskContainer().getTask(taskID);
        task.setName(name);
        //task.setParentID(Integer.valueOf(parentTaskID));
        //task.setStatus(Task.Status.valueOf(status));
    }

    public static void responseGetAllUsers(ObjectOutputStream oos) throws IOException
    {
        oos.writeObject(getTaskContainer().getAllUsers());
        oos.flush();
        oos.reset();
    }

    public static void responseGetUser(String name, ObjectOutputStream oos) throws IOException
    {
        oos.writeObject(getTaskContainer().getUser(name));
        oos.flush();
        oos.reset();
    }

    public static void responseAddUser(String name) throws TaskTrackerLogicException
    {
        User user = new User(name, 0);
        addUser(user);
    }

    public static void responseDeleteUser(String name) throws TaskTrackerLogicException
    {
        deleteUser(name);
    }

    public static void responseChangeUserName(Message request) throws TaskTrackerLogicException
    {
        Iterator<String> it = request.getParameters().iterator();
        changeUserName(it.next(), it.next());
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

    public static void responseLockTask(Integer taskID) throws TaskTrackerLogicException
    {
        if (lockedTasks.contains(taskID))
        {
            throw new TaskTrackerLogicException("Task with ID "+taskID+" already locked");
        }
        lockedTasks.add(taskID);
    }

    public static void responseUnLockTask(Integer taskID)
    {
        lockedTasks.remove(taskID);
    }
}

