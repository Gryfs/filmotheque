package fr.eni.tp.filmotheque.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Participant;

@Repository
public class FilmDAOImpl implements FilmDAO {

	private final static String CREATE = "INSERT INTO FILM (titre, annee, duree, synopsis, id_realisateur, id_genre) VALUES (:titre, :annee, :duree, :synopsis, :id_realisateur, :id_genre)";
	private final static String SELECT_BY_ID = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM WHERE id=:id";
	private final static String SELECT_ALL = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM";
	private final static String SELECT_TITRE = "SELECT titre FROM FILM WHERE id=:id";
	private final static String COUNT_BY_IDGENRE = "SELECT count(*) FROM GENRE WHERE id = :id";
	private final static String COUNT_BY_IDREALISATEUR = "SELECT count(*) FROM PARTICIPANT WHERE id = :id_realisateur";
	private final static String COUNT_LISTE_ACTEURS = "SELECT COUNT(*) FROM PARTICIPANT WHERE id IN (:listeActeurs)";
	private final static String COUNT_BY_TITRE = "SELECT COUNT(*) FROM FILM WHERE titre = :titre";
	private final static String CREATE_ACTEURS = "INSERT INTO ACTEURS (id_film, id_participant) VALUES (:id_film, :id_participant)";
	private final static String COUNT_BY_IDFILM = "SELECT COUNT(*) FROM FILM WHERE id = :id";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FilmDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public void create(Film film) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", film.getId());
		namedParameters.addValue("titre", film.getTitre());
		namedParameters.addValue("annee", film.getAnnee());
		namedParameters.addValue("duree", film.getDuree());
		namedParameters.addValue("synopsis", film.getSynopsis());
		namedParameters.addValue("id_realisateur", film.getRealisateur().getId());
		namedParameters.addValue("id_genre", film.getGenre().getId());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(CREATE, namedParameters, keyHolder);

		if (keyHolder != null && keyHolder.getKey() != null) {
			film.setId(keyHolder.getKey().longValue());
		}

		if (film.getActeurs() != null) {
			for (Participant acteur : film.getActeurs()) {
				// Insérer chaque acteur dans la table ACTEURS (relation film-participant)
				MapSqlParameterSource acteurParams = new MapSqlParameterSource();
				acteurParams.addValue("id_film", film.getId()); // ID du film
				acteurParams.addValue("id_participant", acteur.getId()); // ID du participant


				namedParameterJdbcTemplate.update(CREATE_ACTEURS, acteurParams);
			}
		}

	}

	@Override
	public Film read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters, new FilmRowMapper());
	}

	@Override
	public List<Film> findAll() {

		return namedParameterJdbcTemplate.query(SELECT_ALL, new FilmRowMapper());
	}

	@Override
	public String findTitre(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		return namedParameterJdbcTemplate.queryForObject(SELECT_TITRE, namedParameters, String.class);
	}

	/**
	 * Mise en place du RowMapper avec gestion de l'association 1-1
	 */
	class FilmRowMapper implements RowMapper<Film> {

		@Override
		public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
			Film f = new Film();
			f.setId(rs.getLong("id"));
			f.setTitre(rs.getString("titre"));
			f.setAnnee(rs.getInt("annee"));
			f.setDuree(rs.getInt("duree"));
			f.setSynopsis(rs.getString("synopsis"));

			Participant realisateur = new Participant();
			realisateur.setId(rs.getLong("id_realisateur"));
			f.setRealisateur(realisateur);

			// Association
			Genre genre = new Genre();
			genre.setId(rs.getLong("id_genre"));
			f.setGenre(genre);

			return f;
		}
	}

	@Override
	public int countByIdGenre(long idGenre) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", idGenre);

		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_IDGENRE, namedParameters, Integer.class);
	}

	@Override
	public int countByIdRealisateur(long idRealisateur) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id_realisateur", idRealisateur);

		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_IDREALISATEUR, namedParameters, Integer.class);
	}

	@Override
	public int countByTitre(String titre) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("titre", titre);

		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_TITRE, namedParameters, Integer.class);
	}

	@Override
	public boolean validerListeActeursExiste(List<Participant> listeActeurs) {
		List<Long> listeId = listeActeurs.stream().map(Participant::getId).toList();

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("listeActeurs", listeId);

		int nbActeurs = namedParameterJdbcTemplate.queryForObject(COUNT_LISTE_ACTEURS, namedParameters, Integer.class);

		return listeId.size() == nbActeurs;
	}

	@Override
	public int countByIdFilm(long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", idFilm);

		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_IDFILM, namedParameters, Integer.class);
	}
	


}
