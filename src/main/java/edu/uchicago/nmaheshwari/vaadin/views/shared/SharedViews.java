package edu.uchicago.nmaheshwari.vaadin.views.shared;


import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import edu.uchicago.nmaheshwari.vaadin.models.FavoriteItem;

@CssImport("./views/shared-views.css")
public class SharedViews {

    public static VerticalLayout getDetail(FavoriteItem favorite, boolean favMode) {
        VerticalLayout detail = new VerticalLayout();
        detail.setSpacing(false);
        detail.setPadding(false);

        detail.addClassName("detail");
        Div tab = new Div();
        tab.addClassName("tab");

        Image image = new Image();

        if(favorite ==null){
            image.setSrc("https://picsum.photos/200/300");
        }else{
            image.setSrc(null == favorite.getCover() ? "https://picsum.photos/200/300" : favorite.getCover());
        }

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addClassName("vertical-layout");


        Span songName = new Span("Song: " + favorite.getTitle());
        songName.addClassName("songName");

        Span albumName = new Span("Album: "+favorite.getAlbum());
        albumName.addClassName("albumName");

        Span artistName = new Span("Performed by " + favorite.getArtist());
        artistName.addClassName("artistName");

        Span duration = new Span("Duration: "+favorite.getDuration() + "s");
        duration.addClassName("duration");

        Span link = new Span("Link: "+ favorite.getLink());
        link.addClassName("link");

        Span rank = new Span("Deezer Rank: "+favorite.getRank());
        rank.addClassName("rank");

        Span email = new Span("Song Favorited By: "+favorite.getUserEmail());
        email.addClassName("email");

        if (favMode) {
            detail.addClassName("fav-mode");
            verticalLayout.add(songName, albumName, artistName, duration, link, rank, email);
        } else {
            verticalLayout.add(songName, albumName, artistName, duration, link, rank);
        }
        detail.add(tab, image, verticalLayout);

        return detail;
    }


    public static HorizontalLayout getCard(FavoriteItem favorite, boolean favMode) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.setPadding(false);

        Div tab = new Div();
        tab.addClassName("tab");
        Image image = new Image();


        image.setSrc(null == favorite.getCover() ? "https://picsum.photos/200/300" : favorite.getCover());
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addClassName("vertical-layout");
        verticalLayout.setSpacing(false);
        verticalLayout.setPadding(false);

        Span songName = new Span("Song: " + favorite.getTitle());
        songName.addClassName("songName");

        Span albumName = new Span("Album: "+favorite.getAlbum());
        albumName.addClassName("albumName");

        Span artistName = new Span("Performed by " + favorite.getArtist());
        artistName.addClassName("artistName");

        Span duration = new Span("Duration: "+favorite.getDuration() + "s");
        duration.addClassName("duration");

        Span link = new Span("Link: "+ favorite.getLink());
        link.addClassName("link");

        Span rank = new Span("Deezer Rank: "+favorite.getRank());
        rank.addClassName("rank");

        Span email = new Span("Song Favorited By: "+favorite.getUserEmail());
        email.addClassName("email");


        if (favMode) {
            verticalLayout.add(songName, albumName, artistName, duration, email);
            card.add(tab, image, verticalLayout);
        } else {
            verticalLayout.add(songName, albumName, artistName, duration);
            card.add(image, verticalLayout);
        }

        return card;
    }


}