package ro.dragossusi

data class TargetParameter(
    val pack: String,
    val name: String,
    val tag: String?
) {

    val className: String
        get() = "$pack.$name"

}