package com.example.enlearn.admin

/**
 * Created by Siva G Gurusamy on 11/Apr/2022
 * email : siva@paxel.co
 */
class ModelInbox {
    var id: Long = 0
    var user_name: String = ""
    var msg_title: String = ""
    var msg_desc: String = ""
    var msg_receiver: String = ""
    var attached_img_url: String = ""
    var time_stamp: Long = 0
    var uid: String = ""
    var users_device_UUID: String = ""
    var profile_image: String = ""
    var user_email: String = ""
    var full_name: String = ""
    var user_gender: String = ""
    var user_type: String = ""


    constructor()

    constructor(userName: String, id: Long, msgReceiver: String, timestamp: Long,
                uid: String, uuid: String, msgTitle: String, msgDesc: String, imgUrl: String,
                userEmail: String,profileImg: String,userGender: String,userType: String,fullName: String,) {
        this.id = id
        this.user_name = userName
        this.msg_title = msgTitle
        this.msg_desc = msgDesc
        this.msg_receiver = msgReceiver
        this.time_stamp = timestamp
        this.uid = uid
        this.attached_img_url = imgUrl
        this.users_device_UUID = uuid
        this.user_email = userEmail
        this.full_name = fullName
        this.profile_image = profileImg
        this.user_gender = userGender
        this.user_type = userType
    }
}