package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsUser;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsUser friendsUser;

    @Override
    public List<User> findAll() {
        String sql = "select * from users order by id";
        return jdbcTemplate.query(sql, userRowMapper());
    }

    @Override
    public User findUserById(int id) {
        String sql = "select * from users where id = ? order by id";
        try {
            return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Нет пользователя с ID: " + id);
        }
    }

    @Override
    public User post(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("users").usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("email", user.getEmail(), "login", user.getLogin(), "name", user.getName(), "birthday", user.getBirthday().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        return user;
    }

    @Override
    public User put(User user) {
        User findUser = findUserById(user.getId());
        String sqlUpdate = "update users set login = ?,name = ?, email=?,birthday =? where id = ?";
        jdbcTemplate.update(sqlUpdate, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void addFriends(Integer id, Integer friendId) {
        friendsUser.addFriends(id, friendId);
    }

    @Override
    public void dellFriends(Integer id, Integer friendId) {
        friendsUser.dellFriends(id, friendId);
    }

    @Override
    public List<User> getFriends(Integer id) {
        return friendsUser.getFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return friendsUser.getCommonFriends(id, otherId);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(rs.getInt("id"), rs.getString("email"), rs.getString("login"), rs.getString("name"), LocalDate.parse(rs.getString("birthday")));
    }


}
