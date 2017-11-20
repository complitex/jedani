create table region
(
  id int not null primary key,
  name varchar(255) not null,
  manager_id int null
);

create table city
(
  id int not null primary key,
  name varchar(255) not null,
  region_id int null,
  manager_id int null,
  constraint city__region_fk
  foreign key (region_id) references region (id)
);

create index city__region_fk on city (region_id);

create table user
(
  id int not null primary key,
  email varchar(255) not null null,
  encrypted_password varchar(255) not null,
  ancestry varchar(255) null,
  j_id varchar(255) null,
  reset_password_token varchar(255) null,
  reset_password_send_at datetime null,
  remember_created_at datetime null,
  created_at datetime null,
  updated_at datetime null,
  mk_status int null,
  first_name varchar(255) null,
  second_name varchar(255) null,
  last_name varchar(255) null,
  phone varchar(255) null,
  city_id int null,
  manager_rank_id int null,
  involved_at datetime null,
  full_ancestry_path varchar(255) null,
  depth_level varchar(255) null,
  ancestry_depth int null,
  contact_info varchar(255) null,
  birthday date null,
  fired_status tinyint null,
  old_parent_id int null,
  old_child_id text null,
  constraint user__city_fk
  foreign key (city_id) references city (id)
);

create index user__city_fk on user (city_id);











