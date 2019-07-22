package com.bereket.ethiopiandama

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group

import com.nightonke.boommenu.BoomButtons.BoomButton
import com.nightonke.boommenu.BoomButtons.HamButton
import com.nightonke.boommenu.BoomMenuButton

import java.util.Locale


class ManuActivity : AppCompatActivity() {

    var localeCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        val preferenceManager = PreferenceManager(this)
        if(!preferenceManager.LastLanguage().equals("default"))
            changeLocale(preferenceManager.LastLanguage())

        val webView = findViewById<WebView>(R.id.myWebView);
//        val webSettings = webView.settings
//        webSettings.javaScriptEnabled = true
        webView.webViewClient =  WebViewClient()
        webView.loadUrl("file:///android_asset/index.html")

        val bmb:BoomMenuButton = findViewById(R.id.bmb)
        for (i in 0 until bmb.getPiecePlaceEnum().pieceNumber() as Int) {
            val builder = HamButton.Builder()
            when(i){
                0 -> builder.normalTextRes(R.string.singlePlayer)
                        .normalImageRes(R.drawable.ic_person_black_24dp)
                        .listener {
                            singlePlayer()
                        }
                1 -> builder.normalTextRes(R.string.multiPlayer)
                        .normalImageRes(R.drawable.ic_people_black_24dp)
                        .listener {
                            multiPlayer()
                        }
                2 -> builder.normalTextRes(R.string.language)
                        .normalImageRes(R.drawable.ic_language_black_24dp)
                        .listener {
                            changeLanguage()
                        }
                3 -> builder.normalTextRes(R.string.help)
                        .normalImageRes(R.drawable.ic_help_black_24dp)
                        .listener {
                            help()
                        }
                4 -> builder.normalTextRes(R.string.exit)
                        .normalImageRes(R.drawable.ic_exit_to_app_black_24dp)
                        .listener {
                            exit()
                        }
            }


            bmb.addBuilder(builder)
        }

       bmb.setAutoBoom(true)
    }

    fun changeLocale(localeCode: String){
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(localeCode.toLowerCase()))
        } else {
            config.locale = Locale(localeCode.toLowerCase())
        }
        resources.updateConfiguration(config, dm)

    }

    fun help() {
        val preferenceManager = PreferenceManager(applicationContext)
        preferenceManager.setFirstTimeLaunch(true)
        startActivity(Intent(this@ManuActivity, MainScreen::class.java))
        finish()
    }

    fun changeLanguage() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val levelsView = inflater.inflate(R.layout.languages, null)
        val returnValue = ReturnValue()
        val builder = AlertDialog.Builder(this)
        builder.setView(levelsView)
        val popupWindow = builder.create()
        popupWindow.show()
        val group = levelsView.findViewById<Group>(R.id.group)
        val refIds = group.referencedIds
        for (id in refIds) {
            levelsView.findViewById<View>(id).setOnClickListener(View.OnClickListener { v ->
                val id = v.id
                when (id) {
                    R.id.english -> {
                        returnValue.`val` = 1
                        popupWindow.dismiss()
                    }
                    R.id.amharic -> {
                        returnValue.`val` = 2
                        popupWindow.dismiss()
                    }
                    R.id.oromic -> {
                        returnValue.`val` = 3
                        popupWindow.dismiss()
                    }
                }



                if (returnValue.`val` == 1)
                    localeCode = "en"
                else if (returnValue.`val` == 2)
                    localeCode = "am"
                else if (returnValue.`val` == 3)
                    localeCode = "om"
                if (localeCode == "") {
                    return@OnClickListener
                }
                changeLocale(localeCode)
                val preferenceManager = PreferenceManager(this)
                preferenceManager.setLastLanguage(localeCode)
                recreate()
            })
        }


    }

    fun exit() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun singlePlayer(): Int {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val levelsView = inflater.inflate(R.layout.levels, null)
        val returnValue = ReturnValue()
        val builder = AlertDialog.Builder(this)
        builder.setView(levelsView)
        val popupWindow = builder.create()
        popupWindow.show()
        val group = levelsView.findViewById<Group>(R.id.group)
        val refIds = group.referencedIds
        for (id in refIds) {
            levelsView.findViewById<View>(id).setOnClickListener { v ->
                val id = v.id
                when (id) {
                    R.id.easy -> {
                        returnValue.`val` = 4
                        popupWindow.dismiss()
                    }
                    R.id.medium -> {
                        returnValue.`val` = 10
                        popupWindow.dismiss()
                    }
                    R.id.hard -> {
                        returnValue.`val` = 11
                        popupWindow.dismiss()
                    }

                }

                val intent = Intent(this@ManuActivity, MainActivity::class.java)
                intent.putExtra("difficulty", returnValue.`val`)
                intent.putExtra("mode", 1)
                startActivity(intent)
                finish()
            }
        }
        return 0
    }
    public fun multiPlayer(){
        val intent = Intent(this@ManuActivity, MainActivity::class.java)
        intent.putExtra("mode", 2)
        startActivity(intent)
        finish()
    }

    internal class ReturnValue {
        var `val`: Int = 0
    }




}
