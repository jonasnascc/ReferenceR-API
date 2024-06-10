package be.wanna.Referencerback.dto;

public record AuthorProfileDTO(
    String iconUrl,
    String userTagline,
    String userName,
    Integer deviations,
    Integer watchers,
    Integer watching,
    Integer pageviews,
    Integer favourites

) {}
