package com.uzlov.dating.lavada.domain.models

import java.util.*

class CategoryGifts (
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var gifts: List<Gift> = emptyList()
)
