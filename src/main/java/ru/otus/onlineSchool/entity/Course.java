package ru.otus.onlineSchool.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "title", unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Group> groups = new HashSet<>();

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    @JsonManagedReference
    private Set<Lesson> lessons = new HashSet<>();

    public boolean addLesson(Lesson lesson) {
        if (lessons.contains(lesson)) return false;
        lesson.setCourse(this);
        return lessons.add(lesson);
    }

    public boolean removeLesson(Lesson lesson) {
        if (!lessons.contains(lesson)) return false;
        lesson.setCourse(null);
        return lessons.remove(lesson);

    }

    public boolean updateLesson(Lesson lesson) {
        if (!lessons.remove(lesson)) return false;
        lesson.setCourse(this);
        return lessons.add(lesson);
    }

    public void addGroup(Group group) {
        groups.add(group);
        group.setCourse(this);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.setCourse(null);
    }


    public Course() {
    }

    public Course(long id, String title){
        this.id = id;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
