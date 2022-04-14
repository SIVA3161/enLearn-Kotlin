package com.example.enlearn.ui.account

/**
 * Created by Siva G Gurusamy on 14/Apr/2022
 * email : siva@paxel.co
 */
class ModelUserRepository {

    var user_name: String = ""
    var full_name: String = ""
    var user_email: String = ""
    var profile_image: String = ""
    var user_gender: String = ""
    var user_type: String = ""
    var user_device_uuid: String = ""
    var user_uid: String = ""
    var time_stamp: Long = 0

     constructor()

    constructor(userName: String, fullName: String, userEmail: String, userAvatar: String,
                userGender: String, userType: String, usersDeviceUUID: String, userUID: String,timestamp:Long){
        this.user_name = userName
        this.full_name = fullName
        this.user_email = userEmail
        this.profile_image = userAvatar
        this.user_gender = userGender
        this.user_type = userType
        this.user_device_uuid = usersDeviceUUID
        this.user_uid = userUID
        this.time_stamp = timestamp
    }


}