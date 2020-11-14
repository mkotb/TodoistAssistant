package com.mazenk.todoist.integration.data

import com.google.gson.annotations.SerializedName

class TodoistAccessData (
    @SerializedName("access_token")
    val accessToken: String
)
