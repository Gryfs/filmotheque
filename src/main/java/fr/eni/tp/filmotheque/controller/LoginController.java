package fr.eni.tp.filmotheque.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import fr.eni.tp.filmotheque.bll.ContexteService;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dal.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({"membreSession"})
public class LoginController {
	
	private ContexteService contexteService;

	public LoginController(ContexteService contexteService) {
		this.contexteService = contexteService;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("membre", new Membre());
		
		return "register";
	}
	
	
	@PostMapping("/register")
	public String registerAccount(@Valid @ModelAttribute("membre") Membre membre, BindingResult bindingResult) {

		System.out.println("creerMembre = " + membre);
		if (!bindingResult.hasErrors()) {
			try {
				contexteService.creerMembre(membre);
				return "redirect:/login";
			} catch (BusinessException e) {

				e.printStackTrace();
				e.getClesErreurs().forEach(cle -> {
					ObjectError error = new ObjectError("globalError", cle);
					bindingResult.addError(error);
				});
				return "register";
			}

		} else {
			return "register";
		}

	}
	
	
	
	
	@GetMapping("/session")
	public String connecterMembre(Principal principal, Model model) {
		
		
	    String email = principal.getName();
	    System.out.println("Utilisateur connecté : " + email);

	    Membre membreConnecte = contexteService.charger(email);
	    System.out.println("Membre trouvé en base : " + membreConnecte);

	    model.addAttribute("membreSession", membreConnecte);
	    System.out.println(model.getAttribute("membreSession"));
	    
	    return "redirect:/";
	}

	

	
	@ModelAttribute("membreSession")
	public Membre chargerMembreSession() {

		return new Membre();
	}
}
