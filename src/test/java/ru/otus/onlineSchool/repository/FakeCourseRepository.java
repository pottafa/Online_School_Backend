package ru.otus.onlineSchool.repository;

import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.onlineSchool.entity.Course;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.Lesson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeCourseRepository implements CourseRepository {

    private Map<Long, Course> courses;

    public FakeCourseRepository() {
        reset();
    }

    public void reset() {
        courses = new HashMap<>();
        Course course1 = new Course(1, "Course One");

        course1.addGroup(new Group(1, "Group one"));
        course1.addGroup(new Group(2, "Group two"));

        course1.addLesson(new Lesson(1, "Lesson one", "Some description"));
        course1.addLesson(new Lesson(2, "Lesson two", "Some description"));

        courses.put(1L, course1);
        courses.put(2L, new Course(2, "Course Two"));
        courses.put(3L, new Course(3, "Course Three"));


    }

    @Override
    public Optional<Course> findByTitle(String title) {
        return courses.entrySet().stream()
                .filter(e -> e.getValue().getTitle().equals(title))
                .map(e -> e.getValue())
                .findFirst();
    }

    @Override
    public Course save(Course course) {
        courses.put(course.getId(), course);
        return course;
    }

    @Override
    public <S extends Course> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(courses.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return courses.containsKey(id);
    }

    @Override
    public Iterable<Course> findAll() {
        return courses.values();
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
        courses.remove(id);
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