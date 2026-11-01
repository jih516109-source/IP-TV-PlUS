package com.example

import com.example.data.model.ChannelEntity
import com.example.data.parser.M3UParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class ExampleUnitTest {

    @Test
    fun testM3UParserWithExtinf() = runBlocking {
        val sampleM3U = """
            #EXTM3U
            #EXTINF:-1 tvg-id="nasa.us" tvg-name="NASA TV" tvg-logo="https://example.com/nasa.png" group-title="Documentary",NASA Public 4K
            https://example.com/live/nasa/master.m3u8
            #EXTINF:-1 tvg-name="Sky Sports" tvg-logo="https://example.com/sky.png" group-title="Sports",Sky Sports Football FHD
            https://example.com/live/sports.m3u8
            #EXTINF:-1 group-title="Bangladesh",Dhaka TV Live
            https://example.com/live/dhaka.m3u8
        """.trimIndent()

        val parsedChannels = mutableListOf<ChannelEntity>()
        val count = M3UParser.parseM3UStream(
            inputStream = ByteArrayInputStream(sampleM3U.toByteArray(Charsets.UTF_8)),
            playlistId = 1L,
            batchSize = 10
        ) { batch, _ ->
            parsedChannels.addAll(batch)
        }

        assertEquals(3, count)
        assertEquals(3, parsedChannels.size)

        // NASA Channel
        val nasa = parsedChannels[0]
        assertEquals("NASA Public 4K", nasa.name)
        assertEquals("Documentary", nasa.category)
        assertEquals("https://example.com/nasa.png", nasa.logoUrl)
        assertEquals("https://example.com/live/nasa/master.m3u8", nasa.streamUrl)
        assertEquals("4K", nasa.resolutionBadge)

        // Sports Channel
        val sports = parsedChannels[1]
        assertEquals("Sky Sports Football FHD", sports.name)
        assertEquals("Sports", sports.category)
        assertEquals("FHD", sports.resolutionBadge)

        // Bangladesh Channel
        val bd = parsedChannels[2]
        assertEquals("Dhaka TV Live", bd.name)
        assertEquals("Bangladesh", bd.category)
    }

    @Test
    fun testM3UParserLargeVolumePerformance() = runBlocking {
        val builder = StringBuilder("#EXTM3U\n")
        val totalTestChannels = 1500
        for (i in 1..totalTestChannels) {
            builder.append("#EXTINF:-1 tvg-id=\"ch$i\" tvg-name=\"Channel $i\" tvg-logo=\"https://img.com/$i.png\" group-title=\"Entertainment\",Live HD Channel $i\n")
            builder.append("https://stream.server.net/ch$i/live.m3u8\n")
        }

        val startTime = System.currentTimeMillis()
        var batchCount = 0
        var totalParsed = 0

        val count = M3UParser.parseM3UStream(
            inputStream = ByteArrayInputStream(builder.toString().toByteArray(Charsets.UTF_8)),
            playlistId = 99L,
            batchSize = 250
        ) { batch, countSoFar ->
            batchCount++
            totalParsed = countSoFar
            assertTrue(batch.isNotEmpty())
        }

        val duration = System.currentTimeMillis() - startTime

        assertEquals(totalTestChannels, count)
        assertEquals(totalTestChannels, totalParsed)
        assertEquals(6, batchCount) // 1500 / 250 = 6 batches
        assertTrue("Parsing 1500 channels must take less than 1.5s", duration < 1500)
    }

    @Test
    fun testAssetJsonChannelsCount() {
        val file = java.io.File("src/main/assets/iptv_org_channels.json")
        if (file.exists()) {
            val content = file.readText(Charsets.UTF_8)
            assertTrue("JSON content should not be empty", content.length > 1000)
            assertTrue("JSON should contain streaming URLs", content.contains("m3u8"))
        }
    }
}
