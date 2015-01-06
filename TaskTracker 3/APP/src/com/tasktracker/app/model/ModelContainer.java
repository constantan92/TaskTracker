package com.tasktracker.app.model;

import com.tasktracker.app.controller.TaskListener;

import java.io.Serializable;
import java.util.*;

public class ModelContainer implements Serializable
{
    private Map<Integer, Task> allTasks;
    private Map<Integer, List<Integer>> taskChildren;
    private Map<String, User> allUsers;

    private Integer lastTaskID;
    private Set<Integer> deletedTaskIDs;

    private static ModelContainer instance;

    private List<TaskListener> taskListeners;

    private ModelContainer()
    {
        allTasks = new HashMap<Integer, Task>();
        taskChildren = new HashMap<Integer, List<Integer>>();
        allUsers = new HashMap<String, User>();

        lastTaskID = 0;
        deletedTaskIDs = new HashSet<Integer>();

        taskListeners = new ArrayList<TaskListener>();
    }

    public static ModelContainer getInstance()
    {
        if (instance == null)
        {
            instance = new ModelContainer();
        }

        return instance;
    }

    public static void setInstance(ModelContainer modelContainer)
    {
       instance = modelContainer;
    }

    public Task getTask(Integer taskID)
    {
        return allTasks.get(taskID);
    }

    public Integer getNextTaskID()
    {
        if (deletedTaskIDs.isEmpty())
        {
            return lastTaskID + 1;
        }
        else
        {
            return deletedTaskIDs.iterator().next();
        }
    }

    public void addTask(Task task)
    {
        Integer taskID = task.getTaskID();
        allTasks.put(taskID, task);
        lastTaskID = taskID;
        if (deletedTaskIDs.contains(taskID))
        {
            deletedTaskIDs.remove(taskID);
        }

        Integer parentID = task.getParentID();
        if (parentID != null)
        {
            List<Integer> subTasks= taskChildren.get(parentID);
            if (subTasks == null)
            {
                subTasks = new ArrayList<Integer>();
                taskChildren.put(parentID, subTasks);
            }
            subTasks.add(taskID);
        }

        for (TaskListener taskListener : taskListeners)
        {
            taskListener.taskCreated(task);
        }
    }

    public void deleteTask(Integer taskID)
    {
        Task task = allTasks.get(taskID);

        List<Integer> subTasks= taskChildren.get(taskID);
        if (subTasks != null)
        {
            for (Integer subTaskID : new ArrayList<Integer>(subTasks))
            {
                deleteTask(subTaskID);
            }

        }

        for (TaskListener taskListener : taskListeners)
        {
            taskListener.taskDeleted(taskID);
        }

        allTasks.remove(taskID);
        deletedTaskIDs.add(taskID);

        Integer parentID = task.getParentID();
        if (parentID != null)
        {
            List<Integer> subTasksCache = taskChildren.get(parentID);
            subTasksCache.remove(taskID);
            if (subTasksCache.isEmpty())
            {
                taskChildren.remove(parentID);
            }
        }
    }

    public User getUser(String name)
    {
        return allUsers.get(name);
    }

    public Collection<User> getAllUsers()
    {
        return new ArrayList<User>(allUsers.values());
    }

    public void addUser(User user)
    {
        String userName = user.getName();
        allUsers.put(userName, user);
    }

    public void deleteUser(String name)
    {
        allUsers.remove(name);
    }

    public void changeUserName(String oldName, String newName)
    {
        User user = getUser(oldName);
        user.setName(newName);
        allUsers.remove(oldName);
        allUsers.put(newName, user);
    }

    public Collection<Task> getSubTasks(Integer parentTaskID)
    {
        List<Integer> subTaskIds = taskChildren.get(parentTaskID);
        if (subTaskIds == null)
        {
            return Collections.EMPTY_LIST;
        }

        List<Task> subTasks = new ArrayList<Task>();
        for (Integer subTaskId : subTaskIds)
        {
            subTasks.add(allTasks.get(subTaskId));
        }
        return subTasks;
    }

    public Collection<Task> getRootTasks()
    {
        Collection<Task> result = new ArrayList<Task>();
        for (Task task : allTasks.values())
        {
            if (task.getParentID() == null)
            {
                result.add(task);
            }
        }

        return result;
    }

    public void addTaskListener(TaskListener taskListener)
    {
        taskListeners.add(taskListener);
    }

    public void removeTaskListener(TaskListener taskListener)
    {
        taskListeners.remove(taskListener);
    }
}
