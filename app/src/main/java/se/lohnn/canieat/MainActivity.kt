package se.lohnn.canieat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.jetbrains.anko.intentFor
import se.lohnn.canieat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var fab: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.clickListener = this
        fab = binding.scan
    }

    fun onClick() {
//        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, fab, "transition_fab")

        val intent = intentFor<ScanActivity>()
        startActivity(intent)//, options.toBundle())
    }
}
