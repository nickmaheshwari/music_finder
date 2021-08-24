package edu.uchicago.nmaheshwari.vaadin.views.favorites;


import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.uchicago.nmaheshwari.vaadin.cache.Cache;
import edu.uchicago.nmaheshwari.vaadin.models.FavoriteItem;
import edu.uchicago.nmaheshwari.vaadin.service.FavoritesService;
import edu.uchicago.nmaheshwari.vaadin.utils.Utils;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainView;
import edu.uchicago.nmaheshwari.vaadin.views.shared.SharedViews;

import java.util.ArrayList;
import java.util.List;

@Route(value = "favorites", layout = MainView.class)
@PageTitle("Favorites")
@CssImport("./views/generic-list.css")
public class FavoritesView extends Div implements AfterNavigationObserver {
    public static int MAX_RESULTS = 20;
    private FavoritesService favoritesService;
    private Grid<FavoriteItem> grid = new Grid<>();
    private int page;
    private boolean isLoading = false;
    private boolean isEnd = false;

    //no need to cache these. We will simply refetch them each time the user returns to this page
    private List<FavoriteItem> favoriteItems = new ArrayList<>();

    public FavoritesView(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;

        addClassName("generic-list");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(favoriteItem -> SharedViews.getCard(favoriteItem, true));
        grid.addItemClickListener(
                event -> grid.getUI().ifPresent(ui -> {

                            Cache.getInstance().setDetailItem(event.getItem());
                            Cache.getInstance().setFavMode(true);
                            ui.navigate("detail-view");

                        }
                ));

        add(withClientsideScrollListener(grid));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent navigationEvent) {
        page = 1;
        getFavoritesPaged();
    }

    private void getFavoritesPaged() {
        if (isEnd) return;

        isLoading = true;
        favoritesService.getFavoritesPaged(favoriteResponse -> {
            getUI().get().access(() -> {

                int size = favoriteResponse.size();
                switch (size){
                    case 0:
                        isLoading = false;
                        isEnd = true;
                        return;
                    case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 12
                            : case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                        addItemsToGrid(favoriteResponse, size);
                        isEnd = true;
                        break;
                    case 20:
                    default:
                        addItemsToGrid(favoriteResponse, size);
                        page++;
                        break;

                }
                isLoading = false;
                getUI().get().push();
            });
        }, page);
    }

    private void addItemsToGrid(List<FavoriteItem> favoriteResponse, int size) {
        favoriteItems.addAll(favoriteResponse);
        grid.setItems(favoriteItems.stream());
        if (page > 1) {
            new Notification("Loading page " + page + " with " + size + (size == 1 ? " song." : " songs."),
                    1000,
                    Notification.Position.BOTTOM_CENTER).open();
        }
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
            getFavoritesPaged();
        }
    }



}