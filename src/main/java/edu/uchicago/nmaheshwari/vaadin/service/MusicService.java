package edu.uchicago.nmaheshwari.vaadin.service;

import edu.uchicago.nmaheshwari.vaadin.models.MusicResponse;
import edu.uchicago.nmaheshwari.vaadin.repo.MusicRepo;
import org.springframework.stereotype.Service;

@Service
public class MusicService {

    private MusicRepo musicRepo;

    public MusicService(MusicRepo musicRepo) {
        this.musicRepo = musicRepo;
    }


    public void getMusic(ResponseCallback<MusicResponse> callback, String search, int index){
        musicRepo.getMusic(callback, search, index);
    }
}
