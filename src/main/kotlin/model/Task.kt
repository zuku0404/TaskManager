package model

data class Task(val id: Long, var title: String, var description: String = "", var user: User?, var board: Board)