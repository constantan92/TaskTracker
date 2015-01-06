package com.tasktracker.client.view.task;

import com.tasktracker.client.controller.ClientController;
import com.tasktracker.client.controller.TaskTrackerLogicException;
import com.tasktracker.shared.model.Task;
import com.tasktracker.shared.model.TaskRelation;
import com.tasktracker.shared.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

public class TaskParametersView
{
    ClientController clientController;

    private JPanel containerPanel;
    private JPanel paramsPanel;
    private JPanel relationsPanel;

    private int width;
    private int height;

    Task curTask;

    private Map<Parameter, JTextComponent> parameters;
    private Map<TaskRelation.Relation, JList> relations;
    JList curList;

    public TaskParametersView(final ClientController clientController, Dimension dimension)
    {
        this.clientController = clientController;

        containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setMaximumSize(new Dimension(dimension.width + 100, 1920));
        containerPanel.setPreferredSize(new Dimension(dimension.width, dimension.height));
        containerPanel.setBorder(BorderFactory.createTitledBorder("Task"));
        width = dimension.width;
        height = dimension.height;

        paramsPanel = new JPanel();
        initParamsPanel();
        relationsPanel = new JPanel();
        initRelationsPanel();

        JPanel panelToScroll = new JPanel();
        panelToScroll.setLayout(new BoxLayout(panelToScroll, BoxLayout.Y_AXIS));
        panelToScroll.add(paramsPanel);
        panelToScroll.add(relationsPanel);

        JScrollPane scrollPaneParams = new JScrollPane(panelToScroll);
        scrollPaneParams.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneParams.setBorder(BorderFactory.createEmptyBorder());

        containerPanel.add(scrollPaneParams, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(0,2));
        JButton commitButton = new JButton("Update");
        commitButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (curTask == null) return;
                for (Map.Entry<Parameter, JTextComponent> entry : parameters.entrySet())
                {
                    JTextComponent textComponent = entry.getValue();
                    Parameter parameter = entry.getKey();
                    if (textComponent.getBackground().equals(Color.GREEN))
                    {
                        try
                        {
                            String newValue = textComponent.getText();
                            switch(parameter)
                            {
                                case NAME: curTask.setName(newValue); break;
                                    //case TASK_ID: curTask.setName(textComponent.getText());
                                case PARENT_ID: curTask.setParentID(Integer.valueOf(newValue)); break;
                                case STATUS: curTask.setStatus(Task.Status.valueOf(newValue)); break;
                                case PRIORITY: curTask.setPriority(Integer.valueOf(newValue)); break;
                                case START_DATE: curTask.setStartDate(DateFormat.getInstance().parse(newValue)); break;
                                case END_DATE: curTask.setEndDate(DateFormat.getInstance().parse(newValue)); break;
                                case DESCRIPTION: curTask.setDescription(newValue); break;
                            }
                        }
                        catch (ParseException e1)
                        {
                            e1.printStackTrace();
                        }
                        textComponent.setBackground(Color.WHITE);
                    }
                }
                for (Map.Entry<TaskRelation.Relation, JList> entry : relations.entrySet())
                {
                    JList jList = entry.getValue();
                    TaskRelation.Relation relation = entry.getKey();
                    if (jList.getBackground().equals(Color.GREEN))
                    {
                        DefaultListModel model = (DefaultListModel) jList.getModel();
                        ArrayList<String> newValues = new ArrayList<String>();
                        ArrayList<String> oldValues = new ArrayList<String>();
                        for (int i = 0; i < model.getSize(); i++)
                        {
                            newValues.add( (String) model.get(i));
                        }
                        for (TaskRelation taskRelation : curTask.getRelationsByRelation(relation))
                        {
                            oldValues.add(taskRelation.getUser());
                        }
                        for (String newValue : newValues)
                        {
                            if (!oldValues.contains(newValue))
                            {
                                TaskRelation newTaskRelation = new TaskRelation(curTask.getTaskID(),newValue, relation, "");
                                curTask.addRelation(newTaskRelation);
                            }
                            else
                            {
                                oldValues.remove(newValue);
                            }
                        }
                        for (String oldValueToDelete : oldValues)
                        {
                            curTask.deleteRelation(oldValueToDelete, relation);
                        }
                    }

                    entry.getValue().setBackground(Color.WHITE);
                }
                try
                {
                    clientController.taskChanged(curTask);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                showTaskParameters(curTask);
            }
        });
        buttons.add(commitButton);
        buttons.add(cancelButton);

        containerPanel.add(buttons, BorderLayout.SOUTH);
    }

    private void initParamsPanel()
    {
        paramsPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
        paramsPanel.setMaximumSize(new Dimension(width, 300));
        paramsPanel.setPreferredSize(new Dimension(width, (int) (height * 0.5)));

        parameters = new HashMap<Parameter, JTextComponent>();

        final JFormattedTextField nameCtrl = new JFormattedTextField();
        nameCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null && !curTask.getName().equals(nameCtrl.getText()))
                    nameCtrl.setBackground(Color.GREEN);
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.NAME, nameCtrl);

        final JFormattedTextField taskIDCtrl = new JFormattedTextField();
        taskIDCtrl.setEditable(false);
        addParamRow(Parameter.TASK_ID, taskIDCtrl);

        final JFormattedTextField parentIDCtrl = new JFormattedTextField();
        parentIDCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null)
                {
                    Integer parentID = curTask.getParentID();
                    Integer newValue;
                    try
                    {
                        newValue = Integer.valueOf(parentIDCtrl.getText());
                    }
                    catch (NumberFormatException e1)
                    {
                        newValue = null;
                        parentIDCtrl.setBackground(Color.RED);
                    }
                    if (newValue != null && !newValue.equals(parentID))
                    {
                        parentIDCtrl.setBackground(Color.GREEN);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.PARENT_ID, parentIDCtrl);

        final JFormattedTextField statusCtrl = new JFormattedTextField();
        statusCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null)
                {
                    Task.Status status = curTask.getStatus();
                    Task.Status newValue;
                    try
                    {
                        newValue = Task.Status.valueOf(statusCtrl.getText());
                    }
                    catch (IllegalArgumentException e1)
                    {
                        newValue = null;
                        statusCtrl.setBackground(Color.RED);
                    }
                    if (newValue != null && !newValue.equals(status))
                    {
                        statusCtrl.setBackground(Color.GREEN);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.STATUS, statusCtrl);

        final JFormattedTextField priorityCtrl = new JFormattedTextField();
        priorityCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null)
                {
                    Integer priority = curTask.getPriority();
                    Integer newValue;
                    try
                    {
                        newValue = Integer.valueOf(priorityCtrl.getText());
                    }
                    catch (NumberFormatException e1)
                    {
                        newValue = null;
                        priorityCtrl.setBackground(Color.RED);
                    }
                    if (newValue != null && !newValue.equals(priority))
                    {
                        priorityCtrl.setBackground(Color.GREEN);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.PRIORITY, priorityCtrl);

        final JFormattedTextField startDateCtrl = new JFormattedTextField();
        startDateCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null)
                {
                    Date startDate = curTask.getStartDate();
                    Date newValue;
                    try
                    {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        newValue = dateFormat.parse(startDateCtrl.getText());
                    }
                    catch (ParseException e1)
                    {
                        newValue = null;
                        startDateCtrl.setBackground(Color.RED);
                    }
                    if (newValue != null && !newValue.equals(startDate))
                    {
                        startDateCtrl.setBackground(Color.GREEN);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.START_DATE, startDateCtrl);

        final JFormattedTextField endDateCtrl = new JFormattedTextField();
        endDateCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null)
                {
                    Date endDate = curTask.getEndDate();
                    Date newValue;
                    try
                    {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        newValue = dateFormat.parse(endDateCtrl.getText());
                    }
                    catch (ParseException e1)
                    {
                        newValue = null;
                        endDateCtrl.setBackground(Color.RED);
                    }
                    if (newValue != null && !newValue.equals(endDate))
                    {
                        endDateCtrl.setBackground(Color.GREEN);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.END_DATE, endDateCtrl);

        final JFormattedTextField descrCtrl = new JFormattedTextField();
        descrCtrl.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                if (curTask != null && !descrCtrl.getText().equals(curTask.getDescription()))
                    descrCtrl.setBackground(Color.GREEN);
            }

            @Override
            public void removeUpdate(DocumentEvent e){}

            @Override
            public void changedUpdate(DocumentEvent e){}
        });
        addParamRow(Parameter.DESCRIPTION, descrCtrl);

        paramsPanel.setLayout(new GridLayout(parameters.size(), 1));
    }

    private void addParamRow(Parameter parameter, JTextComponent valueControl)
    {
        JPanel param = new JPanel();
        param.setLayout(new BoxLayout(param, BoxLayout.LINE_AXIS));

        JLabel paramName = new JLabel(parameter.name());
        paramName.setPreferredSize(new Dimension(100, 25));
        paramName.setMaximumSize(new Dimension(150, 30));
        param.add(paramName, 0);

        valueControl.setPreferredSize(new Dimension(150, 25));
        valueControl.setMaximumSize(new Dimension(200, 30));
        param.add(valueControl, 1);

        paramsPanel.add(param);
        parameters.put(parameter, valueControl);
    }

    private void initRelationsPanel()
    {
        relationsPanel.setBorder(BorderFactory.createTitledBorder("Relations with Users"));
        relationsPanel.setLayout(new GridBagLayout());

        relations = new HashMap<TaskRelation.Relation, JList>();

        final JFrame selectUserDialog = new JFrame();
        selectUserDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        selectUserDialog.setSize(300, 400);
        selectUserDialog.getContentPane().setLayout(new BorderLayout());
        selectUserDialog.setVisible(false);

        final DefaultListModel userListModel = new DefaultListModel();
        /*Collection<User> users = null;
        try
        {
            users = clientController.getAllUsers();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        for (User user : users)
        {
            userListModel.addElement(user.getName());
        }*/
        final JList userList = new JList(userListModel);
        selectUserDialog.getContentPane().add( new JScrollPane(userList), BorderLayout.CENTER);

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ((DefaultListModel)curList.getModel()).add(
                        curList.getModel().getSize(),
                        userListModel.get(userList.getSelectedIndex())
                );
                curList.setBackground(Color.GREEN);
            }
        });
        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectUserDialog.setVisible(false);
            }
        });
        JPanel buttons = new JPanel(new GridLayout(1, 2));
        buttons.add(selectButton);
        buttons.add(cancelButton);

        selectUserDialog.getContentPane().add(buttons, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        int i = 0;
        for (TaskRelation.Relation relation : TaskRelation.Relation.values())
        {
            JLabel relationName = new JLabel(relation.name());
            relationName.setPreferredSize(new Dimension(95, 30));
            relationName.setMaximumSize(new Dimension(150, 30));
            c.fill = GridBagConstraints.NONE;
            c.anchor = (i == 0) ? GridBagConstraints.FIRST_LINE_START: GridBagConstraints.LINE_START;
            c.insets = (i == 0) ? new Insets(20,3,4,2): new Insets(4,3,4,2);
            c.gridx = 0;
            c.gridy = i;
            relationsPanel.add(relationName, c);

            final DefaultListModel listModel = new DefaultListModel();
            final JList list = new JList(listModel);
            relations.put(relation, list);
            c.fill = GridBagConstraints.NONE;
            c.anchor = (i == 0) ? GridBagConstraints.PAGE_START: GridBagConstraints.CENTER;
            c.insets = (i == 0) ? new Insets(20,2,4,2): new Insets(4,2,4,2);
            c.gridx = 1;
            c.gridy = i;
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setMaximumSize(new Dimension(220, 200));
            scrollPane.setPreferredSize(new Dimension(180, 60));
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            relationsPanel.add(scrollPane, c);

            JPanel smallButtons = new JPanel(new GridLayout(2,1));
            JButton addButton = new JButton("+");
            addButton.addActionListener(new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    curList = list;
                    selectUserDialog.setVisible(true);
                }
            });
            smallButtons.add(addButton, 0);

            JButton removeButton = new JButton("-");
            removeButton.addActionListener(new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    listModel.remove(list.getSelectedIndex());
                    list.setBackground(Color.GREEN);
                }
            });
            smallButtons.add(removeButton, 1);

            c.fill = GridBagConstraints.NONE;
            c.gridx = 2;
            c.gridy = i;
            c.insets = (i == 0) ? new Insets(20,2,4,5): new Insets(4,2,4,5);
            c.anchor = (i == 0) ? GridBagConstraints.FIRST_LINE_END: GridBagConstraints.LINE_END;
            relationsPanel.add(smallButtons, c);

            i++;
        }
    }

    public void showTaskInfo(Task task)
    {
        TitledBorder title = (TitledBorder) containerPanel.getBorder();
        title.setTitle(task == null ? "Task" : task.getName());

        if (curTask != null)
        {
            try
            {
                clientController.unLockTask(curTask.getTaskID());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        curTask = task;

        try
        {
            clientController.lockTask(task.getTaskID());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TaskTrackerLogicException e)
        {
            e.printStackTrace();
        }

        showTaskParameters(task);
        showTaskRelations(task);

        containerPanel.updateUI();
    }

    private void showTaskParameters(Task task)
    {
        String taskName = task == null ? null : task.getName();
        JTextComponent nameCtrl = parameters.get(Parameter.NAME);
        nameCtrl.setText(taskName == null ? "" : taskName);
        nameCtrl.setBackground(Color.WHITE);

        Integer taskID = task == null ? null : task.getTaskID();
        JTextComponent taskIDCtrl = parameters.get(Parameter.TASK_ID);
        taskIDCtrl.setText(taskID == null ? "" : taskID.toString());
        taskIDCtrl.setBackground(Color.WHITE);

        Integer taskParentID = task == null ? null : task.getParentID();
        JTextComponent taskParentIDCtrl = parameters.get(Parameter.PARENT_ID);
        taskParentIDCtrl.setText(taskParentID == null ? "" : taskParentID.toString());
        taskParentIDCtrl.setBackground(Color.WHITE);

        Task.Status taskStatus = task == null ? null : task.getStatus();
        JTextComponent taskStatusCtrl = parameters.get(Parameter.STATUS);
        taskStatusCtrl.setText(taskStatus == null ? "" : taskStatus.toString());
        taskStatusCtrl.setBackground(Color.WHITE);

        Integer taskPriority = task == null ? null : task.getParentID();
        JTextComponent taskPriorityCtrl = parameters.get(Parameter.PRIORITY);
        taskPriorityCtrl.setText(taskPriority == null ? "" : taskPriority.toString());
        taskPriorityCtrl.setBackground(Color.WHITE);

        Date startDate = task == null ? null : task.getStartDate();
        JTextComponent taskStartDateCtrl = parameters.get(Parameter.START_DATE);
        taskStartDateCtrl.setText(startDate == null ? "" : startDate.toString());
        taskStartDateCtrl.setBackground(Color.WHITE);

        Date endDate = task == null ? null : task.getEndDate();
        JTextComponent taskEndDateCtrl = parameters.get(Parameter.END_DATE);
        taskEndDateCtrl.setText(endDate == null ? "" : endDate.toString());
        taskEndDateCtrl.setBackground(Color.WHITE);

        String taskDescription = task == null ? null : task.getDescription();
        JTextComponent descrCtrl = parameters.get(Parameter.DESCRIPTION);
        descrCtrl.setText(taskDescription == null ? "" : taskDescription);
        descrCtrl.setBackground(Color.WHITE);
    }

    private void showTaskRelations(Task task)
    {
        for (Map.Entry<TaskRelation.Relation, JList> entry : relations.entrySet())
        {
            DefaultListModel model = (DefaultListModel) entry.getValue().getModel();
            model.clear();
            if (task != null)
            {
                for (TaskRelation relation : task.getRelationsByRelation(entry.getKey()))
                {
                    model.addElement(relation.getUser());
                }
            }
            entry.getValue().setBackground(Color.WHITE);
        }
    }

    public JPanel getContainerPanel()
    {
        return containerPanel;
    }

    public JPanel getParamsPanel()
    {
        return paramsPanel;
    }

    public JPanel getRelationsPanel()
    {
        return relationsPanel;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public enum Parameter
    {
       NAME,
       TASK_ID,
       PARENT_ID,
       STATUS,
       PRIORITY,
       START_DATE,
       END_DATE,
       DESCRIPTION;
    }
}
