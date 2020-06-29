insert into courses(course_id, title, description ) values (103, 'Java SE', 'Курс по Java SE');
insert into lessons(id, course_id, title)  values (107, 103, 'Введение');
insert into lessons(id, course_id, title)  values (108, 103, 'Коллекции');
insert into lessons(id, course_id, title)  values (109, 103, 'Рефлекшн');

insert into courses(course_id, title, description) values(104, 'Spring', 'Курс по Spring Framework');
insert into lessons(id, course_id, title)  values (110, 104, 'Урок 1 по Spring');
insert into lessons(id, course_id,  title) values (111, 104, 'Урок 2 по Spring');
insert into lessons(id, course_id, title)  values (112, 104, 'Урок 3 по Spring');
insert into lessons(id, course_id, title)  values (113, 104, 'Урок 3 по Spring');

insert into groups (course_id, title, group_id) values (103, 'First group', 114);
insert into groups (course_id, title, group_id) values (103, 'Second group', 118);
insert into groups (course_id, title, group_id) values (103, 'Third group', 119);

insert into courses(course_id, title, description) values(105, 'Spring Boot', 'Курс по Spring Boot');

insert into courses(course_id, title, description) values (106, 'Java Script', 'Курс по JS');

insert into roles (id, name) values ('0', 'ROLE_USER' );
insert into roles (id, name) values ('1', 'ROLE_ADMIN' );
insert into roles (id, name) values ('2', 'ROLE_TEACHER' );

insert into users (id, login, password) values (115, 'teacher', '$2y$12$xAJNHHpZxsHQWDTBY9oVouIZgfAM3DrqIxW1J/s/wgvpv6G12Jr36' );
insert into users (id, login, password) values (116, 'student', '$2y$12$xAJNHHpZxsHQWDTBY9oVouIZgfAM3DrqIxW1J/s/wgvpv6G12Jr36'  );

insert into users_roles (user_id, roles_id) values (115, 2 );
insert into users_roles (user_id, roles_id) values (116, 0 );

insert into profiles (id, age, email, name, user_id) values (118, 40, 'someEmail@gmail.com', 'Joe', 115 );
insert into profiles (id, age, email, name, user_id) values (119, 27, 'pottafa.spam@gmail.com', 'Alex', 116 );

insert into users_groups (group_id, user_id) values (114, 116 );
insert into users_groups (group_id, user_id) values (114, 115 );

/*
Admin creation
Password: otus
*/
insert into users (id, login, password) values (117, 'admin', '$2y$12$xAJNHHpZxsHQWDTBY9oVouIZgfAM3DrqIxW1J/s/wgvpv6G12Jr36' );
insert into users_roles (user_id, roles_id) values (117, 1 );

