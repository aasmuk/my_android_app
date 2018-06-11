package com.azura.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Switch
import com.azura.echo.R

class SettingsFragment : Fragment() {

    var shakeSwitch:Switch?=null
    var myActivity: Activity?=null
    object Statified
    {
        var MY_PREFS_NAME="Shake Feature"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view= inflater.inflate(R.layout.fragment_settings, container, false)
        activity?.title="Settings"
        shakeSwitch=view?.findViewById(R.id.switchShake)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs=myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
        val isAllowed=prefs?.getBoolean("feature",false)
        if(isAllowed as Boolean)
        {
            shakeSwitch?.isChecked=true
        }
        else
        {
            shakeSwitch?.isChecked=false
        }
        shakeSwitch?.setOnCheckedChangeListener({
            compoundButton,b->
            if(b)
            {
                val editor=myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()
            }
            else
            {
                val editor=myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_sort)
        item?.isVisible = true
    }





}