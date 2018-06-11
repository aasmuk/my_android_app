package com.azura.echo.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.azura.echo.R
import com.azura.echo.Songs
import com.azura.echo.fragments.SongPlayingFragment

class FavoriteAdapter(_songDetails:ArrayList<Songs>, _context: Context): RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>()
{


    var songDetails: ArrayList<Songs> ? = null
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null

    init
    {
        this.songDetails = _songDetails
        this.mContext = _context
        this.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
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
            val args= Bundle()
            args.putString("path",songObject?.songData)
            args.putString("songTitle",songObject?.songTitle)
            args.putString("songArtist", songObject?.artist)
            args.putInt("SongId",songObject?.songId?.toInt() as Int)
            args.putInt("songPosition",position)
            args.putParcelableArrayList("songData",songDetails)
            songPlayingFragment.arguments=args
            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragmentFavorite")
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
        var contentHolder: RelativeLayout?=null

        init
        {
            trackTitle = view?.findViewById<TextView>(R.id.trackTitle)
            trackArtist = view?.findViewById<TextView>(R.id.trackArtist)
            contentHolder=view?.findViewById<RelativeLayout>(R.id.contentRow)
        }
    }
}