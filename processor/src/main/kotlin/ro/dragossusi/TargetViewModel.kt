package ro.dragossusi

import java.lang.StringBuilder

data class TargetViewModel(
    val pack: String,
    val name: String,
    val parameters: List<TargetParameter>
) {
    val className: String
        get() = "$pack.$name"

    fun toConstructorInvocation(): String {
        val builder = StringBuilder("$name(")
        parameters.forEach {
            builder.append("instance(")
            if (!it.tag.isNullOrEmpty()) {
                builder.append('"')
                builder.append(it.tag)
                builder.append('"')
            }
            builder.append("),")
        }
        builder.append(')')
        return builder.toString()
    }

}