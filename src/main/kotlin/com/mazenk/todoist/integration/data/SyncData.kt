package com.mazenk.todoist.integration.data

class SyncData (
    val user: TodoistUser?
)

data class TodoistUser (
    val userId: Int
)