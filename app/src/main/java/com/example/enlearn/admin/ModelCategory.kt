package com.example.enlearn.admin

/**
 * Created by Siva G Gurusamy on 10/Apr/2022
 * email : siva@paxel.co
 */
class ModelCategory {
    var id: Long = 0
    var userType: String = ""
    var exam_category: String = ""
    var time_stamp: Long = 0
    var uid: String = ""
    var users_device_UUID: String = ""

    constructor()

    constructor(id: Long, examCategory: String, timestamp: Long, uid: String, uuid: String, userType: String) {
        this.id = id
        this.userType = userType
        this.exam_category = examCategory
        this.time_stamp = timestamp
        this.uid = uid
        this.users_device_UUID = uuid
    }
}