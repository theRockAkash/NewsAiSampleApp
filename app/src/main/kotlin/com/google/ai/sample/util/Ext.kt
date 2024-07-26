package com.google.ai.sample.util

import android.os.Build
import android.os.Bundle
import java.io.Serializable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @Created by akash on 22-07-2024.
 * Know more about author at https://akash.cloudemy.in
 */
fun String?.toDate(): String {
    if(this ==null) return ""
    // Define the input and output date formats
    val inputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z")
    val outputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    // Parse the input date string
    val zonedDateTime = ZonedDateTime.parse(this, inputFormatter)

    // Format the ZonedDateTime to the desired output format
    return zonedDateTime.format(outputFormatter)
}
@Suppress("DEPRECATION")
inline fun <reified T : Serializable> Bundle.getSerializableData(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getSerializable(key, T::class.java)
    else getSerializable(key) as? T
}