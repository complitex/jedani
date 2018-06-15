INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (1, 'RU', 1);
INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (2, 'UA', 0);

INSERT INTO `sequence` (`name`) VALUES ('city'), ('city_type'), ('region'), ('country'), ('first_name'), ('middle_name'),
  ('last_name'), ('worker'), ('mk_status'), ('position');

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'text_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'number_value');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (6, 'date');
INSERT INTO entity_value_type (id, value_type) VALUE (10, 'entity_value');
INSERT INTO entity_value_type (id, value_type) VALUE (11, 'entity');

/*Address*/

INSERT INTO `entity`(`id`, `name`) VALUES (1, 'country');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (1, 1, 'Страна'), (1, 2, 'Країна');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (1, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 1, 1, 'Название'), (1, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (1, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 2, 1, 'Краткое название'), (1, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (1, 10, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 10, 1, 'Менеджер'), (1, 10, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (2, 'region');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (2, 1, 'Регион'), (2, 2, 'Регіон');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (2, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (2, 1, 1, 'Название'), (2, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (2, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (2, 2, 1, 'Краткое название'), (2, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (2, 10, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (2, 10, 1, 'Менеджер'), (2, 10, 2, 'Менеджер');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (2, 100, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (2, 101, 0);

INSERT INTO `entity`(`id`, `name`) VALUES (3, 'city_type');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (3, 1, 'Тип населенного пункта'), (3, 2, 'Тип населеного пункту');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (3, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (3, 1, 1, 'Название'), (3, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (3, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (3, 2, 1, 'Краткое название'), (3, 2, 2, 'Коротка назва');

INSERT INTO `entity`(`id`, `name`) VALUES (4, 'city');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (4, 1, 'Населенный пункт'), (4, 2, 'Населений пункт');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (4, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (4, 1, 1, 'Название'), (4, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (4, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (4, 2, 1, 'Краткое название'), (4, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (4, 3, 11, 3);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (4, 3, 1, 'Тип нас. пункта'), (4, 3, 2, 'Тип нас. пункту');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (4, 10, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (4, 10, 1, 'Менеджер'), (4, 10, 2, 'Менеджер');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (4, 100, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (4, 101, 0);

/*User*/

INSERT INTO `entity`(`id`, `name`) VALUES (10, 'user');

INSERT INTO `entity`(`id`, `name`) VALUES (11, 'first_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (11, 1, 'Имя'), (11, 2, 'Ім''я');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (11, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (11, 1, 1, 'Имя'), (11, 1, 2, 'Ім''я');

INSERT INTO `entity`(`id`, `name`) VALUES (12, 'middle_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (12, 1, 'Отчество'), (12, 2, 'По батькові');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (12, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (12, 1, 1, 'Отчество'), (12, 1, 2, 'По батькові');

INSERT INTO `entity`(`id`, `name`) VALUES (13, 'last_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (13, 1, 'Фамилия'), (13, 2, 'Прізвище');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (13, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (13, 1, 1, 'Фамилия'), (13, 1, 2, 'Прізвище');

/* MK Status */

INSERT INTO `entity`(`id`, `name`) VALUES (21, 'mk_status');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (21, 1, 'МК статус'), (21, 2, 'МК статус');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (21, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (21, 1, 1, 'Статус'), (21, 1, 2, 'Статус');

INSERT INTO mk_status (id, object_id, status) VALUES (1, 1, 1);
INSERT INTO mk_status_attribute (id, object_id, entity_attribute_id, status) VALUES (1, 1, 1, 1);
INSERT INTO mk_status_value (attribute_id, locale_id, text) VALUES (1, 1, 'МК отсутствует');

INSERT INTO mk_status (id, object_id, status) VALUES (2, 2, 1);
INSERT INTO mk_status_attribute (id, object_id, entity_attribute_id, status) VALUES (2, 2, 1, 1);
INSERT INTO mk_status_value (attribute_id, locale_id, text) VALUES (2, 1, 'МК в рассрочке');

INSERT INTO mk_status (id, object_id, status) VALUES (3, 3, 1);
INSERT INTO mk_status_attribute (id, object_id, entity_attribute_id, status) VALUES (3, 3, 1, 1);
INSERT INTO mk_status_value (attribute_id, locale_id, text) VALUES (3, 1, 'МК выкуплен');


/* Position */

INSERT INTO `entity`(`id`, `name`) VALUES (22, 'position');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (22, 1, 'Должность'), (22, 2, 'Посада');
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (22, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (22, 1, 1, 'Название'), (22, 1, 2, 'Назва');

/*Worker*/

INSERT INTO `entity`(`id`, `name`) VALUES (20, 'worker');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (20, 1, 'Сотрудник'), (20, 2, 'Співробітник');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 1, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 1, 1, 'Номер договора'), (20, 1, 2, 'Номер договору');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, reference_id) VALUES (20, 2, 11, 11);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 2, 1, 'Имя'), (20, 2, 2, 'Ім''я');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 3, 11, 12);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 3, 1, 'Отчество'), (20, 3, 2, 'По батькові');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 4, 11, 13);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 4, 1, 'Фамилия'), (20, 4, 2, 'Прізвище');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 5, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 5, 1, 'Дата рождения'), (20, 5, 2, 'Дата народження');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 6, 10, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 6, 1, 'Регион'), (20, 6, 2, 'Регио');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 7, 10, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 7, 1, 'Нас.пункт'), (20, 7, 2, 'Нас.пункт');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 8, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 8, 1, 'Телефон'), (20, 8, 2, 'Телефон');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 9, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 9, 1, 'E-mail'), (20, 9, 2, 'E-mail');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 10, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 10, 1, 'Контакты'), (20, 10, 2, 'Контакти');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 11, 11, 22);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 11, 1, 'Должность'), (20, 11, 2, 'Должность');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 12, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 12, 1, 'Дата создания'), (20, 12, 2, 'Дата створення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 13, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 13, 1, 'Дата обновления'), (20, 13, 2, 'Дата оновлення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 14, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 14, 1, 'Дата регистрации'), (20, 14, 2, 'Дата реєстрації');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 15, 11, 21);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 15, 1, 'МК статус'), (20, 15, 2, 'МК статус');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 16, 3);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 16, 1, 'Статус увольнения'), (20, 16, 2, 'Статус звільнення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 17, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 17, 1, 'Идентификатор руководителя'), (20, 17, 2, 'Ідентифікатор керівника');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 18, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 18, 1, 'Токен восстановления пароля'), (20, 18, 2, 'Токен відновлення пароля');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 19, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 19, 1, 'Дата создания токена восстановления пароля'), (20, 19, 2, 'Дата створення токена відновлення пароля');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 20, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 20, 1, 'Дата запоминания устройства'), (20, 20, 2, 'Дата запам''ятовування пристрої');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 100, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 101, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 102, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 103, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 104, 0);

INSERT INTO `worker` (`object_id`, `status`) VALUE (1, 4);
UPDATE `sequence` set `value` = 2 where `name` = 'worker';

/* Admin */

INSERT INTO `user`(login, password) value ('admin', sha2('admin', 256));
INSERT INTO `user_group` (login, name) value ('admin', 'AUTHORIZED');
INSERT INTO `user_group` (login, name) value ('admin', 'ADMINISTRATORS');





