package com.shong.parsingvcs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val exampleString = "BEGIN:VCALENDAR\n" +
                "VERSION:1.0\n" +
                "PRODID:vCal ID default\n" +
                "TZ:+09:00\n" +
                "BEGIN:VEVENT\n" +
                "UID:20220428T100218Z-1@GALAXY-CALENDAR-EVENT-11e01cb6-6dac-4af3-b8fe-cf9047bd55d1\n" +
                "DESCRIPTION;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:=EB=A9=94=EB=AA=A8=EB=A9=94=EB=AA=A8=EB=A9=94=EB=AA=A8\n" +
                "DTEND:20220428T140000Z\n" +
                "DTSTART:20220428T130000Z\n" +
                "LOCATION;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:=EC=9E=A5=EC=86=8C=EC=9E=A5=EC=86=8C=EC=9E=A5=EC=86=8C\n" +
                "COMPLETED:20220428T140000Z\n" +
                "SUMMARY;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:=ED=83=80=EC=9D=B4=ED=8B=80\n" +
                "STATUS:CONFIRMED\n" +
                "AALARM:20220428T130000Z;;1;\n" +
                "AALARM:20220428T125000Z;;1;\n" +
                "AALARM:20220428T120000Z;;1;\n" +
                "AALARM:20220427T130000Z;;1;\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR"
        findViewById<TextView>(R.id.exampleTextView).text = exampleString

        val vcsConverter = VCSConverter()
        findViewById<Button>(R.id.parseButton).setOnClickListener {
            vcsConverter.sendIntent(this@MainActivity, vcsConverter.devideForMap(exampleString))
        }
    }
}