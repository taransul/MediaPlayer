package com.example.mediaplayer.presentation.ui.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.beraldo.playerlib.PlayerService
import com.beraldo.playerlib.PlayerService.Companion.STREAM_URL
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.RadioStation
import com.example.mediaplayer.data.model.RadioStationEntity
import com.example.mediaplayer.data.toRadioStationEntity
import com.example.mediaplayer.databinding.FragmentRadioBinding
import com.example.mediaplayer.presentation.recycler.radio.RadioAdapter
import com.example.mediaplayer.presentation.recycler.radio.RadioClickListener
import com.example.mediaplayer.presentation.recycler.radio.horizontal.HorizontalAdapter
import com.example.mediaplayer.presentation.recycler.radio.horizontal.RadioClickListenerHorizontal
import org.koin.android.viewmodel.ext.android.viewModel

class RadioFragment : Fragment(R.layout.fragment_radio) {
    private val binding: FragmentRadioBinding by viewBinding()
    private val viewModel: RadioViewModel by viewModel()
    private val adapterGrid by lazy { RadioAdapter(radioClickListener) }
    private val adapterHorizontal by lazy { HorizontalAdapter(radioClickListenerHorizontal) }
    private var intent: Intent? = null


    private fun initRecyclerGrid() {
        binding.recyclerConteinerRadio.adapter = adapterGrid
        binding.recyclerConteinerRadio.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private fun initRecyclerHorizontal() {
        binding.recyclerContainerRadioSave.adapter = adapterHorizontal
        binding.recyclerContainerRadioSave.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private val radioClickListener: RadioClickListener = object : RadioClickListener {
        override fun onItemClickListener(radioStation: RadioStation) {

            viewModel.insertRadioNumberOpening(radioStation.toRadioStationEntity())
            viewModel.getRadioNumberOpening()

            if (intent != null) {
                activity?.unbindService(connection)
            }
            intent = Intent(requireContext(), PlayerService::class.java)
            intent?.putExtra(STREAM_URL, radioStation.urlPath)
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        override fun onIconClickListener(position: Int) {
            viewModel.iconClickListenerViewModel(position)
        }
    }


    private val radioClickListenerHorizontal: RadioClickListenerHorizontal =
        object : RadioClickListenerHorizontal {
            override fun onItemClickListener(radioStation: RadioStationEntity) {

                if (intent != null) {
                    activity?.unbindService(connection)
                }
                intent = Intent(requireContext(), PlayerService::class.java)
                intent?.putExtra(STREAM_URL, radioStation.urlPath)
                activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }


    override fun onResume() {
        super.onResume()

        initRecyclerGrid()
        initRecyclerHorizontal()

        viewModel.radio.observe(viewLifecycleOwner) { radio ->
            adapterGrid.addRadio(radio)
        }

        viewModel.saveRadio.observe(viewLifecycleOwner) { saveRadio ->
            adapterHorizontal.addRadio(saveRadio)
        }


        binding.editTextTextPersonName.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {

                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    viewModel.editTextSearch(binding.editTextTextPersonName.text.toString())

                    if (binding.editTextTextPersonName.text.toString() == "") {
                        viewModel.getRadioStation()
                    }

                    binding.editTextTextPersonName.isCursorVisible = false

                    return true
                }
                return false
            }
        })

    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("my!!!", "ERROR")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is PlayerService.PlayerServiceBinder) {
                service.getPlayerHolderInstance()
            }
        }
    }
}