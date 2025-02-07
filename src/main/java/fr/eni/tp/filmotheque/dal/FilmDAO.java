package fr.eni.tp.filmotheque.dal;

import java.util.List;


import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Participant;

public interface FilmDAO {
	
	void create(Film film);
	
	Film read(long id);
	
	List<Film> findAll();
	
	String findTitre(long id);
	
	int countByIdGenre(long idGenre);
	
	int countByIdRealisateur(long idRealisateur);
	
	boolean validerListeActeursExiste(List<Participant> listeActeurs);

	int countByTitre(String titre);
	
	int countByIdFilm(long idFilm);

}
