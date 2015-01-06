package com.tasktracker.client.controller;

import com.tasktracker.shared.message.Message;
import com.tasktracker.shared.message.MessageType;
import com.tasktracker.shared.model.Task;
import com.tasktracker.shared.model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ClientController
{
    Socket socket;

    //private InputStream is = null;
    //private OutputStream os = null;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    boolean stop;

    List<TaskListener> listeners;

    public ClientController()
    {
        stop = false;
        listeners = new ArrayList<TaskListener>();
        try
        {
            socket = new Socket("127.0.0.1", 1234);
            try
            {
                //is = socket.getInputStream();
                //os = socket.getOutputStream();
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Server object streams accepted");

                /*new Thread() {
                    @Override
                    public void run() {
                        while (!stop) {
                            try {
                                Message m = (Message) ois.readObject();
                                switch (m.getType())
                                {
                                    case TASK_ADDED:
                                        Iterator<String> it1 = m.getParameters().iterator();
                                        Integer taskID1 = Integer.valueOf(it1.next());
                                        Task newTask = getTask(taskID1);
                                        for (TaskListener listener : listeners)
                                        {
                                            listener.taskCreated(newTask);
                                        }
                                    case TASK_DELETED:
                                        Iterator<String> it2= m.getParameters().iterator();
                                        Integer taskID2 = Integer.valueOf(it2.next());
                                        for (TaskListener listener : listeners)
                                        {
                                            listener.taskDeleted(taskID2);
                                        }
                                    case TASK_CHANGED:

                                }
                            } catch (Exception ex) {
                            }
                        }
                    }

                }.start();*/
            }
            catch (IOException e)
            {
                e.printStackTrace();
                closeAll();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addTaskListener(TaskListener taskListener)
    {
        listeners.add(taskListener);
    }

    public void closeAll() throws IOException
    {
        if (ois != null) ois.close();
        if (oos != null) oos.close();
        if (socket != null) socket.close();
    }

    public Collection<Task> getRootTasks() throws IOException
    {
        Collection<Task> result = null;
        Message request = new Message(MessageType.GET_ROOT_TASKS, null);

        //ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(request);
        oos.flush();
        oos.reset();

        //ObjectInputStream oin = new ObjectInputStream(is);
        try
        {
            result = (Collection<Task>) ois.readObject();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public Collection<Task> getSubTasks(Integer parentTaskID) throws IOException
    {
        Collection<Task> result = null;
        ArrayList params = new ArrayList<String>();
        params.add(parentTaskID.toString());
        Message request = new Message(MessageType.GET_SUBTASKS, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();

        try
        {
            result = (Collection<Task>) ois.readObject();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public Task getTask(Integer taskID) throws IOException
    {
        Task result = null;
        ArrayList params = new ArrayList<String>();
        params.add(taskID.toString());
        Message request = new Message(MessageType.GET_TASK, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();

        try
        {
            result = (Task) ois.readObject();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public void lockTask(Integer taskID) throws IOException, TaskTrackerLogicException
    {
        ArrayList params = new ArrayList<String>();
        params.add(taskID.toString());
        Message request = new Message(MessageType.LOCK_TASK, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();

        TaskTrackerLogicException result = null;
        try
        {
            result = (TaskTrackerLogicException) ois.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (result != null)
        {
            throw result;
        }
    }

    public void unLockTask(Integer taskID) throws IOException
    {
        ArrayList params = new ArrayList<String>();
        params.add(taskID.toString());
        Message request = new Message(MessageType.UNLOCK_TASK, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();
    }

    public Integer addTask(String name, Integer parentID) throws IOException, TaskTrackerLogicException
    {
        ArrayList params = new ArrayList<String>();
        params.add(name);
        params.add(parentID == null ? "" : parentID.toString());
        Message request = new Message(MessageType.ADD_TASK, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();

        Integer result = null;
        try
        {
            result = (Integer) ois.readObject();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public void taskChanged(Task task) throws IOException
    {
        ArrayList params = new ArrayList<String>();
        params.add(task.getTaskID() ==  null ? "" : task.getTaskID().toString());
        params.add(task.getName() ==  null ? "" : task.getName());
        params.add(task.getParentID() ==  null ? "" : task.getParentID().toString());
        params.add(task.getStatus() ==  null ? "" : task.getStatus().toString());
        params.add(task.getPriority() ==  null ? "" : task.getPriority().toString());
        params.add(task.getStartDate() ==  null ? "" : task.getStartDate().toString());
        params.add(task.getEndDate() ==  null ? "" : task.getEndDate().toString());
        params.add(task.getDescription() ==  null ? "" : task.getDescription());
        Message request = new Message(MessageType.CHANGE_TASK, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();
    }

    public User getUser(String selectedName) throws IOException
    {
        User result = null;
        ArrayList params = new ArrayList<String>();
        params.add(selectedName);
        Message request = new Message(MessageType.GET_USER, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();

        try
        {
            result = (User) ois.readObject();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public Collection<User> getAllUsers() throws IOException
    {
        Collection<User> result = null;
        Message request = new Message(MessageType.GET_USERS, null);

        oos.writeObject(request);
        oos.flush();
        oos.reset();

        try
        {
            result = (Collection<User>) ois.readObject();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public void deleteTask(Integer taskID) throws TaskTrackerLogicException, IOException
    {
        ArrayList params = new ArrayList<String>();
        params.add(taskID.toString());
        Message request = new Message(MessageType.DELETE_TASK, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();
    }

    public void addUser(User user) throws TaskTrackerLogicException, IOException
    {
        ArrayList params = new ArrayList<String>();
        params.add(user.getName());
        params.add(user.getGrants());
        Message request = new Message(MessageType.ADD_USER, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();
    }

    public void deleteUser(String name) throws TaskTrackerLogicException, IOException
    {
        ArrayList params = new ArrayList<String>();
        params.add(name);
        Message request = new Message(MessageType.DELETE_USER, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();
    }

    public void changeUserName(String oldName, String newName) throws TaskTrackerLogicException, IOException
    {
        ArrayList params = new ArrayList<String>();
        params.add(oldName);
        params.add(newName);
        Message request = new Message(MessageType.CHANGE_USER_NAME, params);

        oos.writeObject(request);
        oos.flush();
        oos.reset();
    }
}

