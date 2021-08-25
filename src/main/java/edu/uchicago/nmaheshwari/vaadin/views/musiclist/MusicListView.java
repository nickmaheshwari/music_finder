package edu.uchicago.nmaheshwari.vaadin.views.musiclist;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import edu.uchicago.nmaheshwari.vaadin.cache.Cache;
import edu.uchicago.nmaheshwari.vaadin.models.FavoriteItem;
import edu.uchicago.nmaheshwari.vaadin.service.MusicService;
import edu.uchicago.nmaheshwari.vaadin.utils.Utils;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainView;
import edu.uchicago.nmaheshwari.vaadin.views.shared.SharedViews;

import java.util.stream.Collectors;

@PageTitle("Music List")
@Route(value = "music", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./views/cardlist/card-list-view.css")
public class MusicListView extends Div implements AfterNavigationObserver {

    private MusicService musicService;
    private Grid<FavoriteItem> grid = new Grid<>();
    private boolean isLoading = false;
    private TextField keyWord;
    private Notification loading = new Notification("Loading...", 1000, Notification.Position.BOTTOM_CENTER);

    public MusicListView(MusicService musicService) {
        this.musicService = musicService;

        keyWord = new TextField();
        keyWord.setLabel("Search Term");
        keyWord.setPlaceholder("Enter the name of a song or artist you want to hear and press 'Enter' to getting listening!");
        keyWord.setAutofocus(true);
        keyWord.addKeyDownListener(keyDownEvent -> {
                    String keyStroke = keyDownEvent.getKey().getKeys().toString();
                    if (keyStroke.equals("[Enter]") && !isLoading && !keyWord.getValue().equals("")) {
                        Cache.getInstance().setKeyword(keyWord.getValue());
                        executeSearch(Cache.getInstance().getKeyword());
                    }
                }
        );

        addClassName("generic-list");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(favoriteItem -> SharedViews.getCard(favoriteItem, false));
        grid.addItemClickListener(
                event -> grid.getUI().ifPresent(ui -> {

                            Cache.getInstance().setDetailItem(event.getItem());
                            Cache.getInstance().setFavMode(false);
                            ui.navigate("detail-view");

                        }
                ));

        add(keyWord, withClientsideScrollListener(grid));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        if (Cache.getInstance().itemsSize() > 0)
            grid.setItems(Cache.getInstance().streamItems());

        keyWord.setValue(Cache.getInstance().getKeyword());

    }

    private void getPlaces(String searchTerm) {

        if (null == searchTerm || searchTerm.equals("")) return;

        isLoading = true;
        musicService.getMusic(volResp -> {
            getUI().get().access(() -> {

                Cache.getInstance().addItems(volResp.getData()
                        .stream()
                        .map( item -> FavoriteItem.fromItem(item, Cache.getInstance().getEmail()))
                        .collect(Collectors.toList())
                );
                grid.setItems(Cache.getInstance().streamItems());
                Cache.getInstance().setOffset(Cache.getInstance().getOffset() + 1);
                isLoading = false;
                getUI().get().push();

            });
        }, searchTerm, Cache.getInstance().getOffset());
    }


    private Grid<FavoriteItem> withClientsideScrollListener(Grid<FavoriteItem> grid) {
        grid.getElement().executeJs(
                Utils.getFileFromResourceAsString(this.getClass(), "scrollFunction.js"),
                getElement());
        return grid;
    }

    @ClientCallable
    public void onGridEnd() {

        if (!isLoading) {
            System.out.println("Paging...");
            loading.open();
            getPlaces(Cache.getInstance().getKeyword());
        }

    }

    public void executeSearch(String searchFor) {

        Cache.getInstance().setKeyword(searchFor);
        Cache.getInstance().setOffset(0);
        Cache.getInstance().clearItems();
        getPlaces(searchFor);
    }

}
