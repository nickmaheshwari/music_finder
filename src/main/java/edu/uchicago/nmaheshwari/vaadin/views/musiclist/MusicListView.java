package edu.uchicago.nmaheshwari.vaadin.views.musiclist;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import edu.uchicago.nmaheshwari.vaadin.models.Datum;
import edu.uchicago.nmaheshwari.vaadin.service.MusicService;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainLayout;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Music List")
@Route(value = "music", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@CssImport("./views/cardlist/card-list-view.css")
public class MusicListView extends Div implements AfterNavigationObserver {

    private MusicService musicService;
    Grid<Datum> grid = new Grid<>();
    private boolean isLoading = false;
    private List<Datum> results;
    private int index = 25; //fetch 25 results at a time

    private Notification loading = new Notification("Loading...", 500, Notification.Position.MIDDLE);
    private Notification done = new Notification("Done Loading!", 1500, Notification.Position.BOTTOM_CENTER);

    private TextField searchField;
    private String searchTerm;

    public MusicListView(MusicService musicService) {
        this.musicService = musicService;
        searchField = new TextField();
        searchField.setLabel("Search term");
        searchField.setPlaceholder("search...");
        searchField.setAutofocus(true);
        searchField.setWidthFull();
        searchField.addKeyDownListener(keyDownEvent -> {
            String keyStroke = keyDownEvent.getKey().getKeys().toString();
            if(keyStroke.equals("[Enter]") && !isLoading){
                System.out.println("Search term:  "+ searchField.getValue());
                searchTerm = searchField.getValue();
                index =25;
                results.clear();
                getMusic(searchTerm);
            }
        });

        addClassName("music-list-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(data -> createCard(data));
        grid.addItemClickListener(new ComponentEventListener<ItemClickEvent<Datum>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<Datum> datumItemClickEvent) {
                System.out.println(datumItemClickEvent.getItem());
            }
        });

        add(searchField, withClientsideScrollListener(grid));
    }

    private Grid<Datum> withClientsideScrollListener(Grid<Datum> grid) {
        grid.getElement().executeJs(
                "this.$.scroller.addEventListener('scroll', (scrollEvent) => " +
                        "{requestAnimationFrame(" +
                        "() => $0.$server.onGridScroll({sh: this.$.table.scrollHeight, " +
                        "ch: this.$.table.clientHeight, " +
                        "st: this.$.table.scrollTop}))},true)",
                getElement());
        return grid;
    }

    @ClientCallable
    public void onGridScroll(JsonObject scrollEvent) {
        int scrollHeight = (int) scrollEvent.getNumber("sh");
        int clientHeight = (int) scrollEvent.getNumber("ch");
        int scrollTop = (int) scrollEvent.getNumber("st");

        double percentage = (double) scrollTop / (scrollHeight - clientHeight);
        //reached the absolute bottom of the scroll
        if (percentage == 1.0) {

            if (!isLoading) {
                getMusic(searchTerm);
            }
            grid.scrollToIndex(results.size() / 2);

        }

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

        Span songName = new Span("Song: " + data.getTitle());
        songName.addClassName("songName");
        Span albumName = new Span("|  Album: "+data.getAlbum().getTitle());
        albumName.addClassName("albumName");
        header.add(songName, albumName);

        Span artistName = new Span("Performed by " + data.getArtist().getName());
        artistName.addClassName("artistName");


        Span duration = new Span("Duration: "+data.getDuration() + "s");
        duration.addClassName("duration");

        Span link = new Span("Link: "+ data.getLink());
        duration.addClassName("link");

        Span rank = new Span("Deezer Rank: "+data.getRank());
        duration.addClassName("rank");


        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");


        description.add(header, artistName, link, duration, rank, actions);
        card.add(image, description);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        results = new ArrayList<>();
        //getMusic(searchTerm);

    }

    private void getMusic(String searchTerm){
        isLoading = true;
        loading.open();

        musicService.getMusic(result -> getUI().get().access(() -> {
            results.addAll(result);
            //result.stream().forEach(System.out::println); //print data to console
            grid.setItems(results);
            done.open();
            index = index +25; //increment count by 25
            isLoading = false;
            getUI().get().push();

        }), searchTerm, index);
    }


}
