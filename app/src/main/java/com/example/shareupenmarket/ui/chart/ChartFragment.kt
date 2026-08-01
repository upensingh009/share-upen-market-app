package com.example.shareupenmarket.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shareupenmarket.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        val chart = view.findViewById<LineChart>(R.id.lineChart)

        // Placeholder data — replace with MarketDataService.fetchHistorical
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

        return view
    }
}
