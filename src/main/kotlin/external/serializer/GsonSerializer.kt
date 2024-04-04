package external.serializer

import com.google.gson.GsonBuilder
import domain.serializer.ISerializer

class GsonSerializer : ISerializer {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun <T> fromJson(element: String?, elementClass: Class<T>): T {
        return gson.fromJson(element, elementClass)
    }

    override fun toJson(element: Any?): String {
        return gson.toJson(element)
    }
}