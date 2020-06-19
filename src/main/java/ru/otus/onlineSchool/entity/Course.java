package ru.otus.onlineSchool.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Audited
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Group> groups = new ArrayList<>();

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Lesson> lessons = new ArrayList<>();

    public Course(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public boolean addLesson(Lesson lesson) {
        if (lessons.contains(lesson)) {
            return false;
        }
        lesson.setCourse(this);
        return lessons.add(lesson);
    }

    public boolean removeLesson(Lesson lesson) {
        if (!lessons.contains(lesson)) {
            return false;
        }
        lesson.setCourse(null);
        return lessons.remove(lesson);

    }

    public boolean updateLesson(Lesson lesson) {
        if (!lessons.remove(lesson)) {
            return false;
        }
        lesson.setCourse(this);
        return lessons.add(lesson);
    }

    public boolean addGroup(Group group) {
        if (groups.contains(group)) {
            return false;
        }
        group.setCourse(this);
        return groups.add(group);
    }

    public boolean updateGroup(Group group) {
        if (!groups.remove(group)) return false;
        group.setCourse(this);
        return groups.add(group);
    }

    public boolean removeGroup(Group group) {
        if (!groups.contains(group)) return false;
        group.setCourse(null);
        return groups.remove(group);
    }


    public Course() {
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

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
