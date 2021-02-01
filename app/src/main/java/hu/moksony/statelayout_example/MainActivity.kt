package hu.moksony.statelayout_example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import hu.moksony.statelayout.StateLayout
import hu.moksony.statelayout.states.*
import hu.moksony.statelayout_example.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var stateLayout: StateLayout

    val downloadProgress = MutableLiveData<Int>()
    val stateObserver = MutableLiveData<State>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.ctl = this

        stateLayout = findViewById(R.id.stateLayout)
        stateLayout.showAnimation = AnimationUtils.loadAnimation(this, R.anim.state_show_fade_scale)

        stateObserver.observe(this) {
            Log.d(TAG, "onCreate: ${it.javaClass.canonicalName}")
            stateLayout.setState(it)
        }


        CoroutineScope(Dispatchers.Main).launch {
            val loadingState = LoadingState
                .Builder()
                .setLayoutId(R.layout.state_loading)
                .build()
            stateObserver.postValue(loadingState)
            delay(2000)
            stateObserver.postValue(ErrorState
                .Builder()
                .setRetryCallback() {
                    retry()
                }
                .setLayoutId(R.layout.state_error)
                .setMessage(R.string.network_error)
                .setDrawable(R.drawable.ic_launcher_background)
                .build())
        }

        findViewById<Button>(R.id.btn_test2).setOnClickListener {
            startActivity(Intent(this, TestActivity2::class.java))
        }
    }

    fun retry() {
        CoroutineScope(Dispatchers.Main).launch {
            val loadingState = LoadingState
                .Builder()
                .setLayoutId(R.layout.state_loading)
                .build() as LoadingState

            stateObserver.postValue(loadingState)
            delay(2000)
            stateObserver.postValue(State.ContentState)
        }
    }
}