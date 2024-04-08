package domain.manager

import model.ActionResult
import model.User

interface IAccountManager {
    fun signIn(login: String, password: String): ActionResult
    fun signOut(): ActionResult
    fun signUp(login: String, password: String, firstName: String, lastName: String): ActionResult
    fun unregister() : ActionResult
    fun editUserData(firstName: String?, lastName: String?) : ActionResult
    fun editPassword(oldPassword: String, newPassword: String) : ActionResult
    fun getUser(userId : Long) : User?
    fun getAllUsers(): List<User>
}