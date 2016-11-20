package se.lohnn.canieatthis.product.temp

import se.lohnn.canieatthis.product.Product
import java.security.SecureRandom

class ProductFactory {


    companion object {
        val random = SecureRandom()
        val imageList = listOf("https://cdn.pixabay.com/photo/2016/03/05/22/31/appetite-1239303_960_720.jpg",
                "https://cdn.pixabay.com/photo/2010/12/13/09/59/appetite-2039_640.jpg",
                "https://cdn.pixabay.com/photo/2013/04/07/21/30/croissant-101636_640.jpg",
                "https://cdn.pixabay.com/photo/2014/07/09/22/09/farmers-bread-388647_640.jpg",
                "https://cdn.pixabay.com/photo/2016/03/09/22/50/food-1247612_640.jpg")
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
