package ro.dragossusi.kodein

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DIViewModel(val scope: String = "")
