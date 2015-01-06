package com.tasktracker.client.view.task;

import com.tasktracker.shared.model.Task;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class TaskNode extends DefaultMutableTreeNode implements TreeNode
{
    public TaskNode(Task task)
    {
        super(task);
    }

    public Task getTask()
    {
         return (Task) userObject;
    }

    @Override
    public String toString()
    {
        return ((Task)userObject).getName();
    }
}
