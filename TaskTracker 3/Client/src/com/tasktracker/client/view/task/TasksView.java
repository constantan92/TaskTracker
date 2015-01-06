package com.tasktracker.client.view.task;

import com.tasktracker.client.controller.TaskTrackerLogicException;
import com.tasktracker.shared.model.Task;
import com.tasktracker.client.controller.ClientController;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class TasksView
{
    ClientController clientController;

    private int width;
    private int height;

    private JPanel mainPanel;
    private JPanel tasksTreePanel;
    private TasksTree tasksTree;
    private TaskParametersView taskParametersView;

    public TasksView(ClientController clientController, int width, int height)
    {
        this.clientController = clientController;
        this.width = width;
        this.height = height;

        mainPanel = new JPanel();
        mainPanel.setSize(width, height);
        mainPanel.setLayout(new BoxLayout (mainPanel, BoxLayout.LINE_AXIS));

        initTasksTreePanel();
        initTaskPanel();
    }

    private void initTasksTreePanel()
    {
        int lWidth = (int) (width*0.4);
        int lHeight = (int) (height*0.95);

        tasksTreePanel = new JPanel();
        tasksTreePanel.setLayout(new BorderLayout());
        tasksTreePanel.setLocation(5, 5);
        tasksTreePanel.setPreferredSize(new Dimension(lWidth, lHeight));
        tasksTreePanel.setBorder(BorderFactory.createTitledBorder("Tasks"));

        tasksTree = new TasksTree(clientController);
        clientController.addTaskListener(tasksTree);
        //ModelContainer.getInstance().addTaskListener(tasksTree);

        tasksTree.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent me)
            {
                TreePath tp = tasksTree.getPathForLocation(me.getX(), me.getY());
                if (tp != null)
                {
                    TaskNode taskNode = (TaskNode) tp.getLastPathComponent();
                    if (taskNode != null)
                    {
                        taskParametersView.showTaskInfo(taskNode.getTask());
                    }
                }
                else
                {
                    tasksTree.removeSelectionPath(tasksTree.getSelectionPath());
                }
            }
        });
        tasksTreePanel.add(new JScrollPane(tasksTree), BorderLayout.CENTER);

        //********Task Tree buttons*********
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                TreePath tp = tasksTree.getSelectionPath();
                TaskNode parentTaskNode = null;
                if (tp != null)
                    parentTaskNode = (TaskNode) tp.getLastPathComponent();

                Integer parentTaskID = (parentTaskNode != null) ? parentTaskNode.getTask().getTaskID() : null;
                try
                {
                    Integer taskID = clientController.addTask("New Task", parentTaskID);
                    Task task = new Task(taskID, "New Task", parentTaskID);
                    tasksTree.taskCreated(task);
                }
                catch (TaskTrackerLogicException e)
                {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        JButton deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                TreePath tp = tasksTree.getSelectionPath();
                if (tp != null)
                {
                    Integer taskID = ((TaskNode) tp.getLastPathComponent()).getTask().getTaskID();
                    try
                    {
                        try
                        {
                            clientController.deleteTask(taskID);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        tasksTree.taskDeleted(taskID);
                    }
                    catch (TaskTrackerLogicException e)
                    {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                    taskParametersView.showTaskInfo(null);
                }
            }
        });

        JPanel buttonsPanel = new JPanel(new GridLayout(0,2));
        buttonsPanel.add(addTaskButton);
        buttonsPanel.add(deleteTaskButton);

        tasksTreePanel.add(buttonsPanel, BorderLayout.SOUTH);


        mainPanel.add(tasksTreePanel);
    }

    private void initTaskPanel()
    {
        taskParametersView = new TaskParametersView(
                clientController,
                new Dimension((int) (width*0.6), (int) (height*0.95)));
        mainPanel.add(taskParametersView.getContainerPanel());
    }

    public JPanel getPanel()
    {
        return mainPanel;
    }


    public TaskParametersView getTaskParametersView()
    {
        return taskParametersView;
    }


    public TasksTree getTasksTree()
    {
        return tasksTree;
    }
}
