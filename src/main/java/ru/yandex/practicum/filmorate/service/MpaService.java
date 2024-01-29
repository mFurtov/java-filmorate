package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa findMpaForId(int id) {
        return mpaStorage.findMpaForId(id);
    }

    public List<Mpa> mpaFindAll() {
        return mpaStorage.mpaFindAll();
    }
}
