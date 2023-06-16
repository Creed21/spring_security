set search_path = nst_final;

-- Create sequence for user ID
CREATE SEQUENCE user_sequ START 1;

-- Create sequence for role ID
CREATE SEQUENCE role_sequ START 1;

-- Create sequence for authority ID
CREATE SEQUENCE authority_sequ START 1;

-- Create table for users
CREATE TABLE users (
  id INT DEFAULT nextval('user_sequ') PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL
);

-- insert users
insert into users(username, "password", enabled)
			values('admin_pg', '$2a$10$KBhOy1mnxbEQAx5VsadXa./ZkuYblG0xfdo5p2nkoWCnaNjEcxnTi', true);
insert into users(username, "password", enabled)
			values('user_pg', '$2a$10$1yHv.bx7LMqtYeg4tyBgEesn0jokrG/JbJso9dXQbghOweqthHhGW', true);
insert into users(username, "password", enabled)
			values('stuff_pg', '$2a$10$bC6oApjShWcyc7nsaANlne9ceV8sgX63ticbIfC5JcfGz3HNSjLCC', true);

-- Create table for roles
CREATE TABLE roles (
  id INT DEFAULT nextval('role_sequ') PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

-- insert roles
insert into roles("name")
			values('ADMIN');
insert into roles("name")
			values('USER');
insert into roles("name")
			values('STUFF');

-- Create table for authorities
CREATE TABLE authorities (
  id INT DEFAULT nextval('authority_sequ') PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

-- insert authorities
insert into authorities("name")
				values('READ');
insert into authorities("name")
				values('WRITE');
insert into authorities("name")
				values('DELETE');
insert into authorities("name")
				values('UPDATE');

-- Create table for mapping user to role
CREATE TABLE user_role (
  user_id INT REFERENCES users(id),
  role_id INT REFERENCES roles(id),
  PRIMARY KEY (user_id, role_id)
);

-- Create table for mapping role to authority
CREATE TABLE role_authority (
  role_id INT REFERENCES roles(id),
  authority_id INT REFERENCES authorities(id),
  PRIMARY KEY (role_id, authority_id)
);

-- grant authorities to roles
-- ADMIN: READ, WRITE, DELETE, UPDATE
insert into role_authority(role_id, authority_id)
				values(1, 1);
insert into role_authority(role_id, authority_id)
				values(1, 2);
insert into role_authority(role_id, authority_id)
				values(1, 3);
insert into role_authority(role_id, authority_id)
				values(1, 4);

-- USER: READ, WRITE
insert into role_authority(role_id, authority_id)
				values(2, 1);
insert into role_authority(role_id, authority_id)
				values(2, 2);

-- STUFF: READ, WRITE, DELETE
insert into role_authority(role_id, authority_id)
				values(3, 1);
insert into role_authority(role_id, authority_id)
				values(3, 2);
insert into role_authority(role_id, authority_id)
				values(3, 3);


-- grant roles to users
-- user admin_pg has roles: ADMIN, USER, STUFF
insert into user_role(user_id, role_id)
				values(1, 1);
insert into user_role(user_id, role_id)
				values(1, 2);
insert into user_role(user_id, role_id)
				values(1, 3);

-- user user_pg has roles: USER
insert into user_role(user_id, role_id)
				values(2, 2);

-- user stuff_pg has roles: STUFF
insert into user_role(user_id, role_id)
				values(3, 3);

-- create role_authority view: roles and their granted authorities
create or replace view nst_final.wx_role_authority as
select 	role_id,
		r."name" role_name,
		authority_id,
		a."name" authority_name
from 	roles r
join	role_authority ra
	on  r.id = ra.role_id
join	authorities a
	on	a.id = ra.authority_id;


-- create users_roles_authorities view: see users and their granted roles with authorities
create or replace view nst_final.wx_users_roles_authorities as
select 	u.id user_id,
		u.username,
		u.enabled,
		ra.role_id,
		ra.role_name,
		ra.authority_id,
		ra.authority_name
from 	users u
join	user_role ur
	on 	u.id = ur.user_id
join	wx_role_authority ra
	on	ur.role_id = ra.role_id;

select * from wx_users_roles_authorities;


CREATE SEQUENCE nst_final.simple_data_sequ START 1;

create table nst_final.simple_data(
	id int DEFAULT nextval('simple_data_sequ') primary key,
	value text
);

