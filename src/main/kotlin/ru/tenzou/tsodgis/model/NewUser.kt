package ru.tenzou.tsodgis.model

import java.io.Serializable

class NewUser : Serializable {
    var username: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var password: String? = null

    constructor()

    constructor(username: String, firstName: String, lastName: String, email: String, password: String) {
        this.username = username
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.password = password
    }

    companion object {
        private const val serialVersionUID = -1764970284520387975L
    }

}