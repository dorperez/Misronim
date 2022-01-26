package com.dapps.misronim.model

class User {

    var uid: String? = null
    var userEmail: String? = null
    var userName: String? = null
    var profilePic: String? = null
    var coverPic: String? = null
    var status: String? = null
    var search: String? = null
    var facebook: String? = null
    var instagram: String? = null
    var website: String? = null

    constructor()


    constructor(
        uid: String?,
        userEmail: String?,
        userName: String?,
        profilePic: String?,
        coverPic: String?,
        status: String?,
        search: String?,
        facebook: String?,
        instagram: String?,
        website: String?
    ) {
        this.uid = uid
        this.userEmail = userEmail
        this.profilePic = profilePic
        this.coverPic = coverPic
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
        this.userName = userName
    }


}