package com.jhuo.taskmanager.task_manager.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtilsTest {

    @Test
    fun `convertToApiFormat should return null for null or blank input`() {
        assertNull(DateUtils.convertToApiFormat(null))
        assertNull(DateUtils.convertToApiFormat(""))
        assertNull(DateUtils.convertToApiFormat("   "))
    }

    @Test
    fun `convertToApiFormat should correctly convert valid date string`() {
        val input = "2023 Oct 15 at 14:30"
        val expected = "2023-10-15 14:30:00"
        assertEquals(expected, DateUtils.convertToApiFormat(input))
    }

    @Test
    fun `convertToApiFormat should return null for invalid date format`() {
        assertNull(DateUtils.convertToApiFormat("Invalid date"))
        assertNull(DateUtils.convertToApiFormat("2023/10/15 14:30"))
    }

    @Test
    fun `convertToUiFormat should return null for null or blank input`() {
        assertNull(DateUtils.convertToUiFormat(null))
        assertNull(DateUtils.convertToUiFormat(""))
        assertNull(DateUtils.convertToUiFormat("   "))
    }

    @Test
    fun `convertToUiFormat should correctly convert ISO format`() {
        val input = "2023-10-15T14:30:00.000Z"
        val expected = SimpleDateFormat("yyyy MMM dd 'at' HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(input)!!)

        assertEquals(expected, DateUtils.convertToUiFormat(input))
    }

    @Test
    fun `convertToUiFormat should correctly convert standard format`() {
        val input = "2023-10-15 14:30:00"
        val expected = SimpleDateFormat("yyyy MMM dd 'at' HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }.format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(input)!!)

        assertEquals(expected, DateUtils.convertToUiFormat(input))
    }

    @Test
    fun `convertToUiFormat should return input when format is unrecognized`() {
        val input = "Invalid date format"
        assertEquals(input, DateUtils.convertToUiFormat(input))
    }

    @Test
    fun `formatCreateTimeToEntity should return null for null input`() {
        assertNull(DateUtils.formatCreateTimeToEntity(null))
    }

    @Test
    fun `formatCreateTimeToEntity should correctly format timestamp`() {
        val timestamp = System.currentTimeMillis()
        val expected = SimpleDateFormat("yyyy MMM dd 'at' HH:mm", Locale.getDefault()).format(Date(timestamp))

        assertEquals(expected, DateUtils.formatCreateTimeToEntity(timestamp.toString()))
    }

    @Test
    fun `formatCreateTimeToEntity should return null for invalid timestamp`() {
        assertNull(DateUtils.formatCreateTimeToEntity("not a number"))
    }
}