package com.example.sb_stores.fragments.ui.components

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.core.graphics.blue
import com.dynocodes.graphosable.BarData
import com.dynocodes.graphosable.Graphs
import com.dynocodes.graphosable.Slice
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R

class Graphs {




    fun setPieChartView(sliceList: ArrayList<Slice>, view: ComposeView, context: Context) {
        (context as MainActivity).runOnUiThread {
            val composeView = view
            composeView.apply {

                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {

                    val sortedList = sliceList.sortedByDescending { it.value }

                    // Create a list that includes the top 6 elements and "Others" if there are more than 6 elements
                    val pieChartData = if (sortedList.size > 6) {
                        val top6 = sortedList.subList(0, 6)
                        val othersValue = sortedList.subList(6, sortedList.size).sumBy { it.value }
                        val othersSlice = Slice(othersValue, "Others")
                        top6 + othersSlice
                    } else {
                        sortedList
                    }

                    val graphs = Graphs()
                    val textColor = calculateTextColor()
                    graphs.PieChartWithLabels(
                        data = pieChartData,
                        context = context,
                        ringSize = 50f,
                        textColor = textColor.hashCode()
                    )
                }
            }
        }
    }


    fun setBarChartView(BarDataList: ArrayList<BarData>, view: ComposeView, context: Context) {
        (context as MainActivity).runOnUiThread {
            val composeView = view
            composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    val graphs = Graphs()
                    val textColor = calculateTextColor()
                    graphs.BarChart(
                        BarDataList,
                        Modifier,
                        colorResource(id = R.color.colorAccent),

                        textColor = textColor.hashCode()
                    )
                }
            }
        }
    }

    @Composable
    private fun calculateTextColor(): Color {
        val isDarkTheme = isSystemInDarkTheme()
//        return if (isDarkTheme) {
//            Color.White // Dark mode text color
//        } else {
//            Color.Black // Light mode text color
//        }
        return Color.Black
    }


}