insert into courses(id, title, description ) values (1, 'Java SE', 'Курс по Java SE');
insert into lessons(id, course_id, title)  values (1, 1, 'Введение');
insert into lessons(id, course_id, title)  values (2, 1, 'Коллекции');
insert into lessons(id, course_id, title)  values (3, 1, 'Рефлекшн');

insert into courses(id, title, description) values(2, 'Spring', 'Курс по Spring Framework');
insert into lessons(id, course_id, title)  values (4, 2, 'Урок 1 по Spring');
insert into lessons(id, course_id,  title) values (5, 2, 'Урок 2 по Spring');
insert into lessons(id, course_id, title)  values (6, 2, 'Урок 3 по Spring');
insert into lessons(id, course_id, title)  values (7, 2, 'Урок 3 по Spring');

insert into groups (course_id, title, id) values (2, 'First group', 8);

insert into courses(id, title, description) values(3, 'Spring Boot', 'Курс по Spring Boot');

insert into courses(id, title, description) values (4, 'Java Script', 'Курс по JS');

insert into roles (id, name) values ('0', 'ROLE_USER' );
insert into roles (id, name) values ('1', 'ROLE_ADMIN' );
insert into roles (id, name) values ('2', 'ROLE_TEACHER' );

insert into users (id, login, password) values ('101', 'teacher', '$2y$12$xAJNHHpZxsHQWDTBY9oVouIZgfAM3DrqIxW1J/s/wgvpv6G12Jr36' );
insert into users (id, login, password) values ('102', 'student', '$2y$12$xAJNHHpZxsHQWDTBY9oVouIZgfAM3DrqIxW1J/s/wgvpv6G12Jr36' );

insert into users_roles (user_id, roles_id) values ('101', '2' );
insert into users_roles (user_id, roles_id) values ('102', '0' );

/*
Admin creation
Password: otus
*/
insert into users (id, login, password) values ('100', 'admin', '$2y$12$xAJNHHpZxsHQWDTBY9oVouIZgfAM3DrqIxW1J/s/wgvpv6G12Jr36' );
insert into users_roles (user_id, roles_id) values ('100', '1' );

