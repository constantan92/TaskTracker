package com.tasktracker.app.view.task;

import com.tasktracker.app.controller.TaskController;
import com.tasktracker.app.controller.TaskListener;
import com.tasktracker.app.model.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.Collection;

public class TasksTree extends JTree implements TaskListener
{
    TaskTreeModel taskTreeModel;
    MutableTreeNode  rootNode;

    public TasksTree()
    {
        super();
        rootNode = new DefaultMutableTreeNode("root");
        taskTreeModel = new TaskTreeModel(rootNode);
        setModel(taskTreeModel);
        expandRow(0);
        setRootVisible(false);

        setEditable(true);
        getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        setShowsRootHandles(true);

        addTasksInitial(rootNode, TaskController.getRootTasks());
    }

    private void addTasksInitial(MutableTreeNode parentNode, Collection<Task> tasks)
    {
        for (Task task : tasks)
        {
            MutableTreeNode newNode = addTaskNode(parentNode, task);
            Collection<Task> subTasks = TaskController.getSubTasks(task.getTaskID());
            if (!subTasks.isEmpty())
            {
                addTasksInitial(newNode, subTasks);
            }
        }
    }

    public void updateAll()
    {
        for (int i = 0 ; i < rootNode.getChildCount(); i++)
        {
            rootNode.remove(i);
        }

        addTasksInitial(rootNode, TaskController.getRootTasks());
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

    @Override
    public void taskCreated(Task task)
    {
        MutableTreeNode parentNode = null;
        Integer parentID = task.getParentID();
        if (parentID != null)
        {
            parentNode = taskTreeModel.getTaskNode(parentID);
        }

        addTaskNode(parentNode, task);
    }

    @Override
    public void taskChanged(Task task)
    {

    }

    @Override
    public void taskDeleted(Integer taskID)
    {
        MutableTreeNode newNode = taskTreeModel.getTaskNode(taskID);
        if (newNode != null) taskTreeModel.removeNodeFromParent(newNode);
    }
}