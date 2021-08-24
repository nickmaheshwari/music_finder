package edu.uchicago.nmaheshwari.vaadin.views.details;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.uchicago.nmaheshwari.vaadin.cache.Cache;
import edu.uchicago.nmaheshwari.vaadin.models.FavoriteItem;
import edu.uchicago.nmaheshwari.vaadin.service.FavoritesService;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainView;
import edu.uchicago.nmaheshwari.vaadin.views.shared.SharedViews;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Route(value = "detail-view", layout = MainView.class)
@PageTitle("Music Detail")
@CssImport("./views/detail.css")
public class DetailView extends Div {

    private Button favoriteAction = new Button();
    private Button goToLink = new Button();
    private Button goBack = new Button();
    private FavoritesService favoritesService;

    private Notification noticeAdd = new Notification("Favorite ADDED", 1000, Notification.Position.BOTTOM_CENTER);
    private Notification noticeDeleted = new Notification("Favorite DELETED", 1000,
            Notification.Position.BOTTOM_CENTER);


    public DetailView(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;

        addClassName("detail-wrap");
        add(SharedViews.getDetail(Cache.getInstance().getDetailItem(), Cache.getInstance().isFavMode()));
        add(createButtonLayout());

        favoriteAction.setText(Cache.getInstance().isFavMode() ? "Delete From Favorites" : "Add to Favorites");
        favoriteAction.setClassName(Cache.getInstance().isFavMode() ? "red-button" : "green-button");
        favoriteAction.addClickListener(
                e -> favoriteAction.getUI().ifPresent(ui -> {
                    if (Cache.getInstance().isFavMode())
                        deleteFavorite(Cache.getInstance().getDetailItem().getId());
                    else
                        addFavorite(Cache.getInstance().getDetailItem());
                }
        ));

        goBack.setText(Cache.getInstance().isFavMode() ? "Return to Favorites" : "Return to Music Search");
        goBack.addClickListener(
                e -> goBack.getUI().ifPresent(ui -> {
                            if (Cache.getInstance().isFavMode())
                                ui.navigate("favorites");
                            else
                                ui.navigate("music");
                        }
                ));

        /*goToLink.setText("Go to Song's Deezer Page");
        goToLink.addClickListener(
                e -> goToLink.getUI().ifPresent(ui -> {
                    URI uri = null;
                    try {
                        uri = new URL(Cache.getInstance().getDetailItem().getLink()).toURI();
                    } catch (URISyntaxException uriSyntaxException) {
                        uriSyntaxException.printStackTrace();
                    } catch (MalformedURLException malformedURLException) {
                        malformedURLException.printStackTrace();
                    }
                    openWebpage(uri);
                }));*/
    }

    public void addFavorite(FavoriteItem favorite) {
        favoritesService.addFavorite(UI.getCurrent(), favoriteAdd -> {
            getUI().get().access(() -> {
                noticeAdd.open();
                getUI().get().navigate("favorites");
            });
        }, favorite);

    }

    public void deleteFavorite(String id) {
        favoritesService.deleteFavoriteById(UI.getCurrent(), favoriteDelete -> {
            getUI().get().access(() -> {
                noticeDeleted.open();
                getUI().get().navigate("favorites");
            });
        }, id);

    }


    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        favoriteAction.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(favoriteAction);
      //  buttonLayout.add(goToLink);
        buttonLayout.add(goBack);
        return buttonLayout;
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

}
