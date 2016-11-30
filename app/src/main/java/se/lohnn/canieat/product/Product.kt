package se.lohnn.canieat.product

import java.io.Serializable

class Product() : Serializable {
    lateinit var name: String
    lateinit var description: String
    var imagePath: String? = null

    constructor(name: String, description: String, imageUUID: String) : this() {
        this.name = name
        this.description = description
        this.imagePath = imageUUID
    }
}