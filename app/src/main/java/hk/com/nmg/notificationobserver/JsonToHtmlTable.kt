package hk.com.nmg.notificationobserver

import org.json.JSONArray
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun String.jsonToHtmlTable(): String {
    // Parse the JSON array
    val jsonArray = JSONArray(this)

    // Start the HTML table
    val htmlBuilder = StringBuilder()
    htmlBuilder.append("<table border='1'>")

    // Table header row
    htmlBuilder.append("<tr>")
    htmlBuilder.append("<th>Action Tag</th>")
    htmlBuilder.append("<th>Screen Log Type</th>")
    htmlBuilder.append("<th>Screen Log</th>")
    htmlBuilder.append("<th>Timestamp</th>")
    htmlBuilder.append("</tr>")

    // Iterate over JSON array and add each item as a row in the table
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)

        val actionTag = jsonObject.getString("actionTag")
        val screenLogType = jsonObject.getString("screenLogType")
        val screenLog = jsonObject.getString("screenLog")
        val timestamp = jsonObject.getLong("timestamp")
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        val instant = Instant.ofEpochMilli(timestamp)

// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        // Add a row for each JSON object
        htmlBuilder.append("<tr>")
        htmlBuilder.append("<td>").append(actionTag).append("</td>")
        htmlBuilder.append("<td>").append(screenLogType).append("</td>")
        htmlBuilder.append("<td>").append(screenLog).append("</td>")
        htmlBuilder.append("<td>").append(formatter.format(date)).append("</td>")
        htmlBuilder.append("</tr>")
    }

    // End the HTML table
    htmlBuilder.append("</table>")

    return htmlBuilder.toString()
}