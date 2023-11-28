package view;

import entity.Song;
import interface_adapter.ViewManagerModel;
import interface_adapter.home.HomeController;
import interface_adapter.home.HomeViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "Home";

    private final HomeViewModel homeViewModel;
    private final HomeController homeController;
    private final ViewManagerModel viewManagerModel;

    private final JButton splitPlaylist;

    private final JButton mergePlaylist;

    private final JButton artistsPlaylistMaker;

    private final JButton spotifyToYoutube;
    private final JLabel profile;
    private final JLabel profileText;
    private final JLabel welcome;

    private final DefaultListModel<String> playlistsModel;
    private final JList<String> playlistsList;
    private DefaultListModel<String> songsModel;
    private JList<String> songsList;

    private Map<String, String> playlistNameToIDMap;

    private JLabel titleLabel;

    public HomeView(HomeController homeController, HomeViewModel homeViewModel, ViewManagerModel viewManagerModel) {
        this.homeController = homeController;
        this.homeViewModel = homeViewModel;
        this.viewManagerModel = viewManagerModel;

        this.playlistNameToIDMap = new HashMap<String, String>();

        homeViewModel.addPropertyChangeListener(this);

        // Need actionListeners from the other views, allows us to check if we change back to home
        viewManagerModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        titleLabel = new JLabel("Home");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16)); // Set font to bold
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Create a panel for the buttons and use BoxLayout to stack them vertically
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Buttons")); // Add a titled border

        splitPlaylist = createStyledButton(HomeViewModel.SPLIT_PLAYLIST_NAME);
        artistsPlaylistMaker = createStyledButton(HomeViewModel.ARTISTS_PLAYLIST_MAKER_NAME);
        mergePlaylist = createStyledButton(HomeViewModel.MERGE_PLAYLIST_NAME);
        spotifyToYoutube = createStyledButton(HomeViewModel.SPOTIFY_TO_YT_NAME);
     
        buttonsPanel.add(mergePlaylist);
        buttonsPanel.add(splitPlaylist);
        buttonsPanel.add(artistsPlaylistMaker);
        buttonsPanel.add(spotifyToYoutube);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing

        add(buttonsPanel, BorderLayout.WEST);

        // Create a panel for the profile information
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile")); // Add a titled border

        ImageIcon profileIcon = new ImageIcon("src/images/profile_icon_2.png");
        Image scaledImage = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon smallerProfileIcon = new ImageIcon(scaledImage);

        profile = new JLabel(smallerProfileIcon);
        profile.setAlignmentX(Component.CENTER_ALIGNMENT);
        profile.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        profile.setIconTextGap(10);

        profileText = new JLabel("Profile");
        profileText.setAlignmentX(Component.CENTER_ALIGNMENT);

        profile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                profile.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); // Change border color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                profile.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Restore border color
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                homeController.execute("profile");
            }
        });

        profilePanel.add(profile);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing
        profilePanel.add(profileText);

        // Add the profile panel to the EAST of the main panel
        add(profilePanel, BorderLayout.EAST);

        // Create a panel for the welcome label
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createTitledBorder("Playlists")); // Add a titled border

        welcome = new JLabel();
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

//        welcomePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing
        welcomePanel.add(welcome);
        welcomePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some spacing

        JPanel scrollPanePanel = new JPanel();
        scrollPanePanel.setLayout(new GridLayout(1, 2));

        // add ScrollPanes displaying the user's playlists and songs
        playlistsModel = new DefaultListModel<>();
        playlistsList = new JList<>(playlistsModel);
        JScrollPane playlistsScrollPane = new JScrollPane(playlistsList);
        playlistsScrollPane.setBorder(BorderFactory.createTitledBorder("Your Playlists"));

        scrollPanePanel.add(playlistsScrollPane);

        songsModel = new DefaultListModel<>();
        songsList = new JList<>(songsModel);
        JScrollPane songsScrollPane = new JScrollPane(songsList);
        songsScrollPane.setBorder(BorderFactory.createTitledBorder("Songs"));

        scrollPanePanel.add(songsScrollPane);

        welcomePanel.add(scrollPanePanel);

        // Add the welcome label to the CENTER of the main panel
        add(welcomePanel, BorderLayout.CENTER);


        playlistsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedPlaylist = playlistsList.getSelectedValue();
                if (selectedPlaylist != null) {
                    updateSongs(selectedPlaylist);
                }
            }
        });


        songsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                String songName = songsList.getSelectedValue();
                if (songName != null) {
                    actionOnPressSong(songName);
                }
            }
        });
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 240, 240));
        button.setMaximumSize(new Dimension(150, 40));

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(button)) {
                    homeController.execute(text);
                }
            }
        });

        return button;
    }

    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            welcome.setText("Welcome " + homeViewModel.getState().getDisplayName());
        }

        // Check if we went back to this view - if we do, then we want to update our playlist model
        if (evt.getPropertyName().equals("view") && evt.getNewValue().equals(this.viewName)) {
            refresh();
        }

    }

    private void refresh() {
        playlistsModel.clear();
        // Store this so we don't need to call the api every time
        this.playlistNameToIDMap = homeController.getPlaylistsMap();
        List<String> playlistNames = playlistNameToIDMap.keySet().stream().toList();
        displayPlaylists(playlistNames);
    }

    private void displayPlaylists(List<String> playlistNames) {
        for (String playlistName : playlistNames) {
            playlistsModel.addElement(playlistName);
        }
    }

    private void updateSongs(String playlistName) {
        String playlistID = playlistNameToIDMap.get(playlistName);
        songsModel.clear();
        List<Song> songs = homeController.getSongs(playlistID);
        for (Song song : songs) {
            songsModel.addElement(song.getName());
        }
    }


    private void actionOnPressSong(String songName) {
        System.out.println(songName);
        // TODO: implement Arjun's use case: whenever a song is selected, this function is called
    }

}
