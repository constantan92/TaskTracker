package com.tasktracker.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task implements Serializable
{
    private  Integer  taskID;
    private  String   name;
    private  Integer  parentID;
    private  Status   status;
    private  Integer  priority;
    private  Date     startDate;
    private  Date     endDate;
    private  String   description;

    private  List<TaskRelation> relations;

    public Task(Integer taskID, String name, Integer parentID)
    {
        /*if (taskID == null)
            throw new IllegalArgumentException("Parameter taskID can not be null!");*/
        if (name == null)
            throw new IllegalArgumentException("Parameter name can not be null!");
        this.taskID = taskID;
        this.name = name;
        this.parentID = parentID;
        relations = new ArrayList<TaskRelation>();
    }

    public Integer getTaskID()
    {
        return taskID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getParentID()
    {
        return parentID;
    }

    public void setParentID(Integer parentID)
    {
        this.parentID = parentID;
    }

    public Status getStatus() //todo for BD
    {
        return status;
    }

    public void setStatus(Status status) //todo for BD
    {
        this.status = status;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority(Integer priority)
    {
        if ((priority < MIN_PRIORITY) || priority > MAX_PRIORITY)
            throw new IllegalArgumentException("Invalid number for priority: " + priority);
        this.priority = priority;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<TaskRelation> getRelations()
    {
        return new ArrayList<TaskRelation>(relations);
    }

    public void addRelation(TaskRelation relation)
    {
        relations.add(relation);
    }

    public void deleteRelation(String user, TaskRelation.Relation relation)
    {
        TaskRelation taskRelationToRemove = null;
        for (TaskRelation taskRelation : relations)
        {
            if (taskRelation.getRelation().equals(relation) && taskRelation.getUser().equals(user))
            {
                taskRelationToRemove = taskRelation;
                break;
            }
        }
        if (taskRelationToRemove != null)
        {
            relations.remove(taskRelationToRemove);
        }
    }

    public List<TaskRelation> getRelationsByRelation(TaskRelation.Relation relation)
    {
        List<TaskRelation> result = new ArrayList<TaskRelation>();
        for (TaskRelation taskRelation : relations)
        {
            if (taskRelation.getRelation().equals(relation))
                result.add(taskRelation);
        }
        return result;
    }

    public List<TaskRelation> getRelationsByUser(String user)
    {
        List<TaskRelation> result = new ArrayList<TaskRelation>();
        for (TaskRelation taskRelation : relations)
        {
            if (taskRelation.getUser().equals(user))
                result.add(taskRelation);
        }
        return result;
    }

    @Override
    public String toString()
    {
        return "Task : " + name + ", ID = " + taskID;
    }

    public static Integer MAX_PRIORITY = 42;
    public static Integer MIN_PRIORITY = -42;

    public static enum Status implements Serializable
    {
        PLANNED,     // 0
        CREATED,     // 1
        SPLITTED,    // 2
        ASSIGNED,    // 3
        IN_PROGRESS, // 4
        ON_HOLD,     // 5
        RESOLVED,    // 6
        CLOSED,      // 7
        REOPENED,    // 8
        CANCELLED,   // 9
        REQUESTED    // 10
    }
}
