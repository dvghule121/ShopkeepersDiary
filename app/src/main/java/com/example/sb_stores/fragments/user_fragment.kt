package com.example.sb_stores.fragments


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.sb_stores.MainActivity
import com.example.sb_stores.R
import com.example.sb_stores.database.DBFileProvider


import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [user_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class user_fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_user_fragment, container, false)



        view.findViewById<Button>(R.id.btnBackup).setOnClickListener {
            val uri = DBFileProvider().backupDatabase(activity as AppCompatActivity)
            Toast.makeText(requireContext(), "$uri", Toast.LENGTH_SHORT).show()

            val DBUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName.toString() + ".provider",
                File(uri)
            )
//            val myIntent = Intent(Intent.ACTION_VIEW)
//            myIntent.data = DBUri
////            val j = Intent.createChooser(myIntent, "Choose an application to open with:")
//            startActivity(myIntent)

//            val sendIntent = Intent()
//            sendIntent.action = Intent.ACTION_SEND
//            sendIntent.putExtra(Intent.EXTRA_FROM_STORAGE, "$uri")
//            sendIntent.type = "text/plain"
//            startActivity(sendIntent)




            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/.db"
            share.putExtra(Intent.EXTRA_STREAM, DBUri)
//            share.setPackage("com.whatsapp")

            requireActivity().startActivity(share)
        }
        view.findViewById<Button>(R.id.btnImport).setOnClickListener{
            val act = activity as MainActivity
            act.getfile()
        }


//        val pieChartView = view.findViewById<PieChartViewLines>(R.id.pie_chart)
//        val slices = listOf(
//            PieChartViewLines.Slice("Label 1", Color.RED, 30f),
//            PieChartViewLines.Slice("Label 2", Color.BLUE, 40f),
//            PieChartViewLines.Slice("Label 3", Color.GREEN, 50f)
//        )
//        pieChartView.setSlices(slices)

        return view
    }




}