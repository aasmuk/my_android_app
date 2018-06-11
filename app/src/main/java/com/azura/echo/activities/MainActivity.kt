package com.azura.echo.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.azura.echo.R
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.support.v7.widget.Toolbar


import com.azura.echo.activities.MainActivity.Statified.drawerLayout
import com.azura.echo.adapters.NavigationDrawerAdapter
import com.azura.echo.fragments.MainScreenFragment
import com.azura.echo.fragments.SongPlayingFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.security.KeyStore

class MainActivity : AppCompatActivity()
{
    var navigationDrawerImages: ArrayList<String> = arrayListOf()

    var iconsNavDrawer = intArrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_favorites, R.drawable.navigation_settings, R.drawable.navigation_aboutus)

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notificationManager:NotificationManager?=null
    }

    var trackNotificationBuilder:Notification?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        navigationDrawerImages.add("All Songs")
        navigationDrawerImages.add("Favourites")
        navigationDrawerImages.add("Settings")
        navigationDrawerImages.add("About Us")

        val mainScreenFragment = MainScreenFragment();
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, mainScreenFragment, "MainScreenFragment")
                .commit()
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this@MainActivity, MainActivity.Statified.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState();


        var navigationAdapter = NavigationDrawerAdapter(navigationDrawerImages, iconsNavDrawer, this)
        navigationAdapter.notifyDataSetChanged();
        var navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()
        navigation_recycler_view.adapter = navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)






        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(), intent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        trackNotificationBuilder = Notification.Builder(this)
                .setContentTitle("A track is playing in the background")
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    override fun onStop()
    {
        super.onStop()
        try {

            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                Statified.notificationManager?.notify(404,trackNotificationBuilder)
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onStart()
    {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(404)
        }catch(e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onResume()
    {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(404)
        }catch(e:Exception)
        {
            e.printStackTrace()
        }
    }



}