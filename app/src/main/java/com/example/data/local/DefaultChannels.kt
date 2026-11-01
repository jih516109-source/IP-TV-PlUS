package com.example.data.local

import com.example.data.model.ChannelEntity

object DefaultChannels {
    val INITIAL_CHANNELS = listOf(
        // Featured & News
        ChannelEntity(
            id = 1,
            name = "NASA TV Public HD",
            category = "Documentary",
            streamUrl = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=300&q=80",
            description = "Live views of Earth from the ISS, rocket launches, and NASA scientific coverage.",
            resolutionBadge = "4K",
            isFeatured = true,
            viewerCount = 42800,
            country = "USA",
            orderIndex = 1
        ),
        ChannelEntity(
            id = 2,
            name = "Al Jazeera English",
            category = "News",
            streamUrl = "https://live-hls-web-aje.getaj.net/AJE/01.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=300&q=80",
            description = "24-hour international news coverage, in-depth reports, and live debate.",
            resolutionBadge = "FHD",
            isFeatured = true,
            viewerCount = 38900,
            country = "International",
            orderIndex = 2
        ),
        ChannelEntity(
            id = 3,
            name = "Deutsche Welle HD",
            category = "News",
            streamUrl = "https://dwamdstream102.akamaized.net/hls/live/2015525/dwstream102/index.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=300&q=80",
            description = "German international broadcaster providing global news and analysis.",
            resolutionBadge = "HD",
            viewerCount = 21500,
            country = "Germany",
            orderIndex = 3
        ),
        ChannelEntity(
            id = 4,
            name = "Red Bull Action Sports",
            category = "Sports",
            streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=300&q=80",
            description = "Extreme sports, mountain biking, Formula 1 highlights, surfing, and stunts.",
            resolutionBadge = "FHD",
            isFeatured = true,
            viewerCount = 54100,
            country = "Austria",
            orderIndex = 4
        ),
        // Bangladesh Channels
        ChannelEntity(
            id = 5,
            name = "BTV National Live",
            category = "Bangladesh",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            logoUrl = "https://images.unsplash.com/photo-1596495577886-d920f1fb7238?w=300&q=80",
            description = "Bangladesh Television national terrestrial and satellite public channel.",
            resolutionBadge = "HD",
            viewerCount = 18400,
            country = "Bangladesh",
            orderIndex = 5
        ),
        ChannelEntity(
            id = 6,
            name = "Sangsad Bangladesh TV",
            category = "Bangladesh",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=300&q=80",
            description = "Official parliamentary sessions, education, and state affairs broadcast.",
            resolutionBadge = "HD",
            viewerCount = 9200,
            country = "Bangladesh",
            orderIndex = 6
        ),
        ChannelEntity(
            id = 7,
            name = "Dhaka News 24",
            category = "Bangladesh",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1495020689067-958852a7765e?w=300&q=80",
            description = "Round the clock news, weather updates, and talk shows from Dhaka.",
            resolutionBadge = "FHD",
            viewerCount = 15600,
            country = "Bangladesh",
            orderIndex = 7
        ),
        // India Channels
        ChannelEntity(
            id = 8,
            name = "DD National India",
            category = "India",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1532375810709-75b1da00537c?w=300&q=80",
            description = "Doordarshan flagship Indian public television network broadcasting cultural programs.",
            resolutionBadge = "HD",
            viewerCount = 29300,
            country = "India",
            orderIndex = 8
        ),
        ChannelEntity(
            id = 9,
            name = "DD Sports HD",
            category = "Sports",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=300&q=80",
            description = "Live cricket, badminton, kabaddi, hockey, and athletic championship telecasts.",
            resolutionBadge = "FHD",
            viewerCount = 34700,
            country = "India",
            orderIndex = 9
        ),
        ChannelEntity(
            id = 10,
            name = "DD News India",
            category = "India",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1586339949916-3e9457bef6d3?w=300&q=80",
            description = "Official Indian state news service in Hindi and English.",
            resolutionBadge = "HD",
            viewerCount = 24100,
            country = "India",
            orderIndex = 10
        ),
        // Movies & Cinema
        ChannelEntity(
            id = 11,
            name = "Tears of Steel Cinema",
            category = "Movies",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            logoUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=300&q=80",
            description = "Sci-fi thriller open movie project featuring dystopian VFX and robotics.",
            resolutionBadge = "4K",
            isFeatured = true,
            viewerCount = 49200,
            country = "International",
            orderIndex = 11
        ),
        ChannelEntity(
            id = 12,
            name = "Sintel Fantasy Cinema",
            category = "Movies",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            logoUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=300&q=80",
            description = "Epic dragon fantasy film showcasing world-class computer animated storytelling.",
            resolutionBadge = "FHD",
            viewerCount = 31200,
            country = "International",
            orderIndex = 12
        ),
        // Kids
        ChannelEntity(
            id = 13,
            name = "Big Buck Bunny Toons",
            category = "Kids",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            logoUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=300&q=80",
            description = "Hilarious animated adventures of the forest bunny and flying squirrels.",
            resolutionBadge = "FHD",
            viewerCount = 41000,
            country = "International",
            orderIndex = 13
        ),
        // Entertainment & Music
        ChannelEntity(
            id = 14,
            name = "Vibe Lounge Music TV",
            category = "Music",
            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300&q=80",
            description = "Non-stop electronic, chillout, and synthwave musical streams.",
            resolutionBadge = "FHD",
            viewerCount = 28600,
            country = "International",
            orderIndex = 14
        ),
        ChannelEntity(
            id = 15,
            name = "Global Entertainment 1",
            category = "Entertainment",
            streamUrl = "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            logoUrl = "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=300&q=80",
            description = "Top rated entertainment specials, celebrity interviews, and lifestyle series.",
            resolutionBadge = "HD",
            viewerCount = 23900,
            country = "International",
            orderIndex = 15
        ),
        // Documentary
        ChannelEntity(
            id = 16,
            name = "Earth Planet Explorer",
            category = "Documentary",
            streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            logoUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300&q=80",
            description = "Breathtaking natural landscapes, deep ocean marine life, and atmospheric wonders.",
            resolutionBadge = "4K",
            viewerCount = 33800,
            country = "International",
            orderIndex = 16
        )
    )
}
