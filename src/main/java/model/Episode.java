package model;

import lombok.Data;

/**
 * Created by Ameer on 3/3/18.
 */
@Data
public class Episode {
    private String id;
    private String title;
    private String summary;
    private String url;
    private String showId;
    private String author;
    private Integer seasonNumber;
    private Integer episodeNumber;
}
