package com.tasktracker.app.test;

import com.tasktracker.app.model.User;

public class TestUserGrants
{
    public static void main(String[] args)
    {
        User user1 = new User("User 1", 0);
        User user2 = new User("User 2", 31);

        System.out.println(getGrantsInfo(user1));
        System.out.println(getGrantsInfo(user2));
        System.out.println("*******");

        user1.addGrant(User.Grant.TASK_MODIFY);
        user2.excludeGrant(User.Grant.TASK_MODIFY);

        System.out.println(getGrantsInfo(user1));
        System.out.println(getGrantsInfo(user2));
    }

    public static String getGrantsInfo(User user)
    {
        return user.getGrants()+": " + User.getGrantsByNumber(user.getGrants());
    }
}
