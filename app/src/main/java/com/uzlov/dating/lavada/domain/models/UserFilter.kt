package com.uzlov.dating.lavada.domain.models

class UserFilter(
    var sex: Int = -1,
    var ageStart: Int = 18,
    var ageEnd: Int = 60,
    var latitude: Float = 0F,
    var longitude: Float = 0F,
)