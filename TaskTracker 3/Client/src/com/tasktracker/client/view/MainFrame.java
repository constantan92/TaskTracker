package com.tasktracker.client.view;

import com.tasktracker.client.controller.ClientController;
import com.tasktracker.client.view.task.TasksView;
import com.tasktracker.client.view.user.UsersView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MainFrame extends JFrame
{
    ClientController clientController;

    static private final int WIDTH = 660;
    static private final int HEIGHT = 600;

    TasksView tasksView;
    UsersView usersView;

    public MainFrame(ClientController clientController)
    {
        super("Task Tracker");
        this.clientController = clientController;
        setSize(WIDTH, HEIGHT);
        setLocation(50,50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initMenu();
        JTabbedPane tabPane = new JTabbedPane();

        tasksView = new TasksView(clientController, WIDTH, HEIGHT);
        tabPane.addTab("Tasks", tasksView.getPanel());

        usersView = new UsersView(clientController, WIDTH, HEIGHT);
        tabPane.addTab("Users", usersView.getPanel());

        getContentPane().add(tabPane);
    }

    private void initMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        /*JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try
                    {
                        File file = fileopen.getSelectedFile();
                        FileInputStream fis = new FileInputStream(file);
                        ObjectInputStream oin = new ObjectInputStream(fis);
                        ModelContainer modelContainer = (ModelContainer) oin.readObject();
                        ModelContainer.setInstance(modelContainer);
                        tasksView.getTasksTree().updateAll();
                    } catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    } catch (ClassNotFoundException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Save file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try
                    {
                        File file = fileopen.getSelectedFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(ModelContainer.getInstance());
                        oos.flush();
                        oos.close();
                    } catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);*/
        JMenu connectionMenu = new JMenu("Connection");

        JMenuItem connectItem = new JMenuItem("Connect");
        connectItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                clientController = new ClientController();
            }
        });
        connectionMenu.add(connectItem);
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    clientController.closeAll();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        connectionMenu.add(closeItem);

        menuBar.add(connectionMenu);

        this.setJMenuBar(menuBar);
    }
}
