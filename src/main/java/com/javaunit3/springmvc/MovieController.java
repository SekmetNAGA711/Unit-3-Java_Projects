package com.javaunit3.springmvc;


import com.javaunit3.springmvc.model.MovieEntity;
import com.javaunit3.springmvc.model.VoteEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Controller
public class MovieController {

    private final BestMovieService bestMovieService;





    public MovieController(BestMovieService bestMovieService, SessionFactory sessionFactory) {
        this.bestMovieService = bestMovieService;
        this.sessionFactory = sessionFactory;
    }

    @RequestMapping("/")
    public String getIndexPage()
    {
        return "index";
    }

    @RequestMapping("/bestMovie")
    public String getBestMoviePage(Model model) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();
        movieEntityList.sort(Comparator.comparing(movieEntity -> movieEntity.getVotes().size()));

        MovieEntity movieWithMostVotes = movieEntityList.get(movieEntityList.size() - 1);
        List<String> voterNames = new ArrayList<>();

        for (Object vote: movieWithMostVotes.getVotes())
        {
            voterNames.add(String.valueOf(vote.getClass()));
        }

        String voterNamesList = String.join(",", voterNames);

        model.addAttribute("bestMovie", movieWithMostVotes.getTitle());
        model.addAttribute("bestMovieVoters", voterNamesList);

        session.getTransaction().commit();

        return "bestMovie";
    }



    @RequestMapping("/voteForBestMovie")
    public String voteForBestMovie(HttpServletRequest request, Model model)
    {
        String movieId = request.getParameter("movieId");
        String voterName = request.getParameter("voterName");

        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        MovieEntity movieEntity = (MovieEntity) session.get(MovieEntity.class, Integer.parseInt(movieId));
        VoteEntity newVote = new VoteEntity();
        newVote.setVoterName(voterName);
        movieEntity.getVotes().add(newVote);

        session.update(movieEntity);

        session.getTransaction().commit();

        return "voteForBestMovie";
    }

    @RequestMapping("/voteForBestMovieForm")
    public String voteForBestMovieFormPage(Model model) {
        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        List<MovieEntity> movieEntityList = session.createQuery("from MovieEntity").list();

        session.getTransaction().commit();

        model.addAttribute("movies", movieEntityList);

        return "voteForBestMovie";
    }


    @RequestMapping("/addMovie")
    public String addMovie(HttpServletRequest request, Model model) {
        String title = request.getParameter("title");
        String maturityRating = request.getParameter("maturityRating");
        String genre = request.getParameter("genre");

        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        MovieEntity newMovie = new MovieEntity();
        newMovie.setTitle(title);
        newMovie.setMaturityRating(maturityRating);
        newMovie.setGenre(genre);

        // Save the new movie to the database
        session.save(newMovie);

        session.getTransaction().commit();

        // Redirect to the home page after adding the movie
        return "redirect:/";
    }



    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping("/addMovieForm")
    public String addMovieForm() {
        return "addMovie";
    }
}