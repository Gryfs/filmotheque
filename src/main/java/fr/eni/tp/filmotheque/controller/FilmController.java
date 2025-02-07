package fr.eni.tp.filmotheque.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.ContexteService;
import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.dal.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({ "genreSession", "participantSession", "membreSession" })
public class FilmController {

	private FilmService filmService;

	public FilmController(FilmService filmService, ContexteService contexteService) {
		this.filmService = filmService;
	}

	@GetMapping("films")
	public String afficherFilms(Model model) {
		System.out.println("Affiche les films : ");
		List<Film> listeFilms = filmService.consulterFilms();

		model.addAttribute("films", listeFilms);

		return "films";
	}

	@GetMapping("/index")
	public String retourIndex() {
		return "index";
	}

	@GetMapping("/creer")
	public String afficherFormulaireCreation(Model model) {
		model.addAttribute("film", new Film());

		return "creer";
	}

	@GetMapping("/indexMembre")
	public String retourIndexAvecMembre(@ModelAttribute("membreSession") Membre membre, Model model) {
		return "index";
	}

	@GetMapping("/detail")
	public String afficherDetail(@RequestParam(name = "id") int idFilm, Model model) {

		Film film = filmService.consulterFilmParId(idFilm);

		model.addAttribute("film", film);

		return "detail";
	}

	@PostMapping("/choisirGenre")
	public String choisirGenre(@RequestParam("idGenre") long idGenre) {

		System.out.println("idCours = " + idGenre);

		return "redirect:/creer";
	}

	@PostMapping("/creer")
	public String creerFilm(@Valid @ModelAttribute("film") Film film, BindingResult bindingResult) {

		System.out.println("creerfilm = " + film);
		if (!bindingResult.hasErrors()) {
			try {
				this.filmService.creerFilm(film);
				return "redirect:/films";
			} catch (BusinessException e) {

				e.printStackTrace();
				e.getClesErreurs().forEach(cle -> {
					ObjectError error = new ObjectError("globalError", cle);
					bindingResult.addError(error);
				});
				return "creer";
			}

		} else {
			return "creer";
		}

	}

	@ModelAttribute("genreSession")
	public List<Genre> chargerGenreEnSession() {
		System.out.println("Chargement des Genres");
		return this.filmService.consulterGenres();
	}

	@ModelAttribute("participantSession")
	public List<Participant> chargerParticipantEnSession() {
		System.out.println("Chargement des Participants");
		return this.filmService.consulterParticipants();
	}

}
