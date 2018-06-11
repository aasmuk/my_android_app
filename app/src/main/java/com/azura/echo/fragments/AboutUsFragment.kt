package com.azura.echo.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.azura.echo.R
import com.azura.echo.Songs
import com.azura.echo.adapters.MainScreenAdapter
import java.util.*


class AboutUsFragment : Fragment()
{



    var myActivity: Activity?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        var view=inflater.inflate(R.layout.fragment_about_us, container, false)
        setHasOptionsMenu(true)
        activity?.title="About Us"

        return view

    }
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

    }
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_sort)
        if (item == null) {
        } else {
            item.isVisible = false
        }
    }


}

