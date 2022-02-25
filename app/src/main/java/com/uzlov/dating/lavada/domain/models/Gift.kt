package com.uzlov.dating.lavada.domain.models

import java.util.*

class Gift(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var descriptions: String = "",
    var cost: Int = 0
)