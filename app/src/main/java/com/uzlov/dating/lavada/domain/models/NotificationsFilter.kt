package com.uzlov.dating.lavada.domain.models

class NotificationsFilter(
    var messages: Boolean = true,
    var matches: Boolean = true,
    var likes: Boolean = true,
    var watchingVideo: Boolean = true,
    var gifts: Boolean = true,
    var news: Boolean = true
)

