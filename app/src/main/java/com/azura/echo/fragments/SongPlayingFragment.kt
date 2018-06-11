package com.azura.echo.fragments


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.azura.echo.CurrentSongHelper
import com.azura.echo.R
import com.azura.echo.Songs
import java.util.*
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.Toast
import com.azura.echo.R.drawable.*
import com.azura.echo.databases.EchoDatabases
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView

class SongPlayingFragment: Fragment() {

    object Statified
    {
        var myActivity: Activity? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playpauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekbar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var currentSongHelper: CurrentSongHelper? = null
        var _audioVisualization:AudioVisualization?=null
        var glView:GLAudioVisualizationView?=null
        var fab:ImageButton?=null
        var favoriteContent: EchoDatabases?=null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var mediaPlayer: MediaPlayer? = null
        var mSensorManager:SensorManager?=null
        var mSensorListener:SensorEventListener?=null
        var MY_PREFS_NAME="Shake Feature"
        var updateSongTime = object : Runnable
        {
            override fun run()
            {
                val getcurrent = mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",

                        TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long ),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long ) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long ))))

                Handler().postDelayed(this, 1000)
            }
        }

    }



    object Staticated
    {
        var MY_PREFS_SHUFFLE="Shuffle feature"
        var MY_PREFS_LOOP="Loop feature"
        fun onSongComplete() {
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {
                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songId = nextSong?.songId as Long
                    Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_on))
            }
            else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_off))
            }
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition += 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition
            }
            if (Statified.currentPosition == Statified.fetchSongs?.size) {
                Statified.currentPosition = 0
            }
            Statified.currentSongHelper?.isLoop = false

            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songId as Long
            updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
            Statified.mediaPlayer?.reset()
            try {

                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_on))
            }
            else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_off))
            }


        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated=songTitle
            var songArtistUpdated=songArtist
            if(songTitle.equals("<unknown>",true))
            {
                songTitleUpdated="unknown"
            }
            if(songArtist.equals("<unknown>",true))
            {
                songArtistUpdated="unknown"
            }
            Statified.songTitleView?.setText(songTitle)
            Statified.songArtistView?.setText(songArtist)
        }

        fun processInformation(mediaplayer: MediaPlayer) {
            val finalTime = mediaplayer.duration;
            val startTime = mediaplayer.currentPosition;
            Statified.startTimeText?.setText(String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long))))
            Statified.endTimeText?.setText(String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long))))

            Statified.seekbar?.max = finalTime
            Statified.seekbar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)

        }


    }




    var mAcceleration:Float=0f
    var mAccelerationCurrent:Float=0f
    var mAccelerationLast:Float=0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title="Now Playing"
        Statified.seekbar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playpauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView=view?.findViewById(R.id.visualizer_view)
        Statified.fab=view?.findViewById(R.id.favoriteIcon)
        Statified.fab?.alpha=0.8f
        return view

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        Statified.favoriteContent=EchoDatabases(Statified.myActivity)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {

            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("SongId")!!.toLong()
            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")
            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar=arguments?.get("FavBottomBar") as? String
        if(fromFavBottomBar!=null)
        {
            Statified.mediaPlayer=FavouriteFragment.Statified.mediaPlayer
        }
        else
        {
            Statified.mediaPlayer= MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {

                Statified.mediaPlayer?.setDataSource(Statified.myActivity,Uri.parse(path))
                Statified.mediaPlayer?.prepare()
            }
            catch(e:Exception)
            {
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
        }
        Statified.mediaPlayer = MediaPlayer()
        Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {

            Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(path))
            Statified.mediaPlayer?.prepare()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Statified.mediaPlayer?.start()
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playpauseImageButton?.setBackgroundResource(pause_icon)
        } else {
            Statified.playpauseImageButton?.setBackgroundResource(play_icon)
        }

        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()

        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context,0)
        Statified._audioVisualization?.linkTo(visualizationHandler)
        var prefsForShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed=prefsForShuffle?.getBoolean("feature",false)
        if(isShuffleAllowed as Boolean)
        {
            Statified.currentSongHelper?.isShuffle=true
            Statified.currentSongHelper?.isLoop=false
            Statified.shuffleImageButton?.setBackgroundResource(shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(loop_white_icon)
        }
        else
        {
            Statified.currentSongHelper?.isShuffle=false
            Statified.shuffleImageButton?.setBackgroundResource(shuffle_white_icon)
        }
        var prefsForLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopAllowed=prefsForLoop?.getBoolean("feature",false)
        if(isLoopAllowed as Boolean)
        {
            Statified.currentSongHelper?.isShuffle=false
            Statified.currentSongHelper?.isLoop=true
            Statified.shuffleImageButton?.setBackgroundResource(shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(loop_icon)
        }
        else
        {
            Statified.currentSongHelper?.isLoop=false
            Statified.loopImageButton?.setBackgroundResource(loop_white_icon)
        }
        if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_on))
        }
        else
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_off))
        }
    }







    fun clickHandler() {

        Statified.fab?.setOnClickListener({
            if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_off))
                Statified.favoriteContent?.deleteFavorite(Statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(Statified.myActivity,"Removed from Favourites",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_on))
                Statified.favoriteContent?.storeAsFavorite(Statified.currentSongHelper?.songId?.toInt(),Statified.currentSongHelper?.songArtist,Statified.currentSongHelper?.songTitle,Statified.currentSongHelper?.songPath)
                Toast.makeText(Statified.myActivity,"Add to Favourites",Toast.LENGTH_SHORT).show()
            }
        })
        var editorShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
        var editorLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
        Statified.shuffleImageButton?.setOnClickListener({
                if (Statified.currentSongHelper?.isShuffle as Boolean)
                {
                    Statified.shuffleImageButton?.setBackgroundResource(shuffle_white_icon)
                    Statified.currentSongHelper?.isShuffle = false
                   editorShuffle?.putBoolean("feature",false)
                   editorShuffle?.apply()
                }
                else
                {
                    Statified.currentSongHelper?.isShuffle = true
                    Statified.currentSongHelper?.isLoop = false
                    Statified.shuffleImageButton?.setBackgroundResource(shuffle_icon)
                    Statified.loopImageButton?.setBackgroundResource(loop_white_icon)
                    editorShuffle?.putBoolean("feature",true)
                    editorShuffle?.apply()
                    editorLoop?.putBoolean("feature",false)
                    editorLoop?.apply()
                }

        })

        Statified.nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        })

        Statified.previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            Statified.playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (Statified.currentSongHelper?.isLoop as Boolean) {

                Statified.loopImageButton?.setBackgroundResource(loop_white_icon)
            }
            playPrevious()

        })

        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop=Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
             if (Statified.currentSongHelper?.isLoop as Boolean) {
                 Statified.loopImageButton?.setBackgroundResource(loop_white_icon)
                 Statified.currentSongHelper?.isLoop = false
                 editorLoop?.putBoolean("feature",false)
                 editorLoop?.apply()

            } else {
                 Statified.currentSongHelper?.isShuffle = false
                 Statified.currentSongHelper?.isLoop = true
                 Statified.shuffleImageButton?.setBackgroundResource(shuffle_white_icon)
                 Statified.loopImageButton?.setBackgroundResource(loop_icon)
                 editorShuffle?.putBoolean("feature",false)
                 editorShuffle?.apply()
                 editorLoop?.putBoolean("feature",true)
                 editorLoop?.apply()
            }


        })

        Statified.playpauseImageButton?.setOnClickListener({
            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.playpauseImageButton?.setBackgroundResource(play_icon)
                Statified.currentSongHelper?.isPlaying = false
            } else {
                Statified.mediaPlayer?.start()
                Statified.playpauseImageButton?.setBackgroundResource(pause_icon)
                Statified.currentSongHelper?.isPlaying = true
            }
        })



    }



    override fun onViewCreated(view:View,savedInstanceState: Bundle?)
    {
        super.onViewCreated(view,savedInstanceState)
        Statified._audioVisualization=Statified.glView as AudioVisualization

    }

    override fun onPause() {
        super.onPause()
        Statified._audioVisualization?.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onResume() {
        super.onResume()
        Statified._audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Statified._audioVisualization?.release()
    }

    fun playPrevious() {

        Statified.currentPosition -= 1
        if (Statified.currentPosition == -1) {
            Statified.currentPosition = 0
        }

        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playpauseImageButton?.setBackgroundResource(pause_icon)
        } else {
            Statified.playpauseImageButton?.setBackgroundResource(play_icon)
            Statified.currentSongHelper?.isLoop = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songId as Long
            Staticated.updateTextViews(Statified.currentSongHelper?.songTitle as String, Statified.currentSongHelper?.songArtist as String)
        }
        Statified.mediaPlayer?.reset()
        try {

            Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean)
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context, favorite_on))
        }
        else
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity   as Context, favorite_off))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager=Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration=0.0f
        mAccelerationCurrent=SensorManager.GRAVITY_EARTH
        mAccelerationLast=SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    fun bindShakeListener()
    {
        Statified.mSensorListener=object:SensorEventListener
        {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int)
            {        }
            override fun onSensorChanged(p0: SensorEvent?)
            {
                val x=p0!!.values[0]
                val y=p0!!.values[1]
                val z=p0!!.values[2]
                mAccelerationLast=mAccelerationCurrent
                mAccelerationCurrent=Math.sqrt((x*x+y*y+z*z).toDouble()).toFloat()
                val delta=mAccelerationCurrent-mAccelerationLast
                mAcceleration=mAcceleration+0.9f+delta
                if(mAcceleration>12)
                {
                    val prefs=Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
                    val isAllowed=prefs?.getBoolean("feature",false)
                    if(isAllowed as Boolean)
                    {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }

        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item:MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true
        val item2:MenuItem?=menu?.findItem(R.id.action_sort)
        item2?.isVisible=false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.action_redirect->
            {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }


}


