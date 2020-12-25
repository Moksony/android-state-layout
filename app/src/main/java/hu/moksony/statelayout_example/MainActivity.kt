package hu.moksony.statelayout_example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import hu.moksony.statelayout.StateLayout
import hu.moksony.statelayout.state
import hu.moksony.statelayout_example.databinding.ActivityMainBinding
import hu.moksony.statelayout_example.databinding.StateNetworkErrorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var stateLayout: StateLayout

    val downloadProgress = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.ctl = this

        stateLayout = findViewById(R.id.stateLayout)
        stateLayout.setState(R.id.state_progress)

        val networkErrorState = state {
            val networkErrorBinding =
                StateNetworkErrorBinding.inflate(layoutInflater, null, false)
            networkErrorBinding.ctl = this
            networkErrorBinding.lifecycleOwner = this
            networkErrorBinding.root
        }
        stateLayout.addState(networkErrorState, R.id.state_network_error)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            stateLayout.setState(R.id.state_network_error)
        }

        findViewById<Button>(R.id.btn_test2).setOnClickListener {
            startActivity(Intent(this,TestActivity2::class.java))
        }
    }

    fun retry() {
        stateLayout.setState(R.id.state_progress)
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0..100) {
                downloadProgress.postValue(i)
                delay(100)
            }
            stateLayout.setState(R.id.state_content)
        }
    }
}