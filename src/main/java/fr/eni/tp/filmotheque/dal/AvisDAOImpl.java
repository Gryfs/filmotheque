package fr.eni.tp.filmotheque.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.bo.Participant;


@Repository
public class AvisDAOImpl implements AvisDAO{
	
	private final static String CREATE = "INSERT INTO AVIS (note,commentaire,id_membre,id_film) VALUES (:note,:commentaire,:id_membre,:id_film)";
	private final static String FIND_AVIS_FILM = "SELECT a.id, a.note, a.commentaire, a.id_membre FROM AVIS a JOIN MEMBRE m ON a.id_membre = m.id WHERE a.id_film = :id_film";
	
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	

	public AvisDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public void create(Avis avis, long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("note", avis.getNote());
		namedParameters.addValue("commentaire", avis.getCommentaire());
		namedParameters.addValue("id_membre", avis.getMembre().getId());
		namedParameters.addValue("id_film", idFilm);

		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(CREATE, namedParameters, keyHolder);

		
		if(keyHolder != null && keyHolder.getKey() !=null) {
			avis.setId(keyHolder.getKey().longValue());
		}
	}

	@Override
	public List<Avis> findByFilm(long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id_film", idFilm);
		return namedParameterJdbcTemplate.query(FIND_AVIS_FILM, namedParameters, new AvisRowMapper());
	}
	
	/**
	 * Mise en place du RowMapper avec gestion de l'association 1-1
	 */
	class AvisRowMapper implements RowMapper<Avis> {

		@Override
		public Avis mapRow(ResultSet rs, int rowNum) throws SQLException {
			Avis a = new Avis();
			a.setId(rs.getLong("id"));
			a.setNote(rs.getInt("note"));
			a.setCommentaire(rs.getString("commentaire"));

			
			

			// Association
			Membre membre = new Membre();
			membre.setId(rs.getLong("id_membre"));
			a.setMembre(membre);

			return a;
		}
	}


}
