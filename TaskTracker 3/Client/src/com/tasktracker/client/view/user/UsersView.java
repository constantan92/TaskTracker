package com.tasktracker.client.view.user;

import com.tasktracker.client.controller.TaskTrackerLogicException;
import com.tasktracker.shared.model.User;
import com.tasktracker.client.controller.ClientController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;

public class UsersView
{
    ClientController clientController;

    private int width;
    private int height;

    private JPanel mainPanel;
    private JList userList;
    JFormattedTextField nameCtrl;
    JFormattedTextField grantsCtrl;

    private User curUser;

    public UsersView(ClientController clientController, int width, int height)
    {
        this.clientController = clientController;
        this.width = width;
        this.height = height;

        mainPanel = new JPanel();
        mainPanel.setSize(width, height);
        mainPanel.setLayout(new GridLayout(1, 2));

        try
        {
            initUserListPanel();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        initUserParamsPanel();

        curUser = null;
    }

    private void initUserListPanel() throws IOException
    {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("All Users"));

        final DefaultListModel userListModel = new DefaultListModel();
        Collection<User> users = clientController.getAllUsers();
        for (User user : users)
        {
            userListModel.addElement(user.getName());
        }
        userList = new JList(userListModel);
        userList.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent me)
            {
                String selectedName = (String) userListModel.get(userList.getSelectedIndex());
                try
                {
                    curUser = clientController.getUser(selectedName);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                showUserParameters(curUser);
            }
        });

        listPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JButton addUserButton = new JButton("New User");
        addUserButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String name = "New User " + (userListModel.getSize() + 1);
                User user = new User(name, 0);
                try
                {
                    clientController.addUser(user);
                }
                catch (TaskTrackerLogicException e1)
                {
                    e1.printStackTrace();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                userListModel.addElement(user.getName());
            }
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String userName = (String) userListModel.get(userList.getSelectedIndex());
                try
                {
                    clientController.deleteUser(userName);
                }
                catch (TaskTrackerLogicException e1)
                {
                    showErrorDialog(e1.getMessage());
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                userListModel.remove(userList.getSelectedIndex());
                userList.clearSelection();
                showUserParameters(null);
            }
        });
        JPanel buttons = new JPanel(new GridLayout(1, 2));
        buttons.setMaximumSize(new Dimension(290, 50));
        buttons.add(addUserButton);
        buttons.add(deleteButton);

        listPanel.add(buttons, BorderLayout.SOUTH);

        mainPanel.add(listPanel);
    }

    private void initUserParamsPanel()
    {
        JPanel paramsPanel = new JPanel(new BorderLayout());
        paramsPanel.setBorder(BorderFactory.createTitledBorder("User Parameters"));
        paramsPanel.setMaximumSize(new Dimension(width, 200));

        JPanel paramsPanelGrid = new JPanel();
        paramsPanelGrid.setLayout(new BoxLayout(paramsPanelGrid, BoxLayout.Y_AXIS));


        nameCtrl = new JFormattedTextField();
        nameCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curUser != null && !curUser.getName().equals(nameCtrl.getText()))
                    nameCtrl.setBackground(Color.GREEN);
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        JPanel userNameParam = new JPanel();
        userNameParam.setLayout(new BoxLayout(userNameParam, BoxLayout.LINE_AXIS));
        JLabel nameForParam1 = new JLabel("User Name");
        nameForParam1.setMaximumSize(new Dimension(100, 25));
        userNameParam.add(nameForParam1, 0);
        nameCtrl.setMaximumSize(new Dimension(200, 25));
        userNameParam.add(nameCtrl, 1);
        paramsPanelGrid.add(userNameParam);

        grantsCtrl = new JFormattedTextField();
        grantsCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curUser != null && !curUser.getGrants().equals(Integer.valueOf(grantsCtrl.getText())))
                    grantsCtrl.setBackground(Color.GREEN);
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        JPanel userGrantsParam = new JPanel();
        userGrantsParam.setLayout(new BoxLayout(userGrantsParam, BoxLayout.LINE_AXIS));
        JLabel nameForParam2 = new JLabel("User Grants");
        nameForParam2.setMaximumSize(new Dimension(100, 25));
        userGrantsParam.add(nameForParam2, 0);
        grantsCtrl.setMaximumSize(new Dimension(200, 25));
        userGrantsParam.add(grantsCtrl, 1);
        paramsPanelGrid.add(userGrantsParam);

        paramsPanel.add(paramsPanelGrid, BorderLayout.CENTER);


        JPanel buttons = new JPanel(new GridLayout(0,2));
        JButton commitButton = new JButton("Update");
        commitButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (curUser == null) return;

                if (nameCtrl.getBackground().equals(Color.GREEN))
                {
                    String newValue = nameCtrl.getText();
                    try
                    {
                        clientController.changeUserName(curUser.getName(), newValue);
                    }
                    catch (TaskTrackerLogicException e1)
                    {
                        showErrorDialog(e1.getMessage());
                        newValue = null;
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                    if (newValue != null)
                    {
                        ((DefaultListModel)userList.getModel()).set(userList.getSelectedIndex(), newValue);
                    }
                    else
                    {
                        nameCtrl.setText(curUser.getName());
                    }
                    nameCtrl.setBackground(Color.WHITE);
                }
                if (grantsCtrl.getBackground().equals(Color.GREEN))
                {
                    Collection<User.Grant> newValue = null;
                    try
                    {
                        newValue = User.getGrantsByNumber(Integer.valueOf(grantsCtrl.getText()));
                    }
                    catch (IllegalArgumentException e1)
                    {
                        grantsCtrl.setBackground(Color.RED);
                    }
                    if (newValue != null)
                    {
                        Collection<User.Grant> oldValue = User.getGrantsByNumber(curUser.getGrants());
                        for (User.Grant newGrant : newValue)
                        {
                            if (!oldValue.contains(newGrant))
                            {
                                curUser.addGrant(newGrant);
                            }
                            else
                            {
                                oldValue.remove(newGrant);
                            }
                        }
                        for (User.Grant oldGrant : oldValue)
                        {
                            curUser.excludeGrant(oldGrant);
                        }

                        grantsCtrl.setBackground(Color.WHITE);
                    }
                }
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                showUserParameters(curUser);
            }
        });
        buttons.add(commitButton);
        buttons.add(cancelButton);

        paramsPanel.add(buttons, BorderLayout.SOUTH);

        mainPanel.add(paramsPanel);
    }

    public void showUserParameters(User user)
    {
        nameCtrl.setBackground(Color.WHITE);
        grantsCtrl.setBackground(Color.WHITE);

        nameCtrl.setText(user == null ? "" : user.getName());
        grantsCtrl.setText(user == null ? "" : user.getGrants().toString());

        nameCtrl.updateUI();
        grantsCtrl.updateUI();
    }

    public JPanel getPanel()
    {
        return mainPanel;
    }

    private void showErrorDialog(String message)
    {
        JDialog errorDialog = new JDialog();
        errorDialog.getContentPane().add(new JLabel(message));
        errorDialog.setVisible(true);
    }
}
