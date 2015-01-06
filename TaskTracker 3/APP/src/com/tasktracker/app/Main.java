package com.tasktracker.app;

import com.tasktracker.app.controller.TaskController;
import com.tasktracker.app.controller.TaskTrackerLogicException;
import com.tasktracker.app.model.ModelContainer;
import com.tasktracker.app.model.Task;
import com.tasktracker.app.model.User;
import com.tasktracker.app.view.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add( new Task(1,"First Task", null));
        tasks.add( new Task(11,"First SubTask 1", 1));
        tasks.add( new Task(12,"First SubTask 2", 1));
        tasks.add( new Task(111,"First SubSubTask", 11));


        tasks.add( new Task(2,"Second Task", null));
        tasks.add( new Task(21,"Second SubTask 1", 2));
        tasks.add( new Task(22,"Second SubTask 2", 2));
        tasks.add( new Task(221,"Second SubSubTask 221", 22));
        tasks.add( new Task(222,"Second SubSubTask 222", 22));

        ModelContainer taskContainer = ModelContainer.getInstance();

        try
        {
            for (Task task : tasks)
            {
                TaskController.addTask(task);
            }
            TaskController.addUser(new User("User 1", 0));
            TaskController.addUser(new User("User 2", 0));
            TaskController.addUser(new User("User 3", 0));
        }
        catch (TaskTrackerLogicException e)
        {
            e.printStackTrace();
        }





        MainFrame mainView = new MainFrame();
        mainView.setVisible(true);
    }
}
