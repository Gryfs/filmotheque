package fr.eni.tp.filmotheque.dal;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Genre;

@Repository
public class GenreDAOImpl implements GenreDAO {

	private final static String SELECT_BY_ID = "SELECT id, titre FROM GENRE WHERE id=:id";
	private final static String SELECT_ALL = "SELECT id, titre FROM GENRE";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GenreDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Genre read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);

		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters,
				new BeanPropertyRowMapper<Genre>(Genre.class));
	}

	@Override
	public List<Genre> findAll() {
		return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<Genre>(Genre.class));
	}

}
