package com.tasktracker.server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ThreadPool
{
    private static boolean run = false;
    private static List<ClientThread> clients = new ArrayList<ClientThread>();

    public static void serverStart(ServerSocket serverSocket, int connectionCount)
    {
        run = true;
        clients.clear();
        for (int i = 0; i < connectionCount; i++) {
            ClientThread thread = new ClientThread(serverSocket);
            thread.start();
            clients.add(thread);
        }
    }

    public static boolean isRun() {
        return run;
    }

    public static void serverStop()
    {
        run = false;
    }
}
