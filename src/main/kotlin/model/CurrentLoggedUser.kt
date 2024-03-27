package model

class CurrentLoggedUser private constructor(){
    private var user: User? = null

    companion object {
        @Volatile
        private var instance: CurrentLoggedUser? = null

        fun getInstance() =
            instance?: synchronized(this) {
                instance?: CurrentLoggedUser().also { instance = it }
        }
    }

    fun setUser(user: User?) {
        this.user = user
    }

    fun getUser(): User? {
        return user
    }
}