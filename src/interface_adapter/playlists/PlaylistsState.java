package interface_adapter.playlists;

import java.util.HashMap;
import java.util.Map;

public class PlaylistsState {
    Map<String, String> playlist_map;

    public PlaylistsState() {
        this.playlist_map = new HashMap();
    }

    public void setPlaylistMap(Map playlist_map) {
        this.playlist_map = playlist_map;
    }

    public Map<String, String> getPlaylistMap(){
        return playlist_map;
    }
}