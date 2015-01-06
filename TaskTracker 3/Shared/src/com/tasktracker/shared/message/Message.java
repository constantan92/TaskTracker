package com.tasktracker.shared.message;


import java.io.Serializable;
import java.util.List;

public class Message implements Serializable
{
    private MessageType type;
    private List<String> parameters;

    public Message(MessageType type, List<String> parameters)
    {
        this.type = type;
        this.parameters = parameters;
    }

    public MessageType getType()
    {
        return type;
    }

    public  List<String> getParameters()
    {
        return parameters;
    }
}
