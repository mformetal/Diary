package miles.diary.util

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author - mbpeele on 2/22/18.
 */
@PublishedApi
internal abstract class TypeReference<T> : Comparable<TypeReference<T>> {
    val type: Type
        get() =
            if (javaClass.genericSuperclass is ParameterizedType) {
                (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
            } else {
                javaClass.genericSuperclass
            }

    override fun compareTo(other: TypeReference<T>) = 0
}