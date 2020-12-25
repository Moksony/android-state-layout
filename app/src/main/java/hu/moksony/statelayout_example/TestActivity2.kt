package hu.moksony.statelayout_example

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import hu.moksony.statelayout.StateLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestActivity2 : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_2)

        val stateLayout = findViewById<StateLayout>(R.id.stateLayout)
        val recyclerView = findViewById<ListView>(R.id.state_content)
        val list = mutableListOf("Apple")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        recyclerView.adapter = adapter


        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            stateLayout.setState(R.id.state_loading)
            delay(5000)
            stateLayout.setState(R.id.state_network_error)
            delay(2000)
            stateLayout.setState(R.id.state_loading)
            delay(1500)
            adapter.addAll(listOf("Banana", "Orange"))
            stateLayout.setState(R.id.state_content)
        }
    }
}