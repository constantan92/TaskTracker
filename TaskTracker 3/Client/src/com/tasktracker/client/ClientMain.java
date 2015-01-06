package com.tasktracker.client;

import com.tasktracker.client.controller.ClientController;
import com.tasktracker.client.view.MainFrame;

public class ClientMain
{
    public static void main(String[] args)
    {
        ClientController clientController = new ClientController();

        MainFrame mainView = new MainFrame(clientController);
        mainView.setVisible(true);
    }
}
