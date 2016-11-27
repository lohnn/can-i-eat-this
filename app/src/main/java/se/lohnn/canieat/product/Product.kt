package se.lohnn.canieat.product

import java.io.Serializable

class Product() : Serializable {
    lateinit var name: String
    lateinit var description: String
    lateinit var imageUUID: String

    constructor(name: String, description: String, imageUUID: String) : this() {
        this.name = name
        this.description = description
        this.imageUUID = imageUUID
    }
}