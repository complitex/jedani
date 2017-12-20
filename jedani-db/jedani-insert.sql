INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (1, 'RU', 1);
INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (2, 'UA', 0);

INSERT INTO `sequence` (`name`) VALUES ('city'), ('city_type'), ('region'), ('country'), ('worker');

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'string_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'date');
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

INSERT INTO `entity`(`id`, `name`) VALUES (11, 'worker');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (11, 1, 'Сотрудник'), (11, 2, 'Співробітник');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 1, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 1, 1, 'Идентификатор Джедани'), (11, 1, 2, 'Ідентифікатор Джедані');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 2, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 2, 1, 'E-mail'), (11, 2, 2, 'E-mail');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 3, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 3, 1, 'Дерево иерархии'), (11, 3, 2, 'Дерево ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 4, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 4, 1, 'Токен восстановления пароля'), (11, 4, 2, 'Токен відновлення пароля');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 5, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 5, 1, 'Дата создания токена восстановления пароля'), (11, 5, 2, 'Дата створення токена відновлення пароля');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 6, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 6, 1, 'Дата запоминания устройства'), (11, 6, 2, 'Дата запам''ятовування пристрої');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 7, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 7, 1, 'Дата создания'), (11, 7, 2, 'Дата створення');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 8, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 8, 1, 'Дата обновления'), (11, 8, 2, 'Дата оновлення');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 9, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 9, 1, 'МК статус'), (11, 9, 2, 'МК статус');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 10, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 10, 1, 'Имя'), (11, 10, 2, 'Ім''я');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 11, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 11, 1, 'Отчество'), (11, 11, 2, 'По батькові');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 12, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 12, 1, 'Фамилия'), (11, 12, 2, 'Прізвище');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 13, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 13, 1, 'Телефон'), (11, 13, 2, 'Телефон');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`, `reference_id`) VALUES (11, 14, 10, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 14, 1, 'Населенный пункт'), (11, 14, 2, 'Населенный пункт');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 15, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 15, 1, 'Менеджер'), (11, 15, 2, 'Менеджер');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 16, 5);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 16, 1, 'Дата регистрации'), (11, 16, 2, 'Дата реєстрації');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 17, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 17, 1, 'Подробное дерево иерархии'), (11, 17, 2, 'Детальний дерево ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 18, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 18, 1, 'Уровень иерархии'), (11, 18, 2, 'Рівень ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 19, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 19, 1, 'Глубина иерархии'), (11, 19, 2, 'Глибина ієрархії');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 20, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 20, 1, 'Контакты'), (11, 20, 2, 'Контакти');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 21, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 21, 1, 'Дата рождения'), (11, 21, 2, 'Дата народження');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 22, 2);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 22, 1, 'Дата увольнения'), (11, 22, 2, 'Дата звільнення');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 23, 4);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 23, 1, 'Идертификатор родителя'), (11, 23, 2, 'Ідертіфікатор батька');
INSERT INTO `entity_attribute`(`entity_id`, `attribute_id`, `value_type_id`) VALUES (11, 24, 1);
INSERT INTO `entity_value`(`entity_id`, `attribute_id`, `locale_id`, `text`) VALUES (11, 24, 1, 'Идертификатор потомка'), (11, 24, 2, 'Ідертіфікатор нащадка');






