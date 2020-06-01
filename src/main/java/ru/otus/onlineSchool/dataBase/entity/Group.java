package ru.otus.onlineSchool.dataBase.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "title")
    private String title;
    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private List<User> students;

    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    public Group() {
    }


    public void addStudent(User student) {
        students.add(student);
        student.addToTheGroup(this);
    }

    public void removeStudent(User student) {
        students.remove(student);
        student.removeFromTheGroup(this);
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        return id != null && id.equals(((Group) o).getId());
    }

    @Override
    public int hashCode() {
        return 11;
    }
}
