package com.javaunit3.springmvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BestMovieService {

    private final Movie bestMovie;

    @Autowired
    public BestMovieService(@Qualifier("titanicMovie") Movie bestMovie) {
        this.bestMovie = bestMovie;
    }

    public Movie getBestMovie() {
        return bestMovie;
    }
}