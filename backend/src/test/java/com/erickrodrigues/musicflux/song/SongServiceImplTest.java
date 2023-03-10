package com.erickrodrigues.musicflux.song;

import com.erickrodrigues.musicflux.album.Album;
import com.erickrodrigues.musicflux.user.User;
import com.erickrodrigues.musicflux.album.AlbumRepository;
import com.erickrodrigues.musicflux.user.UserRepository;
import com.erickrodrigues.musicflux.recently_played.RecentlyPlayedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SongServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private RecentlyPlayedRepository recentlyPlayedRepository;

    @InjectMocks
    private SongServiceImpl songService;

    @Test
    public void play() {
        Long songId = 1L, userId = 1L;

        User user = User.builder().id(userId).build();
        Song song = Song.builder().id(songId).build();

        when(songRepository.findById(songId)).thenReturn(Optional.of(song));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        songService.play(userId, songId);

        assertEquals(1, song.getNumberOfPlays());

        verify(songRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(songRepository, times(1)).save(any());
        verify(userRepository, times(1)).save(any());
        verify(recentlyPlayedRepository, times(1)).save(any());
    }

    @Test
    public void playWithInvalidArguments() {
        when(songRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> songService.play(1L, 1L));

        when(songRepository.findById(anyLong())).thenReturn(Optional.of(Song.builder().build()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> songService.play(1L, 1L));
    }

    @Test
    public void findAllByTitle() {
        String title = "I wanna love you";
        List<Song> songs = List.of(
                Song.builder().id(1L).title("i wanna love you").build(),
                Song.builder().id(2L).title("all i know is i wanna love you").build()
        );

        when(songRepository.findAllByTitleContainingIgnoreCase(title)).thenReturn(songs);

        assertEquals(2, songService.findAllByTitleContainingIgnoreCase(title).size());
        verify(songRepository, times(1)).findAllByTitleContainingIgnoreCase(anyString());
    }

    @Test
    public void findAllByGenreName() {
        final String genre = "synth-pop";
        final List<Song> songs = List.of(
                Song.builder().id(1L).title("Black Celebration").build(),
                Song.builder().id(2L).title("Never Let Me Down Again").build()
        );

        when(songRepository.findAllByGenresNameIgnoreCase(genre)).thenReturn(songs);

        assertEquals(2, songService.findAllByGenreName(genre).size());
        verify(songRepository, times(1)).findAllByGenresNameIgnoreCase(anyString());
    }

    @Test
    public void findAllByAlbumId() {
        final Long albumId = 1L;
        final List<Song> songs = List.of(
                Song.builder().id(1L).title("i wanna love you").build(),
                Song.builder().id(2L).title("all i know is i wanna love you").build()
        );
        final Album album = Album.builder().id(albumId).songs(songs).build();

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        assertEquals(2, songService.findAllByAlbumId(albumId).size());
        verify(albumRepository, times(1)).findById(anyLong());
    }

    @Test
    public void findAllByAlbumIdThatDoesNotExist() {
        final Long albumId = 1L;
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> songService.findAllByAlbumId(albumId));
        verify(albumRepository, times(1)).findById(anyLong());
    }

    @Test
    public void findAllByArtistId() {
        List<Album> albums = List.of(
                Album.builder()
                        .id(1L)
                        .songs(
                                List.of(Song.builder().id(1L).numberOfPlays(5400L).build(),
                                        Song.builder().id(2L).numberOfPlays(400L).build(),
                                        Song.builder().id(3L).numberOfPlays(7600L).build())
                        )
                        .build(),
                Album.builder()
                        .id(2L)
                        .songs(
                                List.of(Song.builder().id(4L).numberOfPlays(1000L).build(),
                                        Song.builder().id(5L).numberOfPlays(9000L).build(),
                                        Song.builder().id(6L).numberOfPlays(7200L).build())
                        )
                        .build()
        );

        when(albumRepository.findAllByArtistsId(1L)).thenReturn(albums);

        String topSongsIds = songService.findMostPlayedSongsByArtistId(1L)
                .stream()
                .map(Song::getId)
                .toList()
                .toString();

        assertEquals("[5, 3, 6, 1, 4]", topSongsIds);
    }

    @Test
    public void findAllByArtistIdWhenArtistHasLessThanFiveSongs() {
        List<Album> albums = List.of(
                Album.builder()
                        .id(1L)
                        .songs(
                                List.of(Song.builder().id(1L).numberOfPlays(5400L).build(),
                                        Song.builder().id(2L).numberOfPlays(400L).build(),
                                        Song.builder().id(3L).numberOfPlays(7600L).build())
                        )
                        .build()
        );

        when(albumRepository.findAllByArtistsId(1L)).thenReturn(albums);

        String topSongsIds = songService.findMostPlayedSongsByArtistId(1L)
                .stream()
                .map(Song::getId)
                .toList()
                .toString();

        assertEquals("[3, 1, 2]", topSongsIds);
    }
}
