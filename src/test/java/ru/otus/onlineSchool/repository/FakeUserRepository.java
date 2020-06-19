package ru.otus.onlineSchool.repository;

import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.Lesson;
import ru.otus.onlineSchool.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeUserRepository implements UserRepository {

    private Map<Long, User> users;

    public FakeUserRepository() {
        reset();
    }

    public void reset() {
        users = new HashMap<>();
        users.put(1L, new User(1, "User one", "Password one"));
        users.put(2L, new User(2, "User two", "Password two"));
        users.put(3L, new User(3, "User three", "Password three"));
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Iterable<User> findAll() {
        return users.values();
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public void delete(User entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        throw new UnsupportedOperationException();
    }
}