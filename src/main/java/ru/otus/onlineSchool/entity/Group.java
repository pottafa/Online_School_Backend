package ru.otus.onlineSchool.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@Table(name = "groups")
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "title", unique = true)
    private String title;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_groups",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id"
                         )},
            inverseJoinColumns = {
                    @JoinColumn(name = "group_id", referencedColumnName = "id"
                           )})
    private List<User> users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Course course;

    public Group() {
    }

    public Group(long id, String title) {
        this.id = id;
        this.title = title;
    }


    public void addStudent(User student) {
        users.add(student);
    }

    public void removeStudent(User student) {
        users.remove(student);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
