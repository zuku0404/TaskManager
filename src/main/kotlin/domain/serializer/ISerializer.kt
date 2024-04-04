package domain.serializer

interface ISerializer {
    fun <T> fromJson(element:String?, elementClass: Class<T>): T?
    fun toJson(element: Any?): String
}