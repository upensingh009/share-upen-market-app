package com.example.shareupenmarket.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shareupenmarket.R
import com.example.shareupenmarket.market.MarketDataService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class ChartFragment : Fragment() {
    private val marketDataService = MarketDataService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        val chart = view.findViewById<LineChart>(R.id.lineChart)

        // Load historical data asynchronously
        lifecycleScope.launch {
            val points = marketDataService.fetchHistorical("NIFTY", days = 30)
            if (points.isNotEmpty()) {
                val entries = points.mapIndexed { idx, (_, price) -> Entry(idx.toFloat(), price.toFloat()) }
                val set = LineDataSet(entries, "NIFTY")
                set.color = Color.BLUE
                set.setDrawValues(false)
                val data = LineData(set)
                chart.data = data
                chart.description.isEnabled = false
                chart.invalidate()
            } else {
                // Keep placeholder data if no historical data available
                val entries = listOf(
                    Entry(0f, 100f),
                    Entry(1f, 102f),
                    Entry(2f, 101f),
                    Entry(3f, 105f),
                    Entry(4f, 103f)
                )
                val set = LineDataSet(entries, "NIFTY")
                val data = LineData(set)
                chart.data = data
                chart.invalidate()
            }
        }

        return view
    }
}
