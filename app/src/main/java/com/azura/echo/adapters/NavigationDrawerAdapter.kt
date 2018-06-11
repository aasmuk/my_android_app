package com.azura.echo.adapters

import android.content.Context
import android.provider.MediaStore
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.azura.echo.R
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.azura.echo.activities.MainActivity
import com.azura.echo.fragments.AboutUsFragment
import com.azura.echo.fragments.FavouriteFragment
import com.azura.echo.fragments.MainScreenFragment
import com.azura.echo.fragments.SettingsFragment
import kotlinx.android.synthetic.main.app_bar_main.view.*

class NavigationDrawerAdapter(_content:ArrayList<String>,_Images:IntArray,_context:Context)
    :RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>()
{


    var content: ArrayList<String>? = null
    var Images: IntArray? = null
    var mContext: Context? = null

    init
     {
        this.content = _content
        this.Images = _Images
        this.mContext = _context
    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    {
        var img: ImageView? = null
        var text: TextView? = null
        var contentHolder: RelativeLayout? = null

        init
        {
            img = itemView?.findViewById(R.id.icon_navdrawer)
            text = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        var itemView = LayoutInflater.from(parent?.context)
                             .inflate(R.layout.row_custom_navigationdrawer, parent, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }



    override fun onBindViewHolder(holder: NavViewHolder, position: Int)
    {
        holder.img?.setBackgroundResource(Images?.get(position) as Int)
        holder.text?.setText(content?.get(position))
        holder.contentHolder?.setOnClickListener(
                {
                    if (position == 0) {
                        val mainScreenFragment = MainScreenFragment()
                        (mContext as MainActivity).supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment, mainScreenFragment)
                                .commit()
                    } else if (position == 1) {
                        val favouriteFragment = FavouriteFragment()
                        (mContext as MainActivity).supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment, favouriteFragment)
                                .commit()
                    } else if (position == 2) {
                        val settingsFragment = SettingsFragment()
                        (mContext as MainActivity).supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment, settingsFragment)
                                .commit()
                    } else {
                        val aboutUsFragment = AboutUsFragment()
                        (mContext as MainActivity).supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment, aboutUsFragment)
                                .commit()
                    }
                    MainActivity.Statified.drawerLayout?.closeDrawers()
                })
    }



    override fun getItemCount(): Int {
        return content?.size as Int
    }

}