package hk.com.nmg.notificationobserver

import org.json.JSONArray
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun List<AppPushModel>.toHtmlTable(): String {
    // Parse the JSON array


    // Start the HTML table
    val htmlBuilder = StringBuilder()
    htmlBuilder.append("<table border='1'>")

    // Table header row
    htmlBuilder.append("<tr>")
    htmlBuilder.append("<th>App Name</th>")
    htmlBuilder.append("<th>Date</th>")
    htmlBuilder.append("<th>Received Time</th>")
    htmlBuilder.append("<th>App Push Title</th>")
    htmlBuilder.append("<th>App Push Content</th>")
    htmlBuilder.append("</tr>")

    // Iterate over JSON array and add each item as a row in the table
    this.forEach { appPushModel ->



        // Add a row for each JSON object
        htmlBuilder.append("<tr>")
        htmlBuilder.append("<td>").append(appPushModel.appName).append("</td>")
        htmlBuilder.append("<td>").append(appPushModel.date).append("</td>")
        htmlBuilder.append("<td>").append(appPushModel.receivedTime).append("</td>")
        htmlBuilder.append("<td>").append(appPushModel.appPushTitle).append("</td>")
        htmlBuilder.append("<td>").append(appPushModel.appPushContent).append("</td>")
        htmlBuilder.append("</tr>")
    }

    // End the HTML table
    htmlBuilder.append("</table>")

    return htmlBuilder.toString()
}