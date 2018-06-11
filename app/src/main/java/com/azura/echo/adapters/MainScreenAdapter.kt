package com.azura.echo.adapters



import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.azura.echo.R
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.azura.echo.Songs
import com.azura.echo.fragments.SongPlayingFragment

import kotlinx.android.synthetic.main.app_bar_main.view.*

class MainScreenAdapter(_songDetails:ArrayList<Songs>,_context:Context):RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>()
{


    var songDetails: ArrayList<Songs> ? = null
    var mContext: Context? = null

    init
    {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)

    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val songObject=songDetails?.get(position)
        holder.trackTitle?.text=songObject?.songTitle
        holder.trackArtist?.text=songObject?.artist
        holder.contentHolder?.setOnClickListener({



            val songPlayingFragment = SongPlayingFragment()
            val args=Bundle()
            args.putString("path",songObject?.songData)
            args.putString("songTitle",songObject?.songTitle)
            args.putInt("SongId",songObject?.songId?.toInt() as Int)
            args.putInt("songPosition",position)
            args.putParcelableArrayList("songData",songDetails)
            songPlayingFragment.arguments=args
            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        })

    }
    override fun getItemCount(): Int
    {
        if(songDetails==null)
        {
            return 0
        }
        else
        {
            return (songDetails as ArrayList<Songs>).size
        }
    }




    class MyViewHolder(view: View?) : RecyclerView.ViewHolder(view)
    {

        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder:RelativeLayout?=null

        init
        {
            trackTitle = view?.findViewById<TextView>(R.id.trackTitle)
            trackArtist = view?.findViewById<TextView>(R.id.trackArtist)
            contentHolder=view?.findViewById<RelativeLayout>(R.id.contentRow)
        }
    }
}