package edu.uchicago.nmaheshwari.vaadin.views.musiclist;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import edu.uchicago.nmaheshwari.vaadin.models.Datum;
import edu.uchicago.nmaheshwari.vaadin.service.MusicService;
import edu.uchicago.nmaheshwari.vaadin.views.MainLayout;

import java.util.Arrays;
import java.util.List;

@PageTitle("Music List")
@Route(value = "music", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class MusicListView extends Div implements AfterNavigationObserver {

    private MusicService musicService;
    Grid<Datum> grid = new Grid<>();
    private boolean isLoading = false;
    private List<Datum> results;

    private Notification notification = new Notification("Loading...", 500, Notification.Position.TOP_CENTER);


    public MusicListView(MusicService musicService) {
        this.musicService = musicService;
        addClassName("music-list-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(data -> createCard(data));
        add(grid);
    }

    private HorizontalLayout createCard(Datum data) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Image image = new Image();
        image.setSrc(data.getAlbum().getCover());
        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span albumName = new Span(data.getAlbum().getTitle());
        albumName.addClassName("albumName");
        Span artistName = new Span(data.getArtist().getName());
        artistName.addClassName("artistName");
        header.add(albumName, artistName);

        Span explicit = new Span(String.valueOf(data.getExplicitLyrics()));
        explicit.addClassName("explicit");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");


        description.add(header, explicit, actions);
        card.add(image, description);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        List<Datum> data = Arrays.asList();
        getMusic();
        grid.setItems(data);

    }

    private void getMusic(){
        notification.open();
        musicService.getMusic(result -> getUI().get().access(() -> {
            result.stream()
                    .forEach(System.out::println);
        }), "eminem");
    }


}
