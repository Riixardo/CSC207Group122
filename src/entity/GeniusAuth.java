package entity;

public class GeniusAuth {

    private static String clientId = "-B8LwUHiCxVsDgGpEOVC87fM49_MQRRtT9GFB_-yuobz3Wzbeub7XQ3lnxON5XXL";

    private static String clientSecret = "";

    private static String accessToken;

    private GeniusAuth() {}

    public static String getAccessToken() {return accessToken;}

    public static String getClientSecret() {return clientSecret;}

    public static String getClientId() {return clientId;}

    public static void setAccessToken(String accessToken) {
        GeniusAuth.accessToken = accessToken;
    }
}