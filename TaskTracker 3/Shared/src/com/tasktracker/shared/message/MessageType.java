package com.tasktracker.shared.message;

import java.io.Serializable;

public enum MessageType implements Serializable
{
    CLOSE_CONNECTION,
    GET_ROOT_TASKS,
    GET_SUBTASKS,
    GET_TASK,
    GET_USERS,
    GET_USER,
    GET_TASK_BY_ID,
    ADD_TASK,
    DELETE_TASK,
    CHANGE_TASK,
    LOCK_TASK,
    UNLOCK_TASK,
    ADD_USER,
    DELETE_USER,
    CHANGE_USER_NAME,

    TASK_ADDED,
    TASK_DELETED,
    TASK_CHANGED ;
}
