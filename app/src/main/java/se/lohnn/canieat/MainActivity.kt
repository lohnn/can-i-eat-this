package se.lohnn.canieat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import se.lohnn.canieat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.clickListener = this
    }

    fun onClick() {
        startActivity<ScanActivity>(
                ScanActivity.KEY_AUTO_FOCUS to true,
                ScanActivity.KEY_USE_FLASH to false)
    }
}
