package com.gathin.tvhome;
/** Honest local top-shelf copy, with no invented recommendations or account data. */
final class AppIdentity {
    static String subtitle(String pkg) {
        if(pkg.contains("smarttube")||pkg.contains("youtube")||pkg.contains("notubetv"))return "Videos, music and your subscriptions.";
        if(pkg.contains("spotify")||pkg.contains("deezer"))return "Your music. Set the mood.";
        if(pkg.contains("tvplayer")||pkg.contains("iptv"))return "Your channels, ready when you are.";
        if(pkg.equals("com.android.vending"))return "Find something new for your TV.";
        if(pkg.contains("netflix"))return "Explore movies, TV shows and originals. Open Netflix to find your next watch.";
        if(pkg.contains("stremio"))return "Discover movies and series. All your entertainment in one place.";
        if(pkg.contains("amazonvideo"))return "Movies, series and originals. Open Prime Video to explore.";
        if(pkg.contains("downloader"))return "Download files and install the apps you choose.";
        if(pkg.contains("speedtest"))return "Check your connection speed and network performance.";
        if(pkg.startsWith("com.anydesk."))return "Open AnyDesk to manage your remote connection.";
        if(pkg.contains("kick"))return "Live streams and creators you follow.";
        return "A space for the apps you love.";
    }
    private AppIdentity(){}
}
