insert into users (id, email, password, first_name, last_name, birth_date, address, phone_number, is_deleted)
values (1, 'user1@gmail.com', '$2a$10$pnyqYjIJXzVu5eAF6wHTxe4gGdDbxW/D2XijSXAf9lGjIWvkEXZH.', 'User', 'Userenko','1980-01-01', 'SuperStreet1', '380987766543', 0),
       (2, 'user2@gmail.com', 'superpassword', 'User1', 'Userneko', '1985-01-01', 'SuperStreet1', '380987766543', 0),
       (3, 'user3@gmail.com', 'superpassword', 'User2', 'Userneko', '2001-01-01', 'SuperStreet1', '380987766543', 0);

insert into roles (id, role_name, is_deleted)
values (1, 'ROLE_ADMIN', 0),
       (2, 'ROLE_USER', 0);

insert into users_roles (user_id, role_id)
values (1, 2),
       (2, 2),
       (3, 2);


