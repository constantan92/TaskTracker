package com.tasktracker.client.view.task;

import com.tasktracker.client.controller.ClientController;
import com.tasktracker.client.controller.TaskListener;
import com.tasktracker.shared.model.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.io.IOException;
import java.util.Collection;

public class TasksTree extends JTree implements TaskListener
{
    TaskTreeModel taskTreeModel;
    MutableTreeNode  rootNode;

    ClientController clientController;

    public TasksTree(ClientController clientController)
    {
        super();
        rootNode = new DefaultMutableTreeNode("root");
        taskTreeModel = new TaskTreeModel(clientController, rootNode);
        setModel(taskTreeModel);
        expandRow(0);
        setRootVisible(false);

        setEditable(true);
        getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        setShowsRootHandles(true);

        this.clientController = clientController;

        try
        {
            addTasksInitial(rootNode, clientController.getRootTasks());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addTasksInitial(MutableTreeNode parentNode, Collection<Task> tasks) throws IOException
    {
        for (Task task : tasks)
        {
            MutableTreeNode newNode = addTaskNode(parentNode, task);
            Collection<Task> subTasks = clientController.getSubTasks(task.getTaskID());
            if (!subTasks.isEmpty())
            {
                addTasksInitial(newNode, subTasks);
            }
        }
    }

    public void updateAll() throws IOException
    {
        for (int i = 0 ; i < rootNode.getChildCount(); i++)
        {
            rootNode.remove(i);
        }

        addTasksInitial(rootNode, clientController.getRootTasks());
    }

    public TaskNode addTaskNode(MutableTreeNode parentNode, Task task)
    {
        TaskNode childNode =
                new TaskNode(task);

        if (parentNode == null)
        {
            parentNode = rootNode;
        }

        taskTreeModel.insertNodeInto(childNode, parentNode,
                parentNode.getChildCount());

        scrollPathToVisible(new TreePath(childNode.getPath()));

        return childNode;
    }

    public void taskCreated(Task task)
    {
        MutableTreeNode parentNode = null;
        Integer parentID = task.getParentID();
        if (parentID != null)
        {
            try
            {
                parentNode = taskTreeModel.getTaskNode(parentID);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        addTaskNode(parentNode, task);
    }

    @Override
    public void taskChanged(Task task)
    {

    }


    public void taskDeleted(Integer taskID)
    {
        MutableTreeNode newNode = null;
        try
        {
            newNode = taskTreeModel.getTaskNode(taskID);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if (newNode != null) taskTreeModel.removeNodeFromParent(newNode);
    }
}