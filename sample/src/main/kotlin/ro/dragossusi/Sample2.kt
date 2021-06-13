package ro.dragossusi

import ro.dragossusi.kodein.DIInject
import ro.dragossusi.kodein.DITag
import ro.dragossusi.kodein.DIViewModel

@DIViewModel
class Sample2 @DIInject constructor(
    @DITag("ceva")
    ceva: Any
) {
}