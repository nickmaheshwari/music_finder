package edu.uchicago.nmaheshwari.vaadin.models;

import java.io.Serializable;

public class FavoriteItem implements Serializable {

    private String id;
    private String userEmail;
    private String title;
    private String artist;
    private String album;
    private String cover;
    private String rank;
    private String link;
    private String duration;

    //factory method
    public static FavoriteItem fromItem(Datum datum, String  userEmail){
        FavoriteItem favoriteItem = new FavoriteItem();

        favoriteItem.setUserEmail(userEmail);
        favoriteItem.setArtist(datum.getArtist().getName());
        favoriteItem.setId(null == datum.getId() ? "": String.valueOf(datum.getId()));
        favoriteItem.setTitle(datum.getTitle());
        favoriteItem.setCover(datum.getAlbum().getCover());
        favoriteItem.setDuration(datum.getDuration() + "s");
        favoriteItem.setRank(String.valueOf(datum.getRank()));
        favoriteItem.setAlbum(datum.getAlbum().getTitle());
        favoriteItem.setLink(datum.getLink());

        return favoriteItem;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

}
