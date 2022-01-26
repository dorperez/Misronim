package com.dapps.misronim.model

class Message {

    var sender : String? = ""
    var message : String? = ""
    var receiver : String? = ""
    var seen : Boolean? = false
    var url : String? = ""
    var messageId : String? = ""


    constructor()

    constructor(
        sender: String?,
        message: String?,
        receiver: String?,
        seen: Boolean?,
        url: String?,
        messageId: String?
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.seen = seen
        this.url = url
        this.messageId = messageId
    }


}