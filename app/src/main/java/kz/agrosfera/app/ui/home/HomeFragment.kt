package kz.agrosfera.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kz.agrosfera.app.AgroApp
import kz.agrosfera.app.MainActivity
import kz.agrosfera.app.R
import kz.agrosfera.app.databinding.FragmentHomeBinding
import kz.agrosfera.app.domain.plant.GardenPlantCatalog
import kz.agrosfera.app.domain.weather.KazakhstanCityCatalog
import kz.agrosfera.app.domain.weather.WeatherInfo
import kz.agrosfera.app.ui.plants.PlantChipAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMyPlants.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerMyPlants.adapter = PlantChipAdapter(GardenPlantCatalog.plants)

        binding.btnCheckPlant.setOnClickListener {
            (requireActivity() as MainActivity).selectTab(R.id.nav_check)
        }

        binding.btnSelectCity.setOnClickListener { showCityPicker() }
        binding.cardWeather.setOnClickListener { showCityPicker() }

        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.authRepository.session.collect { session ->
                    binding.textGreeting.text = if (session != null) {
                        getString(R.string.home_greeting_name, session.name)
                    } else {
                        getString(R.string.home_greeting_guest)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.weatherRepository.selectedCityId.collect { cityId ->
                    val city = KazakhstanCityCatalog.byId(cityId)
                        ?: KazakhstanCityCatalog.defaultCity()
                    binding.textWeatherCity.text =
                        getString(R.string.weather_format_city, city.name)
                    loadWeather(cityId)
                }
            }
        }

        showLastDiagnosis()
    }

    override fun onResume() {
        super.onResume()
        showLastDiagnosis()
        refreshWeatherIfPossible()
    }

    private fun refreshWeatherIfPossible() {
        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            val cityId = app.weatherRepository.getSelectedCityId()
            loadWeather(cityId)
        }
    }

    private fun loadWeather(cityId: String) {
        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressWeather.isVisible = true
            binding.textWeatherDetails.text = getString(R.string.weather_loading)
            val result = app.weatherRepository.fetchCurrentWeather(cityId)
            binding.progressWeather.isVisible = false
            result.fold(
                onSuccess = { info -> renderWeather(info) },
                onFailure = {
                    binding.textWeatherDetails.text = getString(R.string.weather_error)
                    binding.textWeatherTemp.text = "--"
                },
            )
        }
    }

    private fun renderWeather(info: WeatherInfo) {
        binding.textWeatherCity.text = getString(R.string.weather_format_city, info.cityName)
        binding.textWeatherTemp.text = getString(R.string.weather_format_temp, info.temperatureC)
        binding.textWeatherDetails.text = getString(
            R.string.weather_format_details,
            info.conditionLabel,
            info.humidityPercent,
            info.windSpeedMs,
        )
        binding.textWeatherEmoji.text = info.conditionEmoji
    }

    private fun showCityPicker() {
        val cities = KazakhstanCityCatalog.cities
        val names = cities.map { it.name }.toTypedArray()
        val app = requireContext().applicationContext as AgroApp
        viewLifecycleOwner.lifecycleScope.launch {
            val currentId = app.weatherRepository.getSelectedCityId()
            val selectedIndex = cities.indexOfFirst { it.id == currentId }.coerceAtLeast(0)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.weather_select_city_title)
                .setSingleChoiceItems(names, selectedIndex) { dialog, which ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        app.weatherRepository.setSelectedCityId(cities[which].id)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    private fun showLastDiagnosis() {
        val app = requireContext().applicationContext as AgroApp
        val last = app.diagnosisHistoryStore.getLast()
        binding.cardLastDiagnosis.isVisible = last != null
        if (last == null) return
        binding.textLastDiagnosisName.text = last.displayName
        binding.textLastDiagnosisMeta.text = last.confidencePercent?.let {
            getString(R.string.diagnosis_confidence, it)
        } ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
