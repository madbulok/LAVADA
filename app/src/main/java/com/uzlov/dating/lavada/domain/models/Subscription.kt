package com.uzlov.dating.lavada.domain.models

import java.util.*

data class Subscription (
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var datetime: Long = 0,
    var subscriptionId: String = ""
)