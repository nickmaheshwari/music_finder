package edu.uchicago.nmaheshwari.vaadin.views.musiclist;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import edu.uchicago.nmaheshwari.vaadin.models.Datum;
import edu.uchicago.nmaheshwari.vaadin.service.MusicService;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainView;
import elemental.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Music List")
@Route(value = "music", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
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
        grid.addItemClickListener(new ComponentEventListener<ItemClickEvent<Datum>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<Datum> datumItemClickEvent) {
                System.out.println(datumItemClickEvent.getItem());
            }
        });



        add(grid);
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
                getMusic();
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

        Span albumName = new Span(data.getAlbum().getTitle());
        albumName.addClassName("albumName");
        Span artistName = new Span("by " + data.getArtist().getName());
        artistName.addClassName("artistName");
        header.add(albumName, artistName);

        Span id = new Span("ID: "+String.valueOf(data.getId()));


        id.addClassName("id");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");


        description.add(header, id, actions);
        card.add(image, description);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        results = new ArrayList<>();
        getMusic();

    }

    private void getMusic(){
        notification.open();
        musicService.getMusic(result -> getUI().get().access(() -> {
            results.addAll(result);

            //result.stream().forEach(System.out::println); //print data to console

            grid.setItems(results);
        }), "eminem");
    }


}
