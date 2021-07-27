package edu.uchicago.nmaheshwari.vaadin.service;

import edu.uchicago.nmaheshwari.vaadin.models.Datum;
import edu.uchicago.nmaheshwari.vaadin.models.MusicResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import java.util.List;

@Service
public class MusicService {
    public interface AsyncRestCallback<T> {
        void operationFinished(T results);
    }

    public void getMusic(AsyncRestCallback<List<Datum>> callback, String search, int index){
        String BASE = "https://deezerdevs-deezer.p.rapidapi.com/search?q=%s&index=%d";
        String key = "2db3ab5ca4mshb0352d143ae2b94p121f9fjsn66ee38aae7d8";
        String host = "deezerdevs-deezer.p.rapidapi.co";
        String formatted = String.format(BASE, search, index);
        RequestHeadersSpec<?> spec = WebClient.create().get().uri(formatted);
        spec.header("x-rapidapi-host", host);
        spec.header("x-rapidapi-key", key);

        spec.retrieve().toEntity(MusicResponse.class).subscribe(result -> {
            final MusicResponse musicResponse = result.getBody();

            System.out.println("Total Results: " +musicResponse.getTotal());
            System.out.println(musicResponse.getData().size());
            if(null==musicResponse.getData()){
                System.out.println("No results");
                return;
            }


            callback.operationFinished(musicResponse.getData());
        });
    }
}
