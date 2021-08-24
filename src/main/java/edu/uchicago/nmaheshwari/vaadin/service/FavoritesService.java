package edu.uchicago.nmaheshwari.vaadin.service;


import com.vaadin.flow.component.UI;
import edu.uchicago.nmaheshwari.vaadin.models.FavoriteItem;
import edu.uchicago.nmaheshwari.vaadin.repo.FavsRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Service
public class FavoritesService extends ResponseEntityExceptionHandler {
    private FavsRepo favoritesRepository;



    public FavoritesService(FavsRepo favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }


    public void getFavoritesPaged(ResponseCallback<List<FavoriteItem>> callback,
                                  int page) {
        favoritesRepository.getFavoritesPaged(callback, page);
    }



    public void addFavorite(UI ui, ResponseCallback<FavoriteItem> callback,
                            FavoriteItem favorite)  {
        favoritesRepository.addFavorite(ui, callback, favorite);
    }

    public void deleteFavoriteById(UI ui, ResponseCallback<FavoriteItem> callback,
                                   String id) {
        favoritesRepository.deleteFavoriteById(ui, callback, id);
    }



}
