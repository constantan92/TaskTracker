package com.tasktracker.server;

import com.tasktracker.server.controller.ServerController;
import com.tasktracker.server.controller.TaskTrackerLogicException;
import com.tasktracker.shared.message.Message;
import com.tasktracker.shared.model.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

public class ClientThread extends Thread
{
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    //private InputStream is;
    //private OutputStream os;
    private boolean close;

    public ClientThread(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void sendNotify(Message message)
    {
        try
        {
            oos.writeObject(message);
            oos.flush();
            oos.reset();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        System.out.println("Start Client Thread");
        while (ThreadPool.isRun()) {
            try
            {
                socket = serverSocket.accept();

                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("Client accepted");

                close = false;
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
                continue;
            }
            while (!close)
            {
                Message request = null;
                try
                {
                    request = (Message) ois.readObject();
                }
                catch (Exception e)
                {
                    //System.out.println(e.getMessage());
                    continue;
                }

                if  (request != null)
                {
                    try
                    {
                        switch (request.getType())
                        {
                            case GET_ROOT_TASKS:
                                    ServerController.responseRootTasks(oos); break;
                            case GET_SUBTASKS:
                                Integer parentId = Integer.valueOf(request.getParameters().iterator().next());
                                ServerController.responseSubTasks(parentId, oos); break;
                            case ADD_TASK:
                                Iterator<String> it = request.getParameters().iterator();
                                ServerController.responseAddTask(request, oos); break;
                            case DELETE_TASK:
                                Integer taskID = Integer.valueOf(request.getParameters().iterator().next());
                                ServerController.responseDeleteTask(taskID); break;
                            case LOCK_TASK:
                                ServerController.responseLockTask(Integer.valueOf(request.getParameters().iterator().next())); break;
                            case UNLOCK_TASK:
                                ServerController.responseUnLockTask(Integer.valueOf(request.getParameters().iterator().next())); break;
                            case CHANGE_TASK:
                                ServerController.responseChangeTask(request); break;
                            case GET_USERS:
                                ServerController.responseGetAllUsers(oos); break;
                            case GET_USER:
                                ServerController.responseGetUser(request.getParameters().iterator().next(), oos); break;
                            case ADD_USER:
                                ServerController.responseAddUser(request.getParameters().iterator().next()); break;
                            case DELETE_USER:
                                ServerController.responseDeleteUser(request.getParameters().iterator().next()); break;
                            case CHANGE_USER_NAME:
                                ServerController.responseChangeUserName(request); break;
                            case CLOSE_CONNECTION: close = true; break;
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (TaskTrackerLogicException e)
                    {
                        try
                        {
                            oos.writeObject(e);
                            oos.flush();
                            oos.reset();
                        } catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
                else
                {
                    continue;
                }

            }
            try {
                //is.close();
                //os.close();
                ois.close();
                oos.close();
                socket.close();
                socket = null;
            } catch (Exception ex) {
            }
        }
    }
}
