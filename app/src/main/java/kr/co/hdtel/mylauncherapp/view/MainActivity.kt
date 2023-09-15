package kr.co.hdtel.mylauncherapp.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.hdtel.mylauncherapp.R
import kr.co.hdtel.mylauncherapp.data.DataInfo
import kr.co.hdtel.mylauncherapp.databinding.ActivityMainBinding
import kr.co.hdtel.mylauncherapp.util.SpanSize

class MainActivity : AppCompatActivity(), MyAdapter.OnAdapterListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpView()
        setUpBinding()
        setUpObserver()
        viewModel.setInitData()
    }

    private fun setUpView() {}

    private fun setUpBinding() {
        binding = DataBindingUtil.setContentView(this@MainActivity,R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
            activity = this@MainActivity
            favoriteSpanSize = SpanSize(binding.topRv)
            spanSize = SpanSize(binding.itemRv)
            onAdapterListener = this@MainActivity
        }
    }

    private fun setUpObserver() {

    }

    override fun addOnViewModel(widgetItemInfo: DataInfo) {
        //
    }

    override fun removeOnViewModel(widgetItemInfo: DataInfo) {
        //
    }

    override fun swapOnViewModel(list: List<DataInfo>) {
        viewModel.onItemSwap(list)
    }

    override fun setOnViewModel(
        targetList: List<DataInfo>,
        originItem: DataInfo,
        from: Int,
        to: Int
    ) {
        Log.d("sss","setOnViewModel...")
        viewModel.onItemSet(targetList, originItem, from, to)
    }

    override fun errorOnViewModel() {
        //
    }
}