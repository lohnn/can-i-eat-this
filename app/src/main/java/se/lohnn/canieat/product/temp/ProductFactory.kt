package se.lohnn.canieat.product.temp

import se.lohnn.canieat.product.Product
import java.security.SecureRandom

class ProductFactory {


    companion object {
        val random = SecureRandom()
        val imageList = listOf("forest-1818690_1280")
        val descriptionList = listOf("A short description of my product",
                "The only way to describe this is with words, please do.",
                "I'm just writing some descriptions here now.",
                "Do we really need this? Yes we do!")
        val productNames = listOf("Oatly chocolate drink",
                "Peanut butter",
                "I \u2665 eco 6p eggs")

        fun getRandomizedProduct(): Product {
            return Product(productNames.getRandom(),
                    descriptionList.getRandom(),
                    imageList.getRandom())
        }
    }
}

private fun <T> List<T>.getRandom(): T {
    return this[ProductFactory.random.nextInt(this.size)]
}
