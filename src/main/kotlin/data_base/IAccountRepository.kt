package data_base

import model.Account

interface IAccountRepository {
    fun createAccount(login: String, password: String): Boolean
    fun findAccountByLogin(login: String): Account?
    fun findAccountById(accountId: Long): Account?
    fun editAccount(account: Account): Boolean
    fun deleteAccountByLogin(login: String): Boolean
}