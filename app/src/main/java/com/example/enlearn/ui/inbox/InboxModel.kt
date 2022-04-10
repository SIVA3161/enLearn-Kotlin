package com.example.enlearn.ui.inbox

/**
 * Created by Siva G Gurusamy on 05/Apr/2022
 * email : siva@paxel.co
 */
class InboxModel {

    var msgTitle:String = ""
    var msgDesc: String = ""
    var msgImgUrl: String = ""
    var msgReceivedTimeStamp: Long = 0

    constructor()

    constructor(msgTitle: String, msgDesc: String, msgImgUrl: String, msgReceivedTimeStamp: Long) {
        this.msgTitle = msgTitle
        this.msgDesc = msgDesc
        this.msgImgUrl = msgImgUrl
        this.msgReceivedTimeStamp = msgReceivedTimeStamp
    }
}