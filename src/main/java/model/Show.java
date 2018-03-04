package model;

import lombok.Data;

import java.util.List;

/**
 * Created by Ameer on 3/3/18.
 */
@Data
public class Show {
    private String id;
    private String title;
    private String author;
    private String summary;
    private String website;
    private String email;
    private String artworkUrl;
    private String showRSSUrl;
    private List<Episode> episodes;
}
