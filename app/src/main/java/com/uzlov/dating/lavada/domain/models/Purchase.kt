package com.uzlov.dating.lavada.domain.models

import java.util.*

data class Purchase(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var datetime: Long = 0,
    var productId: String = "",
)