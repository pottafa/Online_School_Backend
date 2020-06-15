package ru.otus.onlineSchool.controllers.rest;

import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.repository.CourseRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeCourseRepository implements CourseRepository {
    private final Map<Long, Course> COURSES;

    public FakeCourseRepository() {
        COURSES = new HashMap<>();
        COURSES.put(1L, new Course(1, "Course One"));
        COURSES.put(2L, new Course(2, "Course Two"));
        COURSES.put(3L, new Course(3, "Course Three"));
    }

    @Override
    public Optional<Course> findByTitle(String title) {
        return COURSES.entrySet().stream()
                .filter(e -> e.getValue().getTitle().equals(title))
                .map(e -> e.getValue())
                .findFirst();
    }

    @Override
    public void deleteByTitle(String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Course save(Course course) {
        return COURSES.put(course.getId(), course);
    }

    @Override
    public <S extends Course> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(COURSES.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Course> findAll() {
        return COURSES.values();
    }

    @Override
    public Iterable<Course> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(Long id) {
        COURSES.remove(id);
    }

    @Override
    public void delete(Course entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends Course> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}
