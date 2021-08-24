package edu.uchicago.nmaheshwari.vaadin.repo;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import edu.uchicago.nmaheshwari.vaadin.cache.Cache;
import edu.uchicago.nmaheshwari.vaadin.models.FavoriteItem;
import edu.uchicago.nmaheshwari.vaadin.service.ResponseCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Repository
public class FavsRepo {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    //make sure to add your full container-url-connection-string to the IntelliJ confuguration
    //QUARKUS_BASE_URL :: https://container-service-22.9r4o895c5nvr8.us-east-2.cs.amazonlightsail.com/
    //If you are deploying to beanstalk, place in software || environmental properties.
    @Value("${QUARKUS_BASE_URL:#{'localhost:8888/'}}")
    private String baseUrl;

    public void getFavoritesPaged(ResponseCallback<List<FavoriteItem>> callback, int page) {

        String email = Cache.getInstance().getEmail();

        String raw = baseUrl + "/wish/paged/" + email + "/%d";
        String formatted = String.format(raw, page);
        WebClient.RequestHeadersSpec<?> spec = WebClient.create().get()
                .uri(formatted);

        spec.retrieve().bodyToMono(new ParameterizedTypeReference<List<FavoriteItem>>() {
        }).publishOn(Schedulers.fromExecutor(executorService)).subscribe(results -> callback.operationFinished(results));
    }


    public void deleteFavoriteById(UI ui, ResponseCallback<FavoriteItem> callback, String id) {

        String email = Cache.getInstance().getEmail();
        String raw = baseUrl + "/wish/" + email + "/%s";
        String formatted = String.format(raw, id);
        Mono<FavoriteItem> mono = WebClient.create().delete()
                .uri(formatted)
                .retrieve()
                .bodyToMono(FavoriteItem.class);

        mono
                .doOnError(throwable -> ui.access(() -> {
                    Notification.show("Unable to delete favorite: " + throwable.getMessage(), 2000,
                            Notification.Position.BOTTOM_CENTER);
                    ui.navigate("favorites");
                }))
                .publishOn(Schedulers.fromExecutor(executorService))
                .subscribe(results -> callback.operationFinished(results));

    }


    public void addFavorite(UI ui, ResponseCallback<FavoriteItem> callback, FavoriteItem favoriteAdd) {

        String formatted = baseUrl + "/wish";
        Mono<FavoriteItem> mono = WebClient.create().post()

                .uri(formatted)
                .body(Mono.just(favoriteAdd), FavoriteItem.class)
                .retrieve()
                .bodyToMono(FavoriteItem.class);

        mono
                .doOnError(throwable -> {
                    String message = "";
                    switch (((WebClientResponseException.UnsupportedMediaType) throwable).getStatusCode().value()){
                        case 415:
                            message = "This song is already in your favorites.";
                            break;
                        default:
                            message = "There was an error: " + throwable.getMessage();

                    }
                    final String finalMessage = message;
                    ui.access(() -> {
                        Notification.show(finalMessage , 2000,
                                Notification.Position.BOTTOM_CENTER);
                        ui.navigate("favorites");
                    });

                })
                .publishOn(Schedulers.fromExecutor(executorService))
                .subscribe(results -> callback.operationFinished(results));

    }



}