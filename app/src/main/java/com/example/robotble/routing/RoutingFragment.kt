package com.example.robotble.routing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.robotble.R
import com.example.robotble.searchdevices.SearchBluetoothDevicesFragment
import com.example.robotble.searchdevices.SearchBluetoothLowEnergyDevicesFragment
import kotlinx.android.synthetic.main.fragment_routing.*

class RoutingFragment : Fragment(R.layout.fragment_routing) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bluetooth.setOnClickListener { navigateTo(SearchBluetoothDevicesFragment.newInstance()) }
    bluetoothLE.setOnClickListener { navigateTo(SearchBluetoothLowEnergyDevicesFragment.newInstance()) }
  }

  private fun navigateTo(fragment: Fragment) {
    activity?.supportFragmentManager
      ?.beginTransaction()
      ?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
      ?.replace(R.id.container, fragment)
      ?.addToBackStack(null)
      ?.commitAllowingStateLoss()
  }

  companion object {
    fun newInstance() = RoutingFragment()
  }
}