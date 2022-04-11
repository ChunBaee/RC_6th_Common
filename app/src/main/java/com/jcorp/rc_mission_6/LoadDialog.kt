package com.jcorp.rc_mission_6

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jcorp.rc_mission_6.databinding.DialogLoadedBinding

class LoadDialog : DialogFragment(), LoadAdapter.ClickListener, LoadAdapter.LongClickListener {
    private lateinit var binding : DialogLoadedBinding
    private val viewModel : mViewModel by viewModels({requireActivity()})

    private val loadAdapter = LoadAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogLoadedBinding.inflate(inflater, container, false)

        setRv()

        return binding.root
    }

    private fun setRv() {
        binding.loadRv.adapter = loadAdapter

        loadAdapter.setItem(viewModel.loadDataList.value!!)

        loadAdapter.clickListener(this)
        loadAdapter.longClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        context?.dialogResize(this@LoadDialog, 0.9f, 0.75f)
    }

    fun Context.dialogResize(dialogFragment: LoadDialog, width: Float, height: Float) {
        val windowMananger = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowMananger.defaultDisplay
            val size = Point()
            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowMananger.currentWindowMetrics.bounds
            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    override fun onClick(view: View, position: Int) {
        val item = viewModel.loadDataList.value!![position]
        viewModel.mBossHealth.postValue(item.bossHealth.toInt())
        viewModel.mPlayerHealth.postValue(item.playerHealth.toInt())
        viewModel._currentMoney.postValue(item.currentMoney.toInt())
        viewModel._requireMoney.postValue(item.requireMoney.toInt())
        dismiss()
    }

    override fun onLongClick(view: View, position: Int) {
        val item = viewModel.loadDataList.value!![position]
        viewModel.deleteLoad.postValue(item.saveTime)
        viewModel.mLoadDataList.removeAt(position)
        viewModel.syncLoad()
        loadAdapter.notifyDataSetChanged()
    }


}