package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorDbStorageImpl implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, directorRowMapper());
    }

    @Override
    public Director findDirectorById(int id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, directorRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка поиска режиссёра с id \"{}\"", id);
            throw new EntityNotFoundException("Нет режиссёра с id: " + id);
        }
    }

    @Override
    public Director post(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                        .withTableName("directors").usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", director.getName());
        Number directorId = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(directorId.intValue());
        return director;
    }

    @Override
    public Director put(Director director) {
        String sql = "UPDATE directors SET name = ?";
        findDirectorById(director.getId());
        jdbcTemplate.update(sql, director.getName());
        return director;
    }

    @Override
    public void delDirectorById(int id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name"));
    }
}
