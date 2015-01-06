package com.tasktracker.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class User implements Serializable
{
    private String name;
    private Integer grants;

    public User(String name, Integer grants)
    {
        if (name == null)
            throw new IllegalArgumentException("User name can not be null!");
        if ((grants < 0) || grants > MAX_GRANTS_NUMBER)
            throw new IllegalArgumentException("Invalid number for grants: " + grants);
        this.name = name;
        this.grants = (grants == null ? Integer.valueOf(0) : grants);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getGrants()
    {
        return grants;
    }

    public boolean checkGrant(Grant grant)
    {
        int numb = getNumberByGrant(grant);

        return ((grants & numb) == numb);
    }

    public void addGrant(Grant grant)
    {
        grants |= getNumberByGrant(grant);
    }

    public void excludeGrant(Grant grant)
    {
        grants ^= getNumberByGrant(grant);
    }

    public static Collection<Grant> getGrantsByNumber(int number) throws IllegalArgumentException
    {
        if ((number < 0) || number > MAX_GRANTS_NUMBER)
            throw new IllegalArgumentException("Invalid number for grants: " + number);

        Collection<Grant> result = new ArrayList<Grant>();
        while (number > 0)
        {
            int nextBit = Integer.lowestOneBit(number);
            result.add(getGrantByNumber(nextBit));
            number -= nextBit;
        }

        return result;
    }

    public static Integer getNumberByGrants(Collection<Grant> grants)
    {
        Integer result = Integer.valueOf(0);
        for (Grant grant : grants)
        {
            result += getNumberByGrant(grant);
        }
        return result;
    }

    private static Grant getGrantByNumber(int number)
    {
        switch (number)
        {
            case 1: return Grant.REQUEST;
            case 2: return Grant.TASK_READ;
            case 4: return Grant.TASK_EXECUTE;
            case 8: return Grant.TASK_MODIFY;
            case 16: return Grant.TASK_CREATE;
            default: throw new IllegalArgumentException("Invalid number for grant: " + number);
        }
    }

    private static int getNumberByGrant(Grant grant)
    {
        switch (grant)
        {
            case REQUEST: return 1;
            case TASK_READ: return 2;
            case TASK_EXECUTE: return 4;
            case TASK_MODIFY: return 8;
            case TASK_CREATE: return 16;
            default: throw new IllegalArgumentException("Invalid grant: " + grant);
        }
    }

    private static int MAX_GRANTS_NUMBER = 31;

    public static enum Grant implements Serializable
    {
        REQUEST,      // 1
        TASK_READ,    // 2
        TASK_EXECUTE, // 4
        TASK_MODIFY,  // 8
        TASK_CREATE   // 16
    }
}

