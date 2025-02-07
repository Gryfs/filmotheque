package fr.eni.tp.filmotheque.bll;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.dal.AvisDAO;
import fr.eni.tp.filmotheque.dal.FilmDAO;
import fr.eni.tp.filmotheque.dal.GenreDAO;
import fr.eni.tp.filmotheque.dal.MembreDAO;
import fr.eni.tp.filmotheque.dal.ParticipantDAO;
import fr.eni.tp.filmotheque.dal.exception.BusinessException;

@Service
@Primary
public class FilmServiceImpl implements FilmService {

	private FilmDAO filmDAO;
	private GenreDAO genreDAO;
	private ParticipantDAO participantDAO;
	private AvisDAO avisDAO;
	private MembreDAO membreDAO;

	public FilmServiceImpl(FilmDAO filmDAO, GenreDAO genreDAO, ParticipantDAO participantDAO, AvisDAO avisDAO,
			MembreDAO membreDAO) {
		this.filmDAO = filmDAO;
		this.genreDAO = genreDAO;
		this.participantDAO = participantDAO;
		this.avisDAO = avisDAO;
		this.membreDAO = membreDAO;
	}

	@Override
	public List<Film> consulterFilms() {
		List<Film> listeFilm = filmDAO.findAll();
		for (Film film : listeFilm) {
			film.setGenre(genreDAO.read(film.getGenre().getId()));
			film.setRealisateur(participantDAO.read(film.getRealisateur().getId()));

		}

		return listeFilm;
	}

	@Override
	public Film consulterFilmParId(long id) {
		Film film = filmDAO.read(id);
		film.setGenre(genreDAO.read(film.getGenre().getId()));
		film.setRealisateur(participantDAO.read(film.getRealisateur().getId()));

		List<Participant> acteursDuFilm = participantDAO.findActeurs(id);
		film.setActeurs(acteursDuFilm);
		for (Participant participant : acteursDuFilm) {
			participant = participantDAO.read(participant.getId());
		}

		List<Avis> avisDuFilm = avisDAO.findByFilm(id);
		film.setAvis(avisDuFilm);
		for (Avis avis : avisDuFilm) {
			avis.setMembre(membreDAO.read(avis.getMembre().getId()));
		}

		return film;
	}

	@Override
	public List<Genre> consulterGenres() {

		return genreDAO.findAll();
	}

	@Override
	public List<Participant> consulterParticipants() {

		return participantDAO.findAll();
	}

	@Override
	public Genre consulterGenreParId(long id) {

		return genreDAO.read(id);
	}

	@Override
	public Participant consulterParticipantParId(long id) {

		return participantDAO.read(id);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creerFilm(Film film) throws BusinessException {

		BusinessException be = new BusinessException();
		boolean valide = validerGenreInexistant(film.getGenre().getId(), be);
		valide &= validerRealisateurInexistant(film.getRealisateur().getId(), be);
		valide &= validerListeActeurs(film.getActeurs(), be);
		valide &= validerTitreInexistant(film.getTitre(), be);

		try {
			if (valide) {

				film.setGenre(genreDAO.read(film.getGenre().getId()));
				film.setRealisateur(participantDAO.read(film.getRealisateur().getId()));

				List<Participant> acteursDufilm = new ArrayList<>();

				for (Participant acteur : film.getActeurs()) {
					Participant acteurFromDB = participantDAO.read(acteur.getId());
					acteursDufilm.add(acteurFromDB);

				}
				film.setActeurs(acteursDufilm);

				filmDAO.create(film);

			} else {
				throw be;
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw be;
		}

	}

	@Override
	public String consulterTitreFilm(long id) {

		return filmDAO.findTitre(id);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void publierAvis(Avis avis, long idFilm) throws BusinessException{
		BusinessException be = new BusinessException();
		boolean valide = validationNote(avis.getNote(), be);
		valide &= validationCommentaire(avis.getCommentaire(), be);
		valide &= validationFilm(idFilm, be);
		try {
			if(valide) {
				avisDAO.create(avis, idFilm);
			}
			else {
				throw be;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw be;
		}
		

	}

	@Override
	public List<Avis> consulterAvis(long idFilm) {

		return avisDAO.findByFilm(idFilm);
	}

	private boolean validationNote(int noteAvis, BusinessException be) {
		boolean valide = true;
		if(noteAvis < 0 || noteAvis > 5) {
			valide = false;
			be.addCleErreur("validation.avis.note");
		}
		
		return valide;
		

	}
	
	private boolean validationCommentaire(String commentaire, BusinessException be) {
		boolean valide = true;
				if(commentaire.length() < 1 || commentaire.length() > 250) {
					valide = false;
					be.addCleErreur("validation.avis.commenataire");
				}
		return valide;
	}
	
	private boolean validationFilm(long idFilm, BusinessException be) {
		boolean valide = true;
		int nbFilm = filmDAO.countByIdFilm(idFilm);
		if (nbFilm == 0) {
			valide = false;
			be.addCleErreur("validation.avis.film");
		}

		return valide;
	}

	private boolean validerGenreInexistant(long idGenre, BusinessException be) {
		boolean valide = true;

		int nbGenre = filmDAO.countByIdGenre(idGenre);
		if (nbGenre == 0) {
			valide = false;
			be.addCleErreur("validation.film.genre");
		}

		return valide;
	}

	private boolean validerRealisateurInexistant(long idRealisateur, BusinessException be) {
		boolean valide = true;

		int nbGenre = filmDAO.countByIdRealisateur(idRealisateur);
		if (nbGenre == 0) {
			valide = false;
			be.addCleErreur("validation.film.realisateur");

		}

		return valide;
	}

	private boolean validerListeActeurs(List<Participant> listeActeurs, BusinessException be) {

		boolean listeActeursValide = filmDAO.validerListeActeursExiste(listeActeurs);

		if (!listeActeursValide) {
			be.addCleErreur("validation.film.listeActeurs");
		}

		return listeActeursValide;
	}

	private boolean validerTitreInexistant(String titre, BusinessException be) {
		boolean valide = true;

		int nbTitre = filmDAO.countByTitre(titre);
		if (nbTitre != 0) {
			valide = false;
			be.addCleErreur("validation.film.titre");

		}

		return valide;
	}

}
