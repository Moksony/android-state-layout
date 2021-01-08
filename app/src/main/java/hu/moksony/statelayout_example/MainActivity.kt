package hu.moksony.statelayout_example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            stateLayout.setState(it)
        }


        CoroutineScope(Dispatchers.Main).launch {
            val loadingState = LoadingState
                .Builder()
                .setViewId(R.layout.state_loading)
                .build()
            stateObserver.postValue(loadingState)
            delay(2000)

            stateObserver.postValue(ErrorState
                .Builder()
                .setRetryCallback() {
                    retry()
                }
                .setViewId(R.layout.state_error)
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
            val loadingState = ProgressState
                .Builder()
                .setMax(100)
                .setProgressViewId(R.id.state_progress_view_progress)
                .setViewId(R.layout.state_progress)
                .build() as ProgressState
            stateObserver.postValue(loadingState)

            for (i in 1..100) {
                loadingState.progressAdvice()
                delay(100)
            }
            stateObserver.postValue(ContentState())
        }
    }
}