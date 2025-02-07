package fr.eni.tp.filmotheque.bll;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dal.MembreDAO;
import fr.eni.tp.filmotheque.dal.exception.BusinessException;

@Service
@Primary
public class ContexteServiceImpl implements ContexteService {

	private MembreDAO membreDAO;

	public ContexteServiceImpl(MembreDAO membreDAO) {
		this.membreDAO = membreDAO;
	}

	@Override
	public Membre charger(String email) {

		return membreDAO.read(email);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creerMembre(Membre membre) throws BusinessException {
		BusinessException be = new BusinessException();
		boolean valide = validerEmailInexistant(membre.getPseudo(), be);

		try {
			if (valide) {
				membreDAO.create(membre);
			} else {
				throw be;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw be;
		}
	}
	
	private boolean validerEmailInexistant(String email, BusinessException be) {
		boolean valide = true;

		int nbGenre = membreDAO.countByEmail(email);
		if (nbGenre == 1) {
			valide = false;
			be.addCleErreur("validation.membre.email");
		}

		return valide;
	}

}
