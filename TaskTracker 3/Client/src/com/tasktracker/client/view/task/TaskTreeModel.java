package com.tasktracker.client.view.task;

import com.tasktracker.client.controller.ClientController;
import com.tasktracker.shared.model.Task;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;

public class TaskTreeModel extends DefaultTreeModel implements TreeModel
{
    ClientController clientController;

    public TaskTreeModel(ClientController clientController, TreeNode root)
    {
        super(root);
        this.clientController = clientController;
    }

    public TaskNode getTaskNode(Integer taskID) throws IOException
    {
        Task task = clientController.getTask(taskID);

        Stack<Integer> taskHierarchy = new Stack<Integer>();
        taskHierarchy.add(taskID);

        Integer parentID = task.getParentID();
        while (parentID != null)
        {
            taskHierarchy.add(parentID);
            parentID = clientController.getTask(parentID).getParentID();
        }

        TaskNode result = null;
        Enumeration<TaskNode> subTasks = root.children();
        while(true)
        {
            TaskNode curTaskNode = null;
            boolean taskNodeFound = false;
            Integer seekTaskID = taskHierarchy.pop();
            while(subTasks.hasMoreElements())
            {
                curTaskNode = subTasks.nextElement();
                if (curTaskNode.getTask().getTaskID().equals(seekTaskID))
                {
                    taskNodeFound = true;
                    break;
                }
            }
            if (taskNodeFound)
            {
                if (seekTaskID.equals(taskID))
                {
                    result = curTaskNode;
                    break;
                }
                else
                {
                    subTasks = curTaskNode.children();
                }
            }
            else
            {
                return null;
            }
        }

        return result;
    }
}
