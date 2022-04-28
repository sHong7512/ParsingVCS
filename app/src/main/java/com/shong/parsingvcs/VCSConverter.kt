package com.shong.parsingvcs

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.CalendarContract
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
data class VCS(
    var timeZone: TimeZone? = null,
    var description: String = "",
    var title: String = "",
    var location: String = "",
    var dtStart: Date? = null,
    var dtEnd: Date? = null,
    var complete: Date? = null,
    var status: String = "",
    var aAlarmList: MutableList<Date> = mutableListOf()
)

class VCSConverter {
    val TAG = this::class.java.simpleName + "_sHong"

    data class FreshVCS(
        val title: String,
        val value: String
    )

    //vcs 항목들 분리
    fun devideForMap(_string: String): VCS {
        var string = _string
        val list = mutableListOf<FreshVCS>()

        Log.w(TAG,"shong\n$string")

        for (i in 0 until 100) {
            val buf_ind = string.indexOf("\n")
            if (buf_ind == -1) break

            val str = string.substring(0, buf_ind)
            val str_ind = str.indexOf(":")
            if (str_ind > 0 && str_ind < str.length) {
                list.add(
                    FreshVCS(
                        str.substring(0, str.indexOf(":")),
                        str.substring(str.indexOf(":") + 1, str.length)
                    )
                )
            }

            if (buf_ind + 2 >= string.length) break
            string = string.substring(buf_ind + 1, string.length)
        }

        return vcsParse(list)
    }

    //캘린더로 전송
    fun sendIntent(activity: Activity, vcs: VCS) {
        val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, vcs.dtStart?.time ?: return)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, vcs.dtEnd?.time ?: return)
            putExtra(CalendarContract.Events.TITLE, vcs.title)
            putExtra(CalendarContract.Events.EVENT_LOCATION, vcs.description)   //EVENT_LOCATION이 메모(description)임. 버그 조심
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, vcs.timeZone)
            putExtra(CalendarContract.Events.DESCRIPTION, vcs.location)         //DESCRIPTION이 장소(location)임. 버그 조심

        }
        activity.startActivity(intent)
    }

    //vcs파싱
    private fun vcsParse(list: List<FreshVCS>): VCS {
        val vcs = VCS()
        var tz = TimeZone.getDefault()
        for (item in list) {
            when (item.title) {
                "TZ" -> {
                    vcs.timeZone = TimeZone.getTimeZone("UTC" + item.value)
                    tz = TimeZone.getTimeZone("UTC" + item.value)
                }
                "DESCRIPTION;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8" -> vcs.description = parseUTF8(item.value)
                "SUMMARY;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8" -> vcs.title = parseUTF8(item.value)
                "LOCATION;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8" -> vcs.location = parseUTF8(item.value)
                "DTEND" -> vcs.dtEnd = parseDateTime(item.value, tz)
                "DTSTART" -> vcs.dtStart = parseDateTime(item.value, tz)
                "COMPLETED" -> vcs.complete = parseDateTime(item.value, tz)
                "STATUS" -> vcs.status = item.value
                "AALARM" -> vcs.aAlarmList.add(parseDateTime(item.value, tz))
            }
        }
        Log.w(TAG,"$vcs")
        return vcs
    }

    //utf8 파싱
    private fun parseUTF8(str: String): String {
        var byteArray = byteArrayOf()
        for (i in str.indices step 3) {
            byteArray += Integer.parseInt(str.substring(i + 1, i + 3), 16).toByte()
        }
        return String(byteArray)
    }

    //날짜 파싱
    private fun parseDateTime(str: String, tz: TimeZone): Date {
        return if (str.length > 8) {
            val formatter_tz = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
            formatter_tz.timeZone = tz
            formatter_tz.parse(str)
        } else {
            val formatter_Date = SimpleDateFormat("yyyyMMdd")
            formatter_Date.parse(str)
        }
    }

}