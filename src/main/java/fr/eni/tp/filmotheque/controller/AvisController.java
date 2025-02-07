package fr.eni.tp.filmotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dal.exception.BusinessException;

@Controller
@SessionAttributes({"membreSession"})
public class AvisController {
	
	private FilmService filmService;
	
	public AvisController(FilmService filmService) {
		this.filmService = filmService;
	}
	
	// Création d'un nouvel avis
	@GetMapping("/avis")
	public String creerAvis(@RequestParam(name = "idFilm") long idFilm, Model model,
	@ModelAttribute("membreSession") Membre membreEnSession) {
		if (membreEnSession != null && membreEnSession.getId() >= 1) {
			// Il y a un membre en session
			System.out.println(membreEnSession.getId());
			Film f = this.filmService.consulterFilmParId(idFilm);
			
			if(f != null) {
				model.addAttribute("film", f);
				
				Avis avis = new Avis();
				model.addAttribute(avis);
				return "avis";
			}
		}	
		return "redirect:/films";
			
	}
	
	
	@PostMapping("/avis")
	public String creerAvis(@ModelAttribute(name = "membreSession") Membre membreEnSession, @ModelAttribute(name = "avis") Avis avis, @RequestParam(name = "idFilm") long idFilm, BindingResult bindingResult, Model model) {
		System.out.println(membreEnSession);
		if (membreEnSession != null && membreEnSession.getId() >= 1) {
			
			avis.setMembre(membreEnSession);
			System.out.println(avis);
			
			try {
				filmService.publierAvis(avis, idFilm);
				return "redirect:/films";
			} catch (BusinessException e) {
				e.printStackTrace();
				e.getClesErreurs().forEach(cle -> {
					ObjectError error = new ObjectError("globalError", cle);
					bindingResult.addError(error);
					
					Film f = this.filmService.consulterFilmParId(idFilm);
					model.addAttribute("film", f);
				});
				return "avis";
			}
		}
		// Redirection vers la liste des films :
		return "redirect:/films";

	}
	
	

}
