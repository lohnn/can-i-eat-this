package se.lohnn.canieat.dataservice

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import se.lohnn.canieat.product.Product
import se.lohnn.canieat.product.temp.ProductFactory
import java.io.File
import java.util.UUID

class DataService private constructor() {
    companion object {
        val instance: DataService by lazy {
            DataService()
        }

        //Image UUID, path to file
        private val imageMap = mutableMapOf<String, StorageReference>()

        fun getImageStorageRef(imagePath: String?): StorageReference? {
            if (imagePath == null) {
                return null
            }

            //TODO: Lazy
            return FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://can-i-eat-this-ca957.appspot.com")
                    .child("canieatthis")
                    .child("images")
                    .child(imagePath + ".jpg")
        }
    }

    val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    init {
        database.setPersistenceEnabled(false)
    }

    fun getProduct(barcodeValue: String, function: (Product) -> Unit) {
        val databasePoint = "products/$barcodeValue"
        databaseGet<Product>(databasePoint) { product ->
            if (product == null) {
                val randomProduct = ProductFactory.getRandomizedProduct()
                databaseSave(databasePoint, randomProduct)
                function.invoke(randomProduct)
            } else {
                function.invoke(product)
            }
        }
    }

    fun saveProduct(barcode: String, product: Product) {
        databaseSave("products/$barcode", product)
    }

    private fun databaseSave(databasePoint: String, obj: Any) {
        database.getReference(databasePoint).setValue(obj)
    }

    inline private fun <reified T> databaseGet(databasePoint: String, crossinline function: (T?) -> Unit) {
        database.getReference(databasePoint)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        function(dataSnapshot.getValue(object : GenericTypeIndicator<T>() {}))
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }
                })
    }
}