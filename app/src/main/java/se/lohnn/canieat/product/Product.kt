package se.lohnn.canieat.product

class Product() {
    lateinit var name: String
    lateinit var description: String
    lateinit var imageURL: String

    constructor(name: String, description: String, imageURL: String) : this() {
        this.name = name
        this.description = description
        this.imageURL = imageURL
    }
}