package se.lohnn.canieatthis

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import se.lohnn.canieatthis.databinding.ActivityMainBinding
import se.lohnn.canieatthis.temp.ScanActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.clickListener = this
    }

    fun onClick() {
        val scanIntent = Intent(this, ScanActivity::class.java)
        scanIntent.putExtra(ScanActivity.AutoFocus, true)
        scanIntent.putExtra(ScanActivity.UseFlash, false)
        startActivity(scanIntent)
    }
}
