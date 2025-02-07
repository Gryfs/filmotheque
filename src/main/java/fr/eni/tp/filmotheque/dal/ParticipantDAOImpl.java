package fr.eni.tp.filmotheque.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Participant;

@Repository
public class ParticipantDAOImpl implements ParticipantDAO {
	
	private final static String SELECT_BY_ID = "SELECT nom, prenom, id FROM PARTICIPANT WHERE id=:id";
	private final static String CREATE = "INSERT INTO ACTEURS (id_participant, id_film) VALUES (:id_participant, :id_film)";
	private final static String SELECT_ALL = "SELECT nom, prenom, id FROM PARTICIPANT";
	private final static String SELECT_ACTEURS_FILM = "SELECT p.id, p.nom, p.prenom FROM participant p JOIN acteurs a ON p.id = a.id_participant WHERE a.id_film = :id_film";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ParticipantDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Participant read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters,
				new BeanPropertyRowMapper<Participant>(Participant.class));
	}

	@Override
	public List<Participant> findAll() {
		return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<Participant>(Participant.class));
	}

	@Override
	public List<Participant> findActeurs(long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id_film", idFilm);

		return namedParameterJdbcTemplate.query(SELECT_ACTEURS_FILM, namedParameters, new ParticipantRowMapper());
	}

	@Override
	public void createActeur(long idParticipant, long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id_film", idFilm);
		namedParameters.addValue("id_participant", idParticipant);

		namedParameterJdbcTemplate.update(CREATE, namedParameters);

	}
	
	/**
	 * Mise en place du RowMapper avec gestion de l'association 1-1
	 */
	class ParticipantRowMapper implements RowMapper<Participant> {

		@Override
		public Participant mapRow(ResultSet rs, int rowNum) throws SQLException {
			Participant p = new Participant();
			p.setId(rs.getLong("id"));
			p.setNom(rs.getString("nom"));
			p.setPrenom(rs.getString("prenom"));

			return p;
		}
	}
	

}
