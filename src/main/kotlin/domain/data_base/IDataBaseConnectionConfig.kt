package domain.data_base

interface IDataBaseConnectionConfig{
    val driver: String
    val url: String
    val user: String
    val password: String
}