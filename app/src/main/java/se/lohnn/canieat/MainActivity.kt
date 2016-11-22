package se.lohnn.canieat

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import se.lohnn.canieat.databinding.ActivityMainBinding
import se.lohnn.canieat.scan.ScanActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.clickListener = this

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        myRef.setValue("Heyoo!")
    }

    fun onClick() {
        val scanIntent = Intent(this, ScanActivity::class.java)
        scanIntent.putExtra(ScanActivity.AutoFocus, true)
        scanIntent.putExtra(ScanActivity.UseFlash, false)
        startActivity(scanIntent)
    }
}
