/* Locale */

INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (1, 'RU', 1);
INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (2, 'UA', 0);

/* Entity value type */

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'text_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'number_value');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (6, 'date');
INSERT INTO entity_value_type (id, value_type) VALUE (10, 'entity_value');
INSERT INTO entity_value_type (id, value_type) VALUE (11, 'entity');

/* Admin */

INSERT INTO `user`(login, password) value ('admin', sha2('admin', 256));
INSERT INTO `user_group` (login, name) value ('admin', 'AUTHORIZED');
INSERT INTO `user_group` (login, name) value ('admin', 'ADMINISTRATORS');

DELIMITER //

-- Add create entity procedures

CREATE PROCEDURE createEntity(IN id BIGINT, IN entityName VARCHAR(64) CHARSET utf8,
                              IN entityDescriptionRU VARCHAR(128) CHARSET utf8, IN entityDescriptionUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertEntity = CONCAT('INSERT INTO `entity` (`id`, `name`) VALUE (',id, ', ''', entityName, ''');');

    PREPARE QUERY FROM @insertEntity; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertEntityValue = CONCAT('INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (', id,
                                    ', 1, ''', entityDescriptionRU, '''), (', id, ', 2, ''', entityDescriptionUA, ''');');

    PREPARE QUERY FROM @insertEntityValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

CREATE PROCEDURE createEntityAttribute(IN entityId BIGINT, IN entityAttributeId BIGINT, IN valueTypeId BIGINT,
                                       IN entityDescriptionRU VARCHAR(128) CHARSET utf8, IN entityDescriptionUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertAttribute = CONCAT('INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (',
                                  entityId, ', ', entityAttributeId, ', ', valueTypeId, ');');

    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertEntityValue = CONCAT('INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (',
                                    entityId, ', ', entityAttributeId, ', 1, ''', entityDescriptionRU, '''), (',
                                    entityId, ', ', entityAttributeId, ', 2, ''', entityDescriptionUA, ''');');

    PREPARE QUERY FROM @insertEntityValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

CREATE PROCEDURE createEntityAttributeWithReference(IN entityId BIGINT, IN entityAttributeId BIGINT, IN valueTypeId BIGINT, IN referenceId BIGINT,
                                                    IN entityDescriptionRU VARCHAR(128) CHARSET utf8, IN entityDescriptionUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertAttribute = CONCAT('INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (',
                                  entityId, ', ', entityAttributeId, ', ', valueTypeId,  ', ', referenceId, ');');

    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertEntityValue = CONCAT('INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (',
                                    entityId, ', ', entityAttributeId, ', 1, ''', entityDescriptionRU, '''), (',
                                    entityId, ', ', entityAttributeId, ', 2, ''', entityDescriptionUA, ''');');

    PREPARE QUERY FROM @insertEntityValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

/* Setting */

INSERT INTO `entity` (`id`, `name`) VALUE (0, 'setting');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (0, 1, 'Настройки'), (0, 2, 'Налаштування');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (0, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (0, 1, 1, 'Значение'), (0, 1, 2, 'Значення');

INSERT INTO `setting` (`id`, `object_id`) VALUE (1, 1);
INSERT INTO `setting_attribute` (`domain_id`, `entity_attribute_id`, `text`) VALUES (1, 1, '/jedani/data/promotion/');

/* Address */

INSERT INTO `entity`(`id`, `name`) VALUES (1, 'country');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (1, 1, 'Страна'), (1, 2, 'Країна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (1, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 1, 1, 'Название'), (1, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (1, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 2, 1, 'Краткое название'), (1, 2, 2, 'Коротка назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (1, 3, 11, 30);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 3, 1, 'Нац.валюта'), (1, 3, 2, 'Нац.валюті');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (1, 4, 11, 31);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 4, 1, 'Курс евро'), (1, 4, 2, 'Курс евро');

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

/* User */

INSERT INTO `entity`(`id`, `name`) VALUES (10, 'user');

/* FIO */

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
INSERT INTO mk_status_attribute (id, domain_id, entity_attribute_id, status) VALUES (1, 1, 1, 1);
INSERT INTO mk_status_value (attribute_id, locale_id, text) VALUES (1, 1, 'МК отсутствует');

INSERT INTO mk_status (id, object_id, status) VALUES (2, 2, 1);
INSERT INTO mk_status_attribute (id, domain_id, entity_attribute_id, status) VALUES (2, 2, 1, 1);
INSERT INTO mk_status_value (attribute_id, locale_id, text) VALUES (2, 1, 'МК в рассрочке');

INSERT INTO mk_status (id, object_id, status) VALUES (3, 3, 1);
INSERT INTO mk_status_attribute (id, domain_id, entity_attribute_id, status) VALUES (3, 3, 1, 1);
INSERT INTO mk_status_value (attribute_id, locale_id, text) VALUES (3, 1, 'МК выкуплен');


/* Position */

INSERT INTO `entity`(`id`, `name`) VALUES (22, 'position');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (22, 1, 'Должность'), (22, 2, 'Посада');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (22, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (22, 1, 1, 'Название'), (22, 1, 2, 'Назва');

INSERT INTO position (`id`, `object_id`, `start_date`, `status`, `user_id`) VALUE (1, 1, now(), 1, 1);
INSERT INTO position_attribute (`id`, `domain_id`, `entity_attribute_id`, `start_date`, `status`, `user_id`) VALUE (1, 1, 1, now(), 1, 1);
INSERT INTO position_value (`id`, `attribute_id`, `locale_id`, text) VALUE (1, 1, 1, 'СЕКРЕТАРЬ');

INSERT INTO position (`id`, `object_id`, `start_date`, `status`, `user_id`) VALUE (3, 3, now(), 1, 1);
INSERT INTO position_attribute (`id`, `domain_id`, `entity_attribute_id`, `start_date`, `status`, `user_id`) VALUE (3, 3, 1, now(), 1, 1);
INSERT INTO position_value (`id`, `attribute_id`, `locale_id`, text) VALUE (3, 3, 1, 'РУКОВОДИТЕЛЬ РЕГИОНА');

INSERT INTO position (`id`, `object_id`, `start_date`, `status`, `user_id`) VALUE (4, 4, now(), 1, 1);
INSERT INTO position_attribute (`id`, `domain_id`, `entity_attribute_id`, `start_date`, `status`, `user_id`) VALUE (4, 4, 1, now(), 1, 1);
INSERT INTO position_value (`id`, `attribute_id`, `locale_id`, text) VALUE (4, 4, 1, 'ТОРГОВЫЙ ДИРЕКТОР');

INSERT INTO position (`id`, `object_id`, `start_date`, `status`, `user_id`) VALUE (5, 5, now(), 1, 1);
INSERT INTO position_attribute (`id`, `domain_id`, `entity_attribute_id`, `start_date`, `status`, `user_id`) VALUE (5, 5, 1, now(), 1, 1);
INSERT INTO position_value (`id`, `attribute_id`, `locale_id`, text) VALUE (5, 5, 1, 'АДМИНИСТРАТИВНЫЙ ДИРЕКТОР');

/* Worker */

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

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 8, 0);
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

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 21, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 21, 1, 'Пользователь'), (20, 21, 2, 'Пользователь');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 100, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 101, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 102, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 103, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 104, 0);

INSERT INTO `worker` (`id`, `object_id`, `status`, `left`, `right`, `level`) VALUE (1, 1, 4, 1, 2, 0);

/* Nomenclature */

INSERT INTO `entity` (`id`, `name`) VALUE (23, 'nomenclature');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (23, 1, 'Номенклатура'), (23, 2, 'Номенклатура');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (23, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (23, 1, 1, 'Название'), (23, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (23, 2, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (23, 2, 1, 'Код'), (23, 2, 2, 'Код');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (23, 3, 10, 1);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (23, 3, 1, 'Страны'), (23, 3, 2, 'Країни');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (23, 4, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (23, 4, 1, 'Тип номенклатуры'), (23, 4, 2, 'Тип номенклатури');

/* Storage */

INSERT INTO `entity` (`id`, `name`) VALUE (24, 'storage');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (24, 1, 'Склад'), (24, 2, 'Склад');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (24, 1, 11, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (24, 1, 1, 'Населенный пункт'), (24, 1, 2, 'Населений пункт');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (24, 2, 10, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (24, 2, 1, 'Ответственные'), (24, 2, 2, 'Відповідальні');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (24, 3, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (24, 3, 1, 'Тип'), (24, 3, 2, 'Тип');

/* Product */

INSERT INTO `entity` (`id`, `name`) VALUE (25, 'product');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (25, 1, 'Товар'), (25, 2, 'Товар');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (25, 1, 11, 23);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 1, 1, 'Номенклатура'), (25, 1, 2, 'Номенклатура');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 2, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 2, 1, 'Количество товара'), (25, 2, 2, 'Кількість товару');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 3, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 3, 1, 'Отправляется товаров'), (25, 3, 2, 'Відправляється товару');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 4, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 4, 1, 'Принимается товаров'), (25, 4, 2, 'Приймається товару');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 5, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 5, 1, 'Количество подарков'), (25, 5, 2, 'Кількість подарунків');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 6, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 6, 1, 'Отправляется подарков'), (25, 6, 2, 'Відправляється подарунків');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 7, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 7, 1, 'Принимается подарков'), (25, 7, 2, 'Приймається подарунків');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (25, 8, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (25, 8, 1, 'Резерв'), (25, 8, 2, 'Резерв');

/* Transaction */

INSERT INTO `entity` (`id`, `name`) VALUE (26, 'transaction');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (26, 1, 'Транзакция'), (26, 2, 'Транзакція');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 1, 11, 23);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 1, 1, 'Номенклатура'), (26, 1, 2, 'Номенклатура');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 2, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 2, 1, 'Количество'), (26, 2, 2, 'Кількість');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 3, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 3, 1, 'Тип транзакции'), (26, 3, 2, 'Тип транзакції');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 4, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 4, 1, 'Тип перемещения'), (26, 4, 2, 'Тип переміщення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 5, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 5, 1, 'Тип получателя'), (26, 5, 2, 'Тип одержувача');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 6, 11, 24);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 6, 1, 'Из склада'), (26, 6, 2, 'Зі складу');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 7, 11, 24);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 7, 1, 'В склад'), (26, 7, 2, 'В склад');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 8, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 8, 1, 'Сотрудник'), (26, 8, 2, 'Співробітник');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, reference_id) VALUES (26, 9, 11, 11);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 9, 1, 'Имя'), (26, 9, 2, 'Ім''я');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 10, 11, 12);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 10, 1, 'Отчество'), (26, 10, 2, 'По батькові');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 11, 11, 13);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 11, 1, 'Фамилия'), (26, 11, 2, 'Прізвище');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 12, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 12, 1, 'Серийный номер'), (26, 12, 2, 'Серійний номер');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 13, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 13, 1, 'Комментарии'), (26, 13, 2, 'Комментарии');

/* Type */

INSERT INTO storage_type (id, type) VALUES (1, 'real'), (2, 'virtual');
INSERT INTO transaction_type (id, type) VALUES (1, 'accept'), (2, 'sell'), (3, 'transfer'), (4, 'withdraw');
INSERT INTO transfer_type (id, type) VALUES (1, 'transfer'), (2, 'gift');
INSERT INTO recipient_type (id, type) VALUES (1, 'storage'), (2, 'worker'), (3, 'client');

/* Promotion */

INSERT INTO `entity` (`id`, `name`) VALUE (27, 'promotion');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (27, 1, 'Акция'), (27, 2, 'Акція');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (27, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 1, 1, 'Название'), (27, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (27, 2, 11, 1);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 2, 1, 'Страна'), (27, 2, 2, 'Країна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (27, 3, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 3, 1, 'Начало'), (27, 3, 2, 'Початок');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (27, 4, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 4, 1, 'Окончание'), (27, 4, 2, 'Закінчення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (27, 5, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 5, 1, 'Файл'), (27, 5, 2, 'Файл');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (27, 6, 11, 23);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 6, 1, 'Товары'), (27, 6, 2, 'Товари');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (27, 7, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 7, 1, 'Курс евро'), (27, 7, 2, 'Курс евро');

/* Sale */

INSERT INTO `entity` (`id`, `name`) VALUE (28, 'sale');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (28, 1, 'Продажа'), (28, 2, 'Продаж');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (28, 1, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 1, 1, 'Продавец'), (28, 1, 2, 'Продавець');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (28, 2, 11, 11);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 2, 1, 'Имя покупателя'), (28, 2, 2, 'Ім''я покупця');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (28, 3, 11, 12);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 3, 1, 'Отчество покупателя'), (28, 3, 2, 'По батькові покупця');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (28, 4, 11, 13);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 4, 1, 'Фамилия покупателя'), (28, 4, 2, 'Прізвище покупця');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 5, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 5, 1, 'Дата'), (28, 5, 2, 'Дата');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 6, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 6, 1, 'Тип продажи'), (28, 6, 2, 'Тип продажу');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 7, 3);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 7, 1, 'Заявка из САП'), (28, 7, 2, 'Заявка з САП');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (28, 8, 11, 27);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 8, 1, 'Акция'), (28, 8, 2, 'Акція');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (28, 9, 11, 24);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 9, 1, 'Склад'), (28, 9, 2, 'Склад');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 10, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 10, 1, 'Номер договора'), (28, 10, 2, 'Номер договору');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 11, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 11, 1, 'Сумма договора'), (28, 11, 2, 'Сума договору');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 12, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 12, 1, 'Сумма договора в локальной валюте'), (28, 12, 2, 'Сума договору в локальній валюті');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 13, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 13, 1, 'Рассрочка'), (28, 13, 2, 'Розстрочка');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (28, 14, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (28, 14, 1, 'Первый платеж'), (28, 14, 2, 'Перший платіж');

/* Sale Item*/

CALL createEntity(29, 'sale_item', 'Позиция продажи', 'Позиція продажу');
CALL createEntityAttributeWithReference(29, 1, 11, 23, 'Номенклатура', 'Номенклатура');
CALL createEntityAttribute(29, 2, 5, 'Количество', 'Кількість');
CALL createEntityAttribute(29, 3, 4, 'Цена', 'Ціна');
CALL createEntityAttribute(29, 4, 4, 'Сумма', 'Сума');
CALL createEntityAttribute(29, 5, 4, 'Стоимость балла', 'Вартість бала');
CALL createEntityAttribute(29, 6, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttributeWithReference(29, 7, 11, 32, 'Базовая цена', 'Базова ціна');
CALL createEntityAttributeWithReference(29, 8, 11, 36, 'Условия продаж', 'Умови продажу');

/* Currency */

INSERT INTO `entity` (`id`, `name`) VALUE (30, 'currency');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (30, 1, 'Валюта'), (30, 2, 'Валюта');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (30, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (30, 1, 1, 'Название'), (30, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (30, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (30, 2, 1, 'Краткое название'), (30, 2, 2, 'Коротка назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (30, 3, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (30, 3, 1, 'Символ'), (30, 3, 2, 'Символ');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (30, 4, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (30, 4, 1, 'Код'), (30, 4, 2, 'Код');


INSERT INTO currency (id, object_id, status) VALUES (1, 1, 1);
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status) VALUES (1, 1, 1, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (1, 1, 'Рубль');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status) VALUES (2, 1, 2, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (2, 1, 'руб.');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status, text) VALUES (3, 1, 3, 1, '₽');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status, text) VALUES (4, 1, 4, 1, 'RUB');

INSERT INTO currency (id, object_id, status) VALUES (2, 2, 1);
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status) VALUES (5, 2, 1, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (5, 1, 'Гривна'), (5, 2, 'Гривня');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status) VALUES (6, 2, 2, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (6, 1, 'грн.'), (6, 2, 'грн.');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status, text) VALUES (7, 2, 3, 1, '₴');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status, text) VALUES (8, 2, 4, 1, 'UAH');

INSERT INTO currency (id, object_id, status) VALUES (3, 3, 1);
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status) VALUES (9, 3, 1, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (9, 1, 'Евро'), (9, 2, 'Евро');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status) VALUES (10, 3, 2, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (10, 1, 'евро'), (10, 2, 'евро');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status, text) VALUES (11, 3, 3, 1, '€');
INSERT INTO currency_attribute (id, domain_id, entity_attribute_id, status, text) VALUES (12, 3, 4, 1, 'EUR');

/* Exchange rate */

INSERT INTO `entity` (`id`, `name`) VALUE (31, 'exchange_rate');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (31, 1, 'Курс валюты'), (31, 2, 'Курс валюті');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 1, 1, 'Название'), (31, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 2, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 2, 1, 'Код'), (31, 2, 2, 'Код');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (31, 3, 11, 30);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 3, 1, 'Базовая валюта'), (31, 3, 2, 'Базова валюта');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (31, 4, 11, 30);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 4, 1, 'Котируемая валюта'), (31, 4, 2, 'Котирувана валюта');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 5, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 5, 1, 'uri_xml');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 6, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 6, 1, 'xpath_name');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 7, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 7, 1, 'xpath_code');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 8, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 8, 1, 'xpath_date');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 9, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 9, 1, 'xpath_value');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 10, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 10, 1, 'Курс'), (31, 10, 2, 'Курс');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 11, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 11, 1, 'uri_date_param');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 12, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 12, 1, 'uri_date_format');


INSERT INTO exchange_rate (id, object_id, status) VALUES (1, 1, 1);
INSERT INTO exchange_rate_attribute (id, domain_id, entity_attribute_id, status) VALUES (1, 1, 1, 1);
INSERT INTO exchange_rate_value (attribute_id, locale_id, text) VALUES (1, 1, 'EUR/RUB (ЦБ РФ');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 2, 1, 'EUR/RUB');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, number) VALUES (1, 3, 1, 3);
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, number) VALUES (1, 4, 1, 1);
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 5, 1, 'http://www.cbr.ru/scripts/XML_daily.asp');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 6, 1, '/ValCurs/Valute[@ID=\'R01239\']/Name/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 7, 1, '/ValCurs/Valute[@ID=\'R01239\']/CharCode/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 8, 1, '/ValCurs/@Date');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 9, 1, '/ValCurs/Valute[@ID=\'R01239\']/Value/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 11, 1, 'date_req');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (1, 12, 1, 'dd/MM/yyyy');

INSERT INTO exchange_rate (id, object_id, status) VALUES (2, 2, 1);
INSERT INTO exchange_rate_attribute (id, domain_id, entity_attribute_id, status) VALUES (10, 2, 1, 1);
INSERT INTO exchange_rate_value (attribute_id, locale_id, text) VALUES (10, 1, 'EUR/UAH (НБУ)');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 2, 1, 'EUR/UAH');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, number) VALUES (2, 3, 1, 3);
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, number) VALUES (2, 4, 1, 2);
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 5, 1, 'https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=EUR&date=');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 6, 1, 'exchange/currency/txt/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 7, 1, 'exchange/currency/cc/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 8, 1, 'exchange/currency/exchangedate/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 9, 1, 'exchange/currency/rate/text()');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 11, 1, 'date');
INSERT INTO exchange_rate_attribute (domain_id, entity_attribute_id, status, text) VALUES (2, 12, 1, 'yyyyMMdd');

/* Base price */

INSERT INTO `entity` (`id`, `name`) VALUE (32, 'price');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (32, 1, 'Базовая цена'), (32, 2, 'Базова ціна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (32, 1, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 1, 1, 'Дата начала'), (32, 1, 2, 'Дата початку');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (32, 2, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 2, 1, 'Дата окончания'), (32, 2, 2, 'Дата закінчення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (32, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 3, 1, 'Цена'), (32, 3, 2, 'Ціна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (32, 4, 11, 1);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 4, 1, 'Страна'), (32, 4, 2, 'Країна');

CALL createEntity(33, 'rule', 'Правило', 'Правило');

CALL createEntity(34, 'rule_condition', 'Условие правила', 'Умова правила');
CALL createEntityAttribute(34, 1, 5, 'Индекс', 'Індекс');
CALL createEntityAttribute(34, 2, 5, 'Тип условия', 'Тип умови');
CALL createEntityAttribute(34, 3, 5, 'Тип значения', 'Тип значення');
CALL createEntityAttribute(34, 4, 5, 'Компаратор', 'Компаратор');
CALL createEntityAttribute(34, 5, 2, 'Условие', 'Умова');

CALL createEntity(35, 'rule_action', 'Действие правила', 'Дії правила');
CALL createEntityAttribute(35, 1, 5, 'Индекс', 'Індекс');
CALL createEntityAttribute(35, 2, 5, 'Тип действия', 'Тип дії');
CALL createEntityAttribute(35, 3, 5, 'Тип значения', 'Тип значення');
CALL createEntityAttribute(35, 4, 5, 'Компаратор', 'Компаратор');
CALL createEntityAttribute(35, 5, 2, 'Действие', 'Дія');

CALL createEntity(36, 'sale_decision', 'Условие продаж', 'Умова продажів');
CALL createEntityAttribute(36, 1, 2, 'Название', 'Назва');
CALL createEntityAttribute(36, 2, 6, 'Дата начала', 'Дата початку');
CALL createEntityAttribute(36, 3, 6, 'Дата окончания', 'Дата закінчення');
CALL createEntityAttributeWithReference(36, 4, 11, 1, 'Страна', 'Країна');
CALL createEntityAttributeWithReference(36, 5, 10, 23, 'Товары', 'Товари');

CALL createEntity(37, 'card', 'Карта', 'Карта');
CALL createEntityAttribute(37, 1, 2, 'Номер карты', 'Номер картки');
CALL createEntityAttribute(37, 2, 6, 'Дата создания', 'Дата створення');
CALL createEntityAttributeWithReference(37, 3, 11, 20, 'Сотрудник', 'Сотрудник');
CALL createEntityAttribute(37, 4, 5, 'Индекс', 'Індекс');


-- Update

INSERT INTO `update` (`version`) VALUE ('20190605_1.0.19');