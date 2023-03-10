package com.erickrodrigues.musicflux.playlist;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSongToPlaylistDto {

    @NotNull(message = "Song ID is mandatory")
    @Positive(message = "Song ID must be greater than zero")
    private Long songId;
}
