package com.tasktracker.shared.model;

import java.io.Serializable;

public class TaskRelation implements Serializable
{
    private Integer  taskID;
    private String   user;
    private Relation relation;
    private String   comment;

    public TaskRelation(Integer taskID, String user, Relation relation, String comment)
    {
        if (taskID == null || user== null || relation == null)
            throw new IllegalArgumentException("Parameters can not be null!");
        this.taskID = taskID;
        this.user = user;
        this.relation = relation;
        this.comment = comment;
    }

    public Integer getTaskID()
    {
        return taskID;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public Relation getRelation() //todo for BD
    {
        return relation;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public static enum Relation implements Serializable
    {
        REQUEST_BY,    // 0
        CREATE_BY,     // 1
        ASSIGN_TO,     // 2
        MODIFY_BY,     // 3
        CONTRIBUTE_BY  // 4
    }
}



