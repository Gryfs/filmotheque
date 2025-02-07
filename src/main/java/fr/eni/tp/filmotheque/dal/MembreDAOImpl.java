package fr.eni.tp.filmotheque.dal;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.bo.Participant;

@Repository
public class MembreDAOImpl implements MembreDAO {
	
	private final static String SELECT_BY_ID = "SELECT id, nom, prenom, email FROM MEMBRE WHERE id=:id";
	private final static String SELECT_BY_EMAIL = "SELECT id, nom, prenom, email FROM MEMBRE WHERE email=:email";
	private final static String CREATE = "INSERT INTO MEMBRE (nom, prenom, email, password, admin) VALUES (:nom, :prenom, :email, :password, 0)";
	private final static String COUNT_BY_EMAIL = "SELECT count(*) FROM MEMBRE WHERE email = :email";


	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	

	public MembreDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Membre read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters,
				new BeanPropertyRowMapper<Membre>(Membre.class));
	}

	@Override
	public Membre read(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("email", email);

		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_EMAIL, namedParameters,
				new BeanPropertyRowMapper<Membre>(Membre.class));
	}

	@Override
	public void create(Membre membre) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("nom", membre.getNom());
		namedParameters.addValue("prenom", membre.getPrenom());
		namedParameters.addValue("email", membre.getPseudo());
		namedParameters.addValue("password", membre.getMotDePasse());


		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(CREATE, namedParameters, keyHolder);

		
		if(keyHolder != null && keyHolder.getKey() !=null) {
			membre.setId(keyHolder.getKey().longValue());
		}
	}
	
	@Override
	public int countByEmail(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("email", email);

		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_EMAIL, namedParameters, Integer.class);
	}

}
