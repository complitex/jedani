INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (1, 'RU', 1);
INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (2, 'UA', 0);

INSERT INTO `sequence` (`name`) VALUES ('city'), ('city_type'), ('region'), ('country'), ('first_name'), ('middle_name'),
  ('last_name'), ('worker');

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'string_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'date');
INSERT INTO entity_value_type (id, value_type) VALUE (6, 'json');
INSERT INTO entity_value_type (id, value_type) VALUE (10, 'entity');

INSERT INTO `entity`(`id`, `name`) VALUES (1, 'country');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (1, 1, 'Страна'), (1, 2, 'Країна');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (1, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (1, 1, 1, 'Название'), (1, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (1, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (1, 2, 1, 'Краткое название'), (1, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (1, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (1, 3, 1, 'Менеджер'), (1, 3, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (2, 'region');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (2, 1, 'Регион'), (2, 2, 'Регіон');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 1, 1, 'Название'), (2, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 2, 1, 'Краткое название'), (2, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (2, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (2, 3, 1, 'Менеджер'), (2, 3, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (3, 'city_type');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (3, 1, 'Тип населенного пункта'), (3, 2, 'Тип населеного пункту');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (3, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (3, 1, 1, 'Название'), (3, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (3, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (3, 2, 1, 'Краткое название'), (3, 2, 2, 'Коротка назва');

INSERT INTO `entity`(`id`, `name`) VALUES (4, 'city');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (4, 1, 'Населенный пункт'), (4, 2, 'Населений пункт');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 1, 1, 'Название'), (4, 1, 2, 'Назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 2, 1, 'Краткое название'), (4, 2, 2, 'Коротка назва');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (4, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (4, 3, 1, 'Менеджер'), (4, 3, 2, 'Менеджер');

INSERT INTO `entity`(`id`, `name`) VALUES (10, 'user');

INSERT INTO `entity`(`id`, `name`) VALUES (11, 'first_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (11, 1, 'Имя'), (11, 2, 'Ім''я');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 1, 1, 'Имя'), (11, 1, 2, 'Ім''я');

INSERT INTO `entity`(`id`, `name`) VALUES (12, 'middle_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (12, 1, 'Отчество'), (12, 2, 'По батькові');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (12, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (12, 1, 1, 'Отчество'), (12, 1, 2, 'По батькові');

INSERT INTO `entity`(`id`, `name`) VALUES (13, 'last_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (13, 1, 'Фамилия'), (13, 2, 'Прізвище');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (13, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (13, 1, 1, 'Фамилия'), (13, 1, 2, 'Прізвище');

INSERT INTO `entity`(`id`, `name`) VALUES (20, 'worker');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (20, 1, 'Сотрудник'), (20, 2, 'Співробітник');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 1, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 1, 1, 'Номер'), (20, 1, 2, 'Номер');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 2, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 2, 1, 'E-mail'), (20, 2, 2, 'E-mail');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 3, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 3, 1, 'Дерево иерархии'), (20, 3, 2, 'Дерево ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 4, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 4, 1, 'Токен восстановления пароля'), (20, 4, 2, 'Токен відновлення пароля');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 5, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 5, 1, 'Дата создания токена восстановления пароля'), (20, 5, 2, 'Дата створення токена відновлення пароля');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 6, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 6, 1, 'Дата запоминания устройства'), (20, 6, 2, 'Дата запам''ятовування пристрої');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 7, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 7, 1, 'Дата создания'), (20, 7, 2, 'Дата створення');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 8, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 8, 1, 'Дата обновления'), (20, 8, 2, 'Дата оновлення');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 9, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 9, 1, 'МК статус'), (20, 9, 2, 'МК статус');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 10, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 10, 1, 'Имя'), (20, 10, 2, 'Ім''я');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 11, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 11, 1, 'Отчество'), (20, 11, 2, 'По батькові');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 12, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 12, 1, 'Фамилия'), (20, 12, 2, 'Прізвище');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 13, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 13, 1, 'Телефон'), (20, 13, 2, 'Телефон');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 14, 10, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 14, 1, 'Нас.пункт'), (20, 14, 2, 'Нас.пункт');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 15, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 15, 1, 'Менеджер'), (20, 15, 2, 'Менеджер');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 16, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 16, 1, 'Дата регистрации'), (20, 16, 2, 'Дата реєстрації');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 17, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 17, 1, 'Подробное дерево иерархии'), (20, 17, 2, 'Детальний дерево ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 18, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 18, 1, 'Уровень иерархии'), (20, 18, 2, 'Рівень ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 19, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 19, 1, 'Глубина иерархии'), (20, 19, 2, 'Глибина ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 20, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 20, 1, 'Контакты'), (20, 20, 2, 'Контакти');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 21, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 21, 1, 'Дата рождения'), (20, 21, 2, 'Дата народження');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 22, 2);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 22, 1, 'Дата увольнения'), (20, 22, 2, 'Дата звільнення');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 23, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 23, 1, 'Идертификатор родителя'), (20, 23, 2, 'Ідертіфікатор батька');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (20, 24, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (20, 24, 1, 'Идертификатор потомка'), (20, 24, 2, 'Ідертіфікатор нащадка');






