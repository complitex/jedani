/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
-- --------------------------------------------------------------------------------------------------------------------
-- Common
-- --------------------------------------------------------------------------------------------------------------------

-- ------------------------------
-- User
-- ------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `login` VARCHAR(64) NOT NULL COMMENT 'Логин',
  `password` VARCHAR(64) NOT NULL COMMENT 'Пароль',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_login` (`login`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Пользователь';

-- ------------------------------
-- Usergroup
-- ------------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE  `user_group` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `login` VARCHAR(45) NOT NULL COMMENT 'Имя пользователя',
  `name` VARCHAR(45) NOT NULL COMMENT 'Название группы',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`login`, `name`),
  CONSTRAINT `fk_user_group__user` FOREIGN KEY (`login`) REFERENCES `user` (`login`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Группа пользователя';

INSERT INTO `user`(login, password) value ('admin', sha2('admin', 256));
INSERT INTO `user_group` (login, name) value ('admin', 'AUTHORIZED');
INSERT INTO `user_group` (login, name) value ('admin', 'ADMINISTRATORS');

-- ------------------------------
-- User history
-- ------------------------------
CREATE TABLE `user_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `user_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор пользователя',
  `login` VARCHAR(64) COMMENT 'Логин',
  `password` VARCHAR(64) COMMENT 'Пароль',
  `group` VARCHAR(512) COMMENT 'Группы пользователя',
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
  `worker_id` BIGINT(20) COMMENT 'Идентификатор сотрудника',
  PRIMARY KEY (`id`),
  KEY `key_user_id` (`user_id`),
  KEY `key_login` (`login`),
  KEY `key_group`(`group`(128)),
  KEY `key_date` (`date`),
  KEY `key_worker_id` (`worker_id`),
  CONSTRAINT `fk_user_history__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_user_history__worker` FOREIGN KEY (`worker_id`) REFERENCES `worker` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'История пользователя';

-- ------------------------------
-- Locale
-- ------------------------------

DROP TABLE IF EXISTS `locale`;
CREATE TABLE `locale` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор локали',
  `locale` VARCHAR(2) NOT NULL COMMENT 'Код локали',
  `system` TINYINT(1) NOT NULL default 0 COMMENT 'Является ли локаль системной',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_locale` (`locale`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локаль';

INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (1, 'RU', 1);
INSERT INTO `locale`(`id`, `locale`, `system`) VALUES (2, 'UA', 0);

-- ------------------------------
-- Update
-- ------------------------------

DROP TABLE IF EXISTS `update`;
CREATE TABLE `update` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `version` VARCHAR(64) NOT NULL COMMENT 'Версия',
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Обновление базы данных';


-- ------------------------------
-- Entity
-- ------------------------------

DROP TABLE IF EXISTS `entity`;
CREATE TABLE `entity` (
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор сущности',
  `name` VARCHAR(100) NOT NULL COMMENT 'Название сущности',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity` (`name`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Сущность';

DROP TABLE IF EXISTS entity_value_type;
CREATE TABLE `entity_value_type` (
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута',
  `value_type` VARCHAR(100) NOT NULL COMMENT 'Тип значения атрибута',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип значения атрибута';

DROP TABLE IF EXISTS `entity_attribute`;
CREATE TABLE `entity_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `entity_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор сущности',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `start_date` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL default NULL COMMENT 'Дата окончания периода действия атрибута',
  `value_type_id` BIGINT(20) COMMENT  'Тип значения атрибута',
  `reference_id` BIGINT(20) COMMENT  'Внешний ключ',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`entity_attribute_id`, `entity_id`),
  KEY `key_entity_id` (`entity_id`),
  KEY `key_value_type_id` (`value_type_id`),
  CONSTRAINT `fk_attribute_type__entity` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_entity_attribute__entity_value_type` FOREIGN KEY (`value_type_id`) REFERENCES entity_value_type (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип атрибута сущности';

DROP TABLE IF EXISTS `entity_value`;
CREATE TABLE `entity_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `entity_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа аттрибута',
  `entity_attribute_id` BIGINT(20) NULL COMMENT 'Идентификатор типа аттрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`entity_id`, `entity_attribute_id`, `locale_id`),
  KEY `key_entity_id` (`entity_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_entity_value__entity` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_entity_value__entity_attribute` FOREIGN KEY (`entity_attribute_id`, `entity_id`)
    REFERENCES `entity_attribute` (`entity_attribute_id`, `entity_id`),
  CONSTRAINT `fk_entity_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация';

INSERT INTO entity_value_type (id, value_type) VALUE (0, 'text_value');
INSERT INTO entity_value_type (id, value_type) VALUE (1, 'number_value');
INSERT INTO entity_value_type (id, value_type) VALUE (2, 'string');
INSERT INTO entity_value_type (id, value_type) VALUE (3, 'boolean');
INSERT INTO entity_value_type (id, value_type) VALUE (4, 'decimal');
INSERT INTO entity_value_type (id, value_type) VALUE (5, 'integer');
INSERT INTO entity_value_type (id, value_type) VALUE (6, 'date');
INSERT INTO entity_value_type (id, value_type) VALUE (10, 'entity_value');
INSERT INTO entity_value_type (id, value_type) VALUE (11, 'entity');

-- ------------------------------
-- Permission
-- ------------------------------

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `permission_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор права доступа',
  `table` VARCHAR(64) NOT NULL COMMENT 'Таблица',
  `entity` VARCHAR(64) NOT NULL COMMENT 'Сущность',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`permission_id`, `entity`, `object_id`),
  KEY `key_permission_id` (`permission_id`),
  KEY `key_table` (`table`),
  KEY `key_entity` (`entity`),
  KEY `key_object_id` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Права доступа';

-- ------------------------------
-- Procedure
-- ------------------------------

DELIMITER //

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

CREATE PROCEDURE createDomainTables (IN entityName VARCHAR(64) CHARSET utf8, IN entityDescription VARCHAR(256) CHARSET utf8)
BEGIN
    SET @createDomain = CONCAT('
        CREATE TABLE `', entityName, '`
        (
            `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT ''Идентификатор'',
            `object_id`        BIGINT(20) NOT NULL COMMENT ''Идентификатор объекта'',
            `parent_id`        BIGINT(20) COMMENT ''Идентификатор родительского объекта'',
            `parent_entity_id` BIGINT(20) COMMENT ''Идентификатор сущности родительского объекта'',
            `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Дата начала периода действия объекта'',
            `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT ''Дата окончания периода действия объекта'',
            `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT ''Статус'',
            `permission_id`    BIGINT(20) NULL COMMENT ''Ключ прав доступа к объекту'',
            `user_id`          BIGINT(20) NULL COMMENT ''Идентифитактор пользователя'',
            PRIMARY KEY (`id`),
            UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
            KEY `key_object_id` (`object_id`),
            KEY `key_parent_id` (`parent_id`),
            KEY `key_parent_entity_id` (`parent_entity_id`),
            KEY `key_start_date` (`start_date`),
            KEY `key_end_date` (`end_date`),
            KEY `key_status` (`status`),
            KEY `key_permission_id` (`permission_id`),
            CONSTRAINT `fk_', entityName, '__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
            CONSTRAINT `fk_', entityName, '__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
            CONSTRAINT `fk_', entityName, '__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ''';');

    PREPARE QUERY FROM @createDomain; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @createAttribute = CONCAT('
        CREATE TABLE `', entityName, '_attribute`
        (
            `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT ''Идентификатор'',
            `domain_id`           BIGINT(20) NOT NULL COMMENT ''Идентификатор домена'',
            `entity_attribute_id` BIGINT(20) NOT NULL COMMENT ''Идентификатор типа атрибута'',
            `text`                VARCHAR(255) COMMENT ''Текст'',
            `number`              BIGINT(20) COMMENT ''Число'',
            `date`                DATETIME COMMENT ''Дата'',
            `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''Дата начала периода действия атрибута'',
            `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT ''Дата окончания периода действия атрибута'',
            `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT ''Статус'',
            `user_id`             BIGINT(20) NULL COMMENT ''Идентифитактор пользователя'',
            PRIMARY KEY (`id`),
            KEY `key_domain_id` (`domain_id`),
            KEY `key_entity_attribute_id` (`entity_attribute_id`),
            KEY `key_text` (`text`),
            KEY `key_number` (`number`),
            KEY `key_date` (`date`),
            KEY `key_start_date` (`start_date`),
            KEY `key_end_date` (`end_date`),
            KEY `key_status` (`status`),
            CONSTRAINT `fk_', entityName, '_attribute__', entityName, '` FOREIGN KEY (`domain_id`) REFERENCES `', entityName, '` (`id`),
            CONSTRAINT `fk_', entityName, '_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ' - Аттрибуты'';');

    PREPARE QUERY FROM @createAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @createValue = CONCAT('
        CREATE TABLE `', entityName, '_value`
        (
            `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT ''Идентификатор'',
            `attribute_id` BIGINT(20) NOT NULL COMMENT ''Идентификатор атрибута'',
            `locale_id`    BIGINT(20) COMMENT ''Идентификатор локали'',
            `text`         VARCHAR(1000) COMMENT ''Текстовое значение'',
            `number`       BIGINT(20) COMMENT ''Числовое значение'',
            PRIMARY KEY (`id`),
            UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
            KEY `key_attribute_id` (`attribute_id`),
            KEY `key_locale` (`locale_id`),
            KEY `key_value` (`text`(128)),
            CONSTRAINT `fk_', entityName, '_value__', entityName, '_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `', entityName, '_attribute` (`id`),
            CONSTRAINT `fk_', entityName, '_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
        ) ENGINE = InnoDB
          CHARSET = utf8
          COLLATE = utf8_unicode_ci COMMENT ''', entityDescription, ' - Значения атрибутов'';');

    PREPARE QUERY FROM @createValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

-- ---------------------------
-- Setting
-- ---------------------------

DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_setting__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_setting__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_setting__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Настройки';

DROP TABLE IF EXISTS `setting_attribute`;
CREATE TABLE `setting_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `date` DATETIME COMMENT 'Дата',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_setting_attribute__setting` FOREIGN KEY (`domain_id`) REFERENCES `setting`(`id`),
  CONSTRAINT `fk_setting_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
  CONSTRAINT `fk_setting_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты настройки';

DROP TABLE IF EXISTS `setting_value`;
CREATE TABLE `setting_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_setting_value__setting_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `setting_attribute` (`id`),
  CONSTRAINT `fk_setting_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Значения атрибутов настройки';

INSERT INTO `entity` (`id`, `name`) VALUE (0, 'setting');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (0, 1, 'Настройки'), (0, 2, 'Налаштування');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (0, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (0, 1, 1, 'Значение'), (0, 1, 2, 'Значення');

INSERT INTO `setting` (`id`, `object_id`) VALUE (1, 1);
INSERT INTO `setting_attribute` (`domain_id`, `entity_attribute_id`, `text`) VALUES (1, 1, '/jedani/data/promotion/');

-- --------------------------------------------------------------------------------------------------------------------
-- Address
-- --------------------------------------------------------------------------------------------------------------------

-- ------------------------------
-- Country
-- ------------------------------
DROP TABLE IF EXISTS `country`;
CREATE TABLE `country` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_country__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_country__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_country__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Страна';

DROP TABLE IF EXISTS `country_attribute`;
CREATE TABLE `country_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_country_attribute__country` FOREIGN KEY (`domain_id`) REFERENCES `country`(`id`),
  CONSTRAINT `fk_country_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты страны';

DROP TABLE IF EXISTS `country_value`;
CREATE TABLE `country_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_country_value__country_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `country_attribute` (`id`),
  CONSTRAINT `fk_country_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов страны';

INSERT INTO `entity`(`id`, `name`) VALUES (1, 'country');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (1, 1, 'Страна'), (1, 2, 'Країна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (1, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 1, 1, 'Название'), (1, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (1, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 2, 1, 'Краткое название'), (1, 2, 2, 'Коротка назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (1, 3, 11, 30);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 3, 1, 'Нац.валюта'), (1, 3, 2, 'Нац.валюті');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (1, 4, 11, 31);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 4, 1, 'Курс балла'), (1, 4, 2, 'Курс балла');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (1, 10, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (1, 10, 1, 'Менеджер'), (1, 10, 2, 'Менеджер');


-- ------------------------------
-- Region
-- ------------------------------
DROP TABLE IF EXISTS `region`;
CREATE TABLE `region` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_region__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_region__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_region__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Регион';

DROP TABLE IF EXISTS `region_attribute`;
CREATE TABLE `region_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_region_attribute__region` FOREIGN KEY (`domain_id`) REFERENCES `region`(`id`),
  CONSTRAINT `fk_region_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты региона';

DROP TABLE IF EXISTS `region_value`;
CREATE TABLE `region_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_region_value__region_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `region_attribute` (`id`),
  CONSTRAINT `fk_region_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов региона';

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

-- ------------------------------
-- City Type
-- ------------------------------

DROP TABLE IF EXISTS `city_type`;
CREATE TABLE `city_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_city_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_city_type__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип населенного пункта';

DROP TABLE IF EXISTS `city_type_attribute`;
CREATE TABLE `city_type_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_type_attribute__city_type` FOREIGN KEY (`domain_id`) REFERENCES `city_type`(`id`),
  CONSTRAINT `fk_city_type_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты типа населенного пункта';

DROP TABLE IF EXISTS `city_type_value`;
CREATE TABLE `city_type_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_city_type_value__city_type_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `city_type_attribute` (`id`),
  CONSTRAINT `fk_city_type_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов типа населенного пункта';

INSERT INTO `entity`(`id`, `name`) VALUES (3, 'city_type');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (3, 1, 'Тип населенного пункта'), (3, 2, 'Тип населеного пункту');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (3, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (3, 1, 1, 'Название'), (3, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (3, 2, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (3, 2, 1, 'Краткое название'), (3, 2, 2, 'Коротка назва');

-- ------------------------------
-- City
-- ------------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_city__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_city__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Населенный пункт';

DROP TABLE IF EXISTS `city_attribute`;
CREATE TABLE `city_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_attribute__city` FOREIGN KEY (`domain_id`) REFERENCES `city`(`id`),
  CONSTRAINT `fk_city_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты населенного пункта';

DROP TABLE IF EXISTS `city_value`;
CREATE TABLE `city_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_city_value__city_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `city_attribute` (`id`),
  CONSTRAINT `fk_city_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов населенного пункта';

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

-- --------------------------------------------------------------------------------------------------------------------
-- Domain
-- --------------------------------------------------------------------------------------------------------------------

-- ------------------------------
-- Last Name
-- ------------------------------

DROP TABLE IF EXISTS `last_name`;
CREATE TABLE `last_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_last_name__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_last_name__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_last_name__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Фамилия';

DROP TABLE IF EXISTS `last_name_attribute`;
CREATE TABLE `last_name_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_last_name_attribute__last_name` FOREIGN KEY (`domain_id`) REFERENCES `last_name`(`id`),
  CONSTRAINT `fk_last_name_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты фамилии';

DROP TABLE IF EXISTS `last_name_value`;
CREATE TABLE `last_name_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_last_name_value__last_name_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `last_name_attribute` (`id`),
  CONSTRAINT `fk_last_name_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов фамилии';

INSERT INTO `entity`(`id`, `name`) VALUES (13, 'last_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (13, 1, 'Фамилия'), (13, 2, 'Прізвище');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (13, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (13, 1, 1, 'Фамилия'), (13, 1, 2, 'Прізвище');

-- ------------------------------
-- First Name
-- ------------------------------

DROP TABLE IF EXISTS `first_name`;
CREATE TABLE `first_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_first_name__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_first_name__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_first_name__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Имя';

DROP TABLE IF EXISTS `first_name_attribute`;
CREATE TABLE `first_name_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_first_name_attribute__first_name` FOREIGN KEY (`domain_id`) REFERENCES `first_name`(`id`),
  CONSTRAINT `fk_first_name_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты имени';

DROP TABLE IF EXISTS `first_name_value`;
CREATE TABLE `first_name_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_first_name_value__first_name_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `first_name_attribute` (`id`),
  CONSTRAINT `fk_first_name_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов имени';

INSERT INTO `entity`(`id`, `name`) VALUES (11, 'first_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (11, 1, 'Имя'), (11, 2, 'Ім''я');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (11, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (11, 1, 1, 'Имя'), (11, 1, 2, 'Ім''я');

-- ------------------------------
-- Middle Name
-- ------------------------------

DROP TABLE IF EXISTS `middle_name`;
CREATE TABLE `middle_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_middle_name__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_middle_name__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_middle_name__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Отчество';

DROP TABLE IF EXISTS `middle_name_attribute`;
CREATE TABLE `middle_name_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_middle_name_attribute__middle_name` FOREIGN KEY (`domain_id`) REFERENCES `middle_name`(`id`),
  CONSTRAINT `fk_middle_name_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты отчества';

DROP TABLE IF EXISTS `middle_name_value`;
CREATE TABLE `middle_name_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_middle_name_value__middle_name_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `middle_name_attribute` (`id`),
  CONSTRAINT `fk_middle_name_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов отчества';

INSERT INTO `entity`(`id`, `name`) VALUES (12, 'middle_name');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (12, 1, 'Отчество'), (12, 2, 'По батькові');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (12, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (12, 1, 1, 'Отчество'), (12, 1, 2, 'По батькові');

-- ------------------------------
-- Worker
-- ------------------------------
DROP TABLE IF EXISTS `worker`;
CREATE TABLE `worker` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  `left` BIGINT(20) COMMENT 'Левый индекс иерархии',
  `right` BIGINT(20) COMMENT 'Правый индекс иерархии',
  `level` BIGINT(20) COMMENT 'Глубина иерархии',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  KEY `key_left` (`left`),
  KEY `key_right` (`right`),
  KEY `key_level` (`level`),
  CONSTRAINT `fk_worker__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_worker__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_worker__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Профиль сотрудника';

DROP TABLE IF EXISTS `worker_attribute`;
CREATE TABLE `worker_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `date` DATETIME COMMENT 'Дата',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_worker_attribute__worker` FOREIGN KEY (`domain_id`) REFERENCES `worker`(`id`),
  CONSTRAINT `fk_worker_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты сотрудника';

DROP TABLE IF EXISTS `worker_value`;
CREATE TABLE `worker_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  `number` BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_text` (`text`(128)),
  KEY `key_number` (`number`),
  CONSTRAINT `fk_worker_value__worker_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `worker_attribute` (`id`),
  CONSTRAINT `fk_worker_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Значения атрибутов сотрудника';

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

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (20, 7, 5, 4);
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

CALL createEntityAttributeWithReference(20, 22, 11, 38, 'Ранг', 'Ранг');
CALL createEntityAttribute(20, 23, 4, 'Баллы', 'Бали');
CALL createEntityAttribute(20, 24, 2, 'Приглашение', 'Запрошення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 100, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 101, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 102, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 103, 0);
INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 104, 0);

INSERT INTO `worker` (`id`, `object_id`, `status`, `left`, `right`, `level`) VALUE (1, 1, 4, 1, 2, 0);

-- ------------------------------
-- MK Status
-- ------------------------------

DROP TABLE IF EXISTS `mk_status`;
CREATE TABLE `mk_status` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_mk_status__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_mk_status__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_mk_status__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'МК статус';

DROP TABLE IF EXISTS `mk_status_attribute`;
CREATE TABLE `mk_status_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_mk_status_attribute__mk_status` FOREIGN KEY (`domain_id`) REFERENCES `mk_status`(`id`),
  CONSTRAINT `fk_mk_status_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты МК статуса';

DROP TABLE IF EXISTS `mk_status_value`;
CREATE TABLE `mk_status_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_mk_status_value__mk_status_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `mk_status_attribute` (`id`),
  CONSTRAINT `fk_mk_status_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов МК статуса';

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

-- ------------------------------
-- Position
-- ------------------------------

DROP TABLE IF EXISTS `position`;
CREATE TABLE `position` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_position__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_position__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_position__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Должности';

DROP TABLE IF EXISTS `position_attribute`;
CREATE TABLE `position_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_position_attribute__position` FOREIGN KEY (`domain_id`) REFERENCES `position`(`id`),
  CONSTRAINT `fk_position_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты должности';

DROP TABLE IF EXISTS `position_value`;
CREATE TABLE `position_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_position_value__position_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `position_attribute` (`id`),
  CONSTRAINT `fk_position_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов должности';

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

-- ---------------------------
-- Nomenclature
-- ---------------------------

DROP TABLE IF EXISTS `nomenclature`;
CREATE TABLE `nomenclature` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_nomenclature__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_nomenclature__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_nomenclature__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Номенклатура';

DROP TABLE IF EXISTS `nomenclature_attribute`;
CREATE TABLE `nomenclature_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_nomenclature_attribute__nomenclature` FOREIGN KEY (`domain_id`) REFERENCES `nomenclature`(`id`),
  CONSTRAINT `fk_nomenclature_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты номенклатуры';

DROP TABLE IF EXISTS `nomenclature_value`;
CREATE TABLE `nomenclature_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  `number` BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  KEY `key_number` (`number`),
  CONSTRAINT `fk_nomenclature_value__nomenclature_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `nomenclature_attribute` (`id`),
  CONSTRAINT `fk_nomenclature_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов номенклатуры';

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

-- ---------------------------
-- Storage
-- ---------------------------

DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_storage__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_storage__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_storage__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Склад';

DROP TABLE IF EXISTS `storage_attribute`;
CREATE TABLE `storage_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_storage_attribute__storage` FOREIGN KEY (`domain_id`) REFERENCES `storage`(`id`),
  CONSTRAINT `fk_storage_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты склада';

DROP TABLE IF EXISTS `storage_value`;
CREATE TABLE `storage_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  `number` BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  KEY `key_number` (`number`),
  CONSTRAINT `fk_storage_value__storage_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `storage_attribute` (`id`),
  CONSTRAINT `fk_storage_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов склада';

INSERT INTO `entity` (`id`, `name`) VALUE (24, 'storage');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (24, 1, 'Склад'), (24, 2, 'Склад');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (24, 1, 11, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (24, 1, 1, 'Населенный пункт'), (24, 1, 2, 'Населений пункт');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (24, 2, 10, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (24, 2, 1, 'Ответственные'), (24, 2, 2, 'Відповідальні');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (24, 3, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (24, 3, 1, 'Тип'), (24, 3, 2, 'Тип');

-- ---------------------------
-- Storage Type
-- ---------------------------

DROP TABLE IF EXISTS storage_type;
CREATE TABLE storage_type (
                              id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
                              `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
                              PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип склада';

INSERT INTO storage_type (id, type) VALUES (1, 'real'), (2, 'virtual');


-- ---------------------------
-- Product
-- ---------------------------

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_product__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_product__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_product__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Товар';

DROP TABLE IF EXISTS `product_attribute`;
CREATE TABLE `product_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_product_attribute__product` FOREIGN KEY (`domain_id`) REFERENCES `product`(`id`),
  CONSTRAINT `fk_product_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты товара';

DROP TABLE IF EXISTS `product_value`;
CREATE TABLE `product_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_product_value__product_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `product_attribute` (`id`),
  CONSTRAINT `fk_product_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов товара';

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

-- ---------------------------
-- Transfer
-- ---------------------------

DROP TABLE IF EXISTS `transfer`;
CREATE TABLE `transfer` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_transfer__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_transfer__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_transfer__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Транзакция';

DROP TABLE IF EXISTS `transfer_attribute`;
CREATE TABLE `transfer_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `date` DATETIME COMMENT 'Дата',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_transfer_attribute__transfer` FOREIGN KEY (`domain_id`) REFERENCES `transfer`(`id`),
  CONSTRAINT `fk_transfer_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты транзакции';

DROP TABLE IF EXISTS `transfer_value`;
CREATE TABLE `transfer_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_transfer_value__transfer_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `transfer_attribute` (`id`),
  CONSTRAINT `fk_transfer_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Значения атрибутов транзакции';

INSERT INTO `entity` (`id`, `name`) VALUE (26, 'transfer');
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

-- ---------------------------
-- Transfer Type
-- ---------------------------

DROP TABLE IF EXISTS transfer_type;
CREATE TABLE transfer_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип транзакции';

INSERT INTO transfer_type (id, type) VALUES (1, 'accept'), (2, 'sell'), (3, 'relocation'), (4, 'withdraw');

-- ---------------------------
-- Transfer Relocation Type
-- ---------------------------

DROP TABLE IF EXISTS transfer_relocation_type;
CREATE TABLE transfer_relocation_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип перемещения';

INSERT INTO transfer_relocation_type (id, type) VALUES (1, 'relocation'), (2, 'gift');

-- ---------------------------
-- Transfer Recipient Type
-- ---------------------------

DROP TABLE IF EXISTS transfer_recipient_type;
CREATE TABLE transfer_recipient_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип получателя';

INSERT INTO transfer_recipient_type (id, type) VALUES (1, 'storage'), (2, 'worker'), (3, 'client');

-- ---------------------------
-- Promotion
-- ---------------------------

DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id` BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`,`status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_promotion__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_promotion__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_promotion__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Акция';

DROP TABLE IF EXISTS `promotion_attribute`;
CREATE TABLE `promotion_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `domain_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `date` DATETIME COMMENT 'Дата',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_promotion_attribute__promotion` FOREIGN KEY (`domain_id`) REFERENCES `promotion`(`id`),
  CONSTRAINT `fk_promotion_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты акции';

DROP TABLE IF EXISTS `promotion_value`;
CREATE TABLE `promotion_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  `number` BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_promotion_value__promotion_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `promotion_attribute` (`id`),
  CONSTRAINT `fk_promotion_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Значения атрибутов акции';

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
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (27, 7, 1, 'Курс балла'), (27, 7, 2, 'Курс балла');

-- ---------------------------
-- Sale
-- ---------------------------

DROP TABLE IF EXISTS `sale`;
CREATE TABLE `sale`
(
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_sale__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_sale__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_sale__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Продажа';

DROP TABLE IF EXISTS `sale_attribute`;
CREATE TABLE `sale_attribute`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `domain_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text`                VARCHAR(255) COMMENT 'Текст',
  `number`              BIGINT(20) COMMENT 'Число',
  `date`                DATETIME COMMENT 'Дата',
  `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_sale_attribute__sale` FOREIGN KEY (`domain_id`) REFERENCES `sale` (`id`),
  CONSTRAINT `fk_sale_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты продажи';

DROP TABLE IF EXISTS `sale_value`;
CREATE TABLE `sale_value`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
  `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
  `number`       BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_sale_value__sale_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `sale_attribute` (`id`),
  CONSTRAINT `fk_sale_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов продажи';

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

CALL createEntityAttribute(28, 15, 5, 'Статус', 'Статус');
CALL createEntityAttribute(28, 16, 3, 'Для себя', 'Для себе');
CALL createEntityAttributeWithReference(28, 17, 11, 20, 'Менеджерский Майкук бонус', 'Менеджерський Майкук бонус');
CALL createEntityAttributeWithReference(28, 18, 11, 20, 'Кулінарний практикум', 'Кулінарний практикум');
CALL createEntityAttribute(28, 19, 4, 'Сумма вознаграждения', 'Сума винагороди');
CALL createEntityAttribute(28, 20, 4, 'Сумма вознаграждения ММБ', 'Сума винагороди ММБ');
CALL createEntityAttribute(28, 21, 4, 'Сумма вознаграждения КП', 'Сума ММБ винагороди КП');
CALL createEntityAttribute(28, 22, 3, 'Комиссия изъята', 'Комісія вилучена');
CALL createEntityAttributeWithReference(28, 23, 11, 42, 'Операционный месяц', 'Операційний місяць');

-- ---------------------------
-- Sale Item
-- ---------------------------

DROP TABLE IF EXISTS `sale_item`;
CREATE TABLE `sale_item`
(
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_sale_item__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_sale_item__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_sale_item__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Позиция продажи';

DROP TABLE IF EXISTS `sale_item_attribute`;
CREATE TABLE `sale_item_attribute`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `domain_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text`                VARCHAR(255) COMMENT 'Текст',
  `number`              BIGINT(20) COMMENT 'Число',
  `date`                DATETIME COMMENT 'Дата',
  `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_sale_item_attribute__sale_item` FOREIGN KEY (`domain_id`) REFERENCES `sale_item` (`id`),
  CONSTRAINT `fk_sale_item_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты позиции продажи';

DROP TABLE IF EXISTS `sale_item_value`;
CREATE TABLE `sale_item_value`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
  `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
  `number`       BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_sale_item_value__sale_item_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `sale_item_attribute` (`id`),
  CONSTRAINT `fk_sale_item_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов позиции продажи';

CALL createEntity(29, 'sale_item', 'Позиция продажи', 'Позиція продажу');
CALL createEntityAttributeWithReference(29, 1, 11, 23, 'Номенклатура', 'Номенклатура');
CALL createEntityAttribute(29, 2, 5, 'Количество', 'Кількість');
CALL createEntityAttribute(29, 3, 4, 'Цена', 'Ціна');
CALL createEntityAttribute(29, 4, 4, 'Сумма', 'Сума');
CALL createEntityAttribute(29, 5, 4, 'Стоимость балла', 'Вартість бала');
CALL createEntityAttribute(29, 6, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttribute(29, 7, 4, 'Базовая цена', 'Базова ціна');
CALL createEntityAttributeWithReference(29, 8, 11, 36, 'Условия продаж', 'Умови продажу');

-- ---------------------------
-- Currency
-- ---------------------------

DROP TABLE IF EXISTS `currency`;
CREATE TABLE `currency`
(
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_currency__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_currency__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_currency__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Валюта';

DROP TABLE IF EXISTS `currency_attribute`;
CREATE TABLE `currency_attribute`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `domain_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text`                VARCHAR(255) COMMENT 'Текст',
  `number`              BIGINT(20) COMMENT 'Число',
  `date`                DATETIME COMMENT 'Дата',
  `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_currency_attribute__currency` FOREIGN KEY (`domain_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `fk_currency_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты валюты';

DROP TABLE IF EXISTS `currency_value`;
CREATE TABLE `currency_value`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
  `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
  `number`       BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_currency_value__currency_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `currency_attribute` (`id`),
  CONSTRAINT `fk_currency_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов валюты';

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

-- ---------------------------
-- Exchange rate
-- ---------------------------

DROP TABLE IF EXISTS `exchange_rate`;
CREATE TABLE `exchange_rate`
(
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_exchange_rate__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_exchange_rate__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_exchange_rate__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Курсы валют';

DROP TABLE IF EXISTS `exchange_rate_attribute`;
CREATE TABLE `exchange_rate_attribute`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `domain_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text`                VARCHAR(255) COMMENT 'Текст',
  `number`              BIGINT(20) COMMENT 'Число',
  `date`                DATETIME COMMENT 'Дата',
  `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_domain_id` (`domain_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_exchange_rate_attribute__exchange_rate` FOREIGN KEY (`domain_id`) REFERENCES `exchange_rate` (`id`),
  CONSTRAINT `fk_exchange_rate_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты курсов валют';

DROP TABLE IF EXISTS `exchange_rate_value`;
CREATE TABLE `exchange_rate_value`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
  `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
  `number`       BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_exchange_rate_value__exchange_rate_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `exchange_rate_attribute` (`id`),
  CONSTRAINT `fk_exchange_rate_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов курсов валют';

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

-- ---------------------------
-- Price
-- ---------------------------

DROP TABLE IF EXISTS `price`;
CREATE TABLE `price`
(
    `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
    `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
    `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
    `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
    `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
    `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
    `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
    `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
    KEY `key_object_id` (`object_id`),
    KEY `key_parent_id` (`parent_id`),
    KEY `key_parent_entity_id` (`parent_entity_id`),
    KEY `key_start_date` (`start_date`),
    KEY `key_end_date` (`end_date`),
    KEY `key_status` (`status`),
    KEY `key_permission_id` (`permission_id`),
    CONSTRAINT `fk_price__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
    CONSTRAINT `fk_price__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
    CONSTRAINT `fk_price__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Базовая цена';

DROP TABLE IF EXISTS `price_attribute`;
CREATE TABLE `price_attribute`
(
    `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `domain_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор домена',
    `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
    `text`                VARCHAR(255) COMMENT 'Текст',
    `number`              BIGINT(20) COMMENT 'Число',
    `date`                DATETIME COMMENT 'Дата',
    `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
    `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
    `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
    `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
    PRIMARY KEY (`id`),
    KEY `key_domain_id` (`domain_id`),
    KEY `key_entity_attribute_id` (`entity_attribute_id`),
    KEY `key_text` (`text`),
    KEY `key_number` (`number`),
    KEY `key_date` (`date`),
    KEY `key_start_date` (`start_date`),
    KEY `key_end_date` (`end_date`),
    KEY `key_status` (`status`),
    CONSTRAINT `fk_price_attribute__price` FOREIGN KEY (`domain_id`) REFERENCES `price` (`id`),
    CONSTRAINT `fk_price_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты базовой цены';

DROP TABLE IF EXISTS `price_value`;
CREATE TABLE `price_value`
(
    `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
    `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
    `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
    `number`       BIGINT(20) COMMENT 'Числовое значение',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
    KEY `key_attribute_id` (`attribute_id`),
    KEY `key_locale` (`locale_id`),
    KEY `key_value` (`text`(128)),
    CONSTRAINT `fk_price_value__price_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `price_attribute` (`id`),
    CONSTRAINT `fk_price_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов базовой цены';

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


-- ---------------------------
-- Sale Decision
-- ---------------------------

CALL createDomainTables('sale_decision', 'Условие продаж');
CALL createDomainTables('rule', 'Правило');
CALL createDomainTables('rule_condition', 'Условие правила');
CALL createDomainTables('rule_action', 'Действие правила');

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
CALL createEntityAttribute(36, 6, 5, 'Тип', 'Тип');

-- ---------------------------
-- Card
-- ---------------------------

CALL createDomainTables('card', 'Карта');

CALL createEntity(37, 'card', 'Карта', 'Карта');
CALL createEntityAttribute(37, 1, 2, 'Номер карты', 'Номер картки');
CALL createEntityAttribute(37, 2, 6, 'Дата создания', 'Дата створення');
CALL createEntityAttributeWithReference(37, 3, 11, 20, 'Сотрудник', 'Сотрудник');
CALL createEntityAttribute(37, 4, 5, 'Индекс', 'Індекс');

-- ---------------------------
-- Rank
-- ---------------------------

CALL createDomainTables('rank', 'Ранг');

CALL createEntity(38, 'rank', 'Ранг', 'Ранг');
CALL createEntityAttribute(38, 1, 0, 'Название', 'Название');

CALL insertDomainText(1, 'rank', 'Рекомендующий гостей', 'Рекомендує гостей');
CALL insertDomainText(2, 'rank', 'Профконсультант', 'Профконсультант');
CALL insertDomainText(3, 'rank', 'Менеджер юниор', 'Менеджер юниор');
CALL insertDomainText(4, 'rank', 'Тим менеджер', 'Тім менеджер');
CALL insertDomainText(5, 'rank', 'Ассистент сеньора', 'Асистент сеньйора');
CALL insertDomainText(6, 'rank', 'Сеньор менеджер', 'Сеньйор менеджер');
CALL insertDomainText(7, 'rank', 'Дивизион менеджер', 'Дивізіон менеджер');
CALL insertDomainText(8, 'rank', 'Ареа менеджер', 'Ареа менеджер');
CALL insertDomainText(9, 'rank', 'Региональный менеджер', 'Регіональний менеджер');
CALL insertDomainText(10, 'rank', 'Серебряный директор', 'Срібний директор');
CALL insertDomainText(11, 'rank', 'Золотой  директор', 'Золотий директор');
CALL insertDomainText(12, 'rank', 'Платиновый  директор', 'Платиновий директор');
CALL insertDomainText(13, 'rank', 'Менеджер ассистент', 'Менеджер ассистент');

-- ---------------------------
-- Reward Type
-- ---------------------------

CALL createDomainTables('reward_type', 'Тип вознаграждения');

CALL createEntity(39, 'reward_type', 'Тип вознаграждения', 'Тип винагорода');
CALL createEntityAttribute(39, 1, 0, 'Название', 'Название');

CALL insertDomainText(1, 'reward_type', 'Покупка Майкук', 'Купівля Майкук');
CALL insertDomainText(2, 'reward_type', 'Покупка БА', 'Купівля БА');
CALL insertDomainText(3, 'reward_type', 'Регистрация ПК', 'Реєстрація ПК');
CALL insertDomainText(4, 'reward_type', 'Базовое вознаграждение МК', 'Базова винагорода МК');
CALL insertDomainText(5, 'reward_type', 'Базовое вознаграждение БА', 'Базова винагорода БА');
CALL insertDomainText(6, 'reward_type', 'Личный финансовый оборот', 'Особистий фінансовий оборот');
CALL insertDomainText(7, 'reward_type', 'Личная продажа', 'Особистий продаж');
CALL insertDomainText(8, 'reward_type', 'Кулинарный практикум', 'Кулінарний практикум');
CALL insertDomainText(9, 'reward_type', 'Менеджерский Майкук бонус', 'Менеджерський Майкук бонус');
CALL insertDomainText(10, 'reward_type', 'Менеджерская надбавка', 'Менеджерська надбавка');
CALL insertDomainText(11, 'reward_type', 'Оборот группы', 'Оборот групи');
CALL insertDomainText(12, 'reward_type', 'Оборот структуры', 'Оборот структури');
CALL insertDomainText(13, 'reward_type', 'Ранг', 'Ранг');


-- ---------------------------
-- Reward
-- ---------------------------

CALL createDomainTables('reward', 'Вознагражднение');

CALL createEntity(40, 'reward', 'Вознагражднение', 'Винагорода');
CALL createEntityAttribute(40, 1, 6, 'Дата', 'Дата');
CALL createEntityAttributeWithReference(40, 2, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(40, 3, 4, 'Сумма (в баллах)', 'Сума (в балах)');
CALL createEntityAttributeWithReference(40, 4, 11, 39, 'Тип', 'Тип');
CALL createEntityAttributeWithReference(40, 5, 11, 38, 'Ранг', 'Ранг');
CALL createEntityAttribute(40, 6, 2, 'Комментарий', 'Коментар');
CALL createEntityAttributeWithReference(40, 7, 11, 28, 'Продажа', 'Продаж');
CALL createEntityAttribute(40, 8, 6, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttribute(40, 9, 4, 'Личный оборот', 'Особистий оборот');
CALL createEntityAttribute(40, 10, 4, 'Групповой оборот', 'Груповий оборот');
CALL createEntityAttribute(40, 11, 4, 'Курс', 'Курс');
CALL createEntityAttribute(40, 12, 4, 'Скидка', 'Знижка');
CALL createEntityAttribute(40, 13, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttribute(40, 14, 4, 'Личный финансовый оборот', 'Особистий фінансовий оборот');
CALL createEntityAttribute(40, 15, 4, 'Финансовый оборот группы', 'Фінансовий оборот групи');
CALL createEntityAttribute(40, 16, 4, 'Оборот структуры', 'Оборот структури');
CALL createEntityAttribute(40, 17, 4, 'Финансовый оборот структуры', 'Фінансовий оборот структури');
CALL createEntityAttributeWithReference(40, 18, 11, 20, 'Менеджер', 'Менеджер');
CALL createEntityAttributeWithReference(40, 19, 11, 38, 'Ранг менеджера', 'Ранг менеджера');
CALL createEntityAttribute(40, 20, 4, 'Всего', 'Всього');
CALL createEntityAttribute(40, 21, 5, 'Статус', 'Статус');
CALL createEntityAttribute(40, 22, 4, 'Базования цена', 'Базованія ціна');
CALL createEntityAttribute(40, 23, 4, 'Цена', 'Ціна');
CALL createEntityAttributeWithReference(40, 24, 11, 42, 'Операционный месяц', 'Операційний місяць');

-- ---------------------------
-- Reward Parameter
-- ---------------------------

CALL createDomainTables('reward_parameter', 'Параметры вознаграждений');

CALL createEntity(43, 'period', 'Параметры вознаграждений', 'Параметри винагород');
CALL createEntityAttribute(43, 1, 6, 'Дата начала', 'Дата початку');
CALL createEntityAttribute(43, 2, 6, 'Дата окончания', 'Дата закінчення');
CALL createEntityAttributeWithReference(43, 3, 11, 39, 'Тип вознаграждения', 'Тип винагороди');
CALL createEntityAttribute(43, 4, 0, 'Название', 'Назва');
CALL createEntityAttribute(43, 5, 4, 'Значение', 'Значення');

DELIMITER //

CREATE PROCEDURE insertRewardParameter(IN objectId BIGINT, IN rewardTypeId BIGINT, IN text VARCHAR(128) CHARSET utf8,
                                       IN value VARCHAR(64) CHARSET utf8)
BEGIN
    SET @insertDomain = CONCAT('INSERT INTO `reward_parameter`(id, object_id) VALUE (', objectId, ', ', objectId, ');');
    PREPARE QUERY FROM @insertDomain; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `reward_parameter_attribute`(domain_id, entity_attribute_id, text) VALUE (',
                                  objectId, ', 3,', rewardTypeId, ');');
    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `reward_parameter_attribute`(domain_id, entity_attribute_id) VALUE (',
                                  objectId, ', 4);');
    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @attributeId = LAST_INSERT_ID();
    SET @insertAttributeValue = CONCAT('INSERT INTO `reward_parameter_value`(attribute_id, locale_id, text) VALUES (',
                                       @attributeId , ', 1, ''', text, '''), (',
                                       @attributeId , ', 2, ''', text, ''');');
    PREPARE QUERY FROM @insertAttributeValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `reward_parameter_attribute`(domain_id, entity_attribute_id, text) VALUE (',
                                  objectId, ', 5,', value, ');');
    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

CALL insertRewardParameter(1, 4, 'Базовое вознаграждение при продаже МК по заявке из САП', '80');
CALL insertRewardParameter(2, 4, 'Базовое вознаграждение для ПК со статусом "Promo" (МК премиум)', '80');
CALL insertRewardParameter(3, 4, 'Базовое вознаграждение для ПК со статусом "Promo" (МК тач)', '90');
CALL insertRewardParameter(4, 4, 'Базовое вознаграждение для ПК со статусом "Just" (МК премиум)', '120');
CALL insertRewardParameter(5, 4, 'Базовое вознаграждение для ПК со статусом "Just" (МК тач)', '130');
CALL insertRewardParameter(6, 4, 'Базовое вознаграждение для ПК со статусом "VIP" (МК премиум)', '170');
CALL insertRewardParameter(7, 4, 'Базовое вознаграждение для ПК со статусом "VIP" (МК тач)', '195');
CALL insertRewardParameter(8, 4, 'Базовое вознаграждение при продаже БА (% от суммы ДКП)', '0.15');

CALL insertRewardParameter(9, 9, 'Менеджерский Майкук бонус (МК премиум)', '50');
CALL insertRewardParameter(10, 9, 'Менеджерский Майкук бонус (МК тач)', '65');

CALL insertRewardParameter(47, 10, 'Групповой оборот для получения ранга Менеджер ассистент', '1000');
CALL insertRewardParameter(11, 10, 'Групповой оборот для получения ранга Менеджер юниор', '3000');
CALL insertRewardParameter(12, 10, 'Групповой оборот для получения ранга Тим менеджер', '6000');
CALL insertRewardParameter(13, 10, 'Групповой оборот для получения ранга Ассистент сеньора', '8000');
CALL insertRewardParameter(14, 10, 'Групповой оборот для получения ранга Сеньор менеджер', '12000');
CALL insertRewardParameter(15, 10, 'Групповой оборот для получения ранга Дивизион менеджер', '16000');
CALL insertRewardParameter(16, 10, 'Групповой оборот для получения ранга Ареа менеджер', '22000');
CALL insertRewardParameter(17, 10, 'Групповой оборот для получения ранга Региональный менеджер', '35000');
CALL insertRewardParameter(18, 10, 'Групповой оборот для получения ранга Серебрянный директор', '60000');
CALL insertRewardParameter(19, 10, 'Групповой оборот для получения ранга Золотой директор', '75000');
CALL insertRewardParameter(20, 10, 'Групповой оборот для получения ранга Платиновый директор', '100000');

CALL insertRewardParameter(48, 10, 'Менеджерские надбавки МК для ранга Менеджер ассистент', '0');
CALL insertRewardParameter(21, 10, 'Менеджерские надбавки МК для ранга Менеджер юниор', '20');
CALL insertRewardParameter(22, 10, 'Менеджерские надбавки МК для ранга Тим менеджер', '20');
CALL insertRewardParameter(23, 10, 'Менеджерские надбавки МК для ранга Ассистент сеньора', '25');
CALL insertRewardParameter(24, 10, 'Менеджерские надбавки МК для ранга Сеньор менеджер', '50');
CALL insertRewardParameter(25, 10, 'Менеджерские надбавки МК для ранга Дивизион менеджер', '50');
CALL insertRewardParameter(26, 10, 'Менеджерские надбавки МК для ранга Ареа менеджер', '70');
CALL insertRewardParameter(27, 10, 'Менеджерские надбавки МК для ранга Региональный менеджер', '70');
CALL insertRewardParameter(28, 10, 'Менеджерские надбавки МК для ранга Серебрянный директор', '90');
CALL insertRewardParameter(29, 10, 'Менеджерские надбавки МК для ранга Золотой директор', '90');
CALL insertRewardParameter(30, 10, 'Менеджерские надбавки МК для ранга Платиновый директор', '90');

CALL insertRewardParameter(49, 10, 'Менеджерские надбавки БА (%) для ранга Менеджер ассистент', '0');
CALL insertRewardParameter(31, 10, 'Менеджерские надбавки БА (%) для ранга Менеджер юниор', '0.02');
CALL insertRewardParameter(32, 10, 'Менеджерские надбавки БА (%) для ранга Тим менеджер', '0.02');
CALL insertRewardParameter(33, 10, 'Менеджерские надбавки БА (%) для ранга Ассистент сеньора', '0.03');
CALL insertRewardParameter(34, 10, 'Менеджерские надбавки БА (%) для ранга Сеньор менеджер', '0.05');
CALL insertRewardParameter(35, 10, 'Менеджерские надбавки БА (%) для ранга Дивизион менеджер', '0.05');
CALL insertRewardParameter(36, 10, 'Менеджерские надбавки БА (%) для ранга Ареа менеджер', '0.07');
CALL insertRewardParameter(37, 10, 'Менеджерские надбавки БА (%) для ранга Региональный менеджер', '0.07');
CALL insertRewardParameter(38, 10, 'Менеджерские надбавки БА (%) для ранга Серебрянный директор', '0.09');
CALL insertRewardParameter(39, 10, 'Менеджерские надбавки БА (%) для ранга Золотой директор', '0.09');
CALL insertRewardParameter(40, 10, 'Менеджерские надбавки БА (%) для ранга Платиновый директор', '0.09');

CALL insertRewardParameter(41, 8, 'Вознаграждение за КП', '25');
CALL insertRewardParameter(42, 8, 'Вознаграждение за КП по заявке из САП', '15');

CALL insertRewardParameter(43, 6, 'Минимальное количество баллов для получения бонуса за личный финансовый оборот', '2000');
CALL insertRewardParameter(44, 6, 'Среднее значение количества баллов для получения бонуса за личный финансовый оборот', '3000');
CALL insertRewardParameter(45, 6, 'Бонус за личный финансовый оборот меньше среднего значения', '50');
CALL insertRewardParameter(46, 6, 'Бонус за личный финансовый оборот больше средного значения', '100');

CALL insertRewardParameter(50, 11, 'Выплаты (%) за групповой оборот для ранга Менеджер ассистент', '1.5');
CALL insertRewardParameter(51, 11, 'Выплаты (%) за групповой оборот для ранга Менеджер юниор', '3');
CALL insertRewardParameter(52, 11, 'Выплаты (%) за групповой оборот для ранга Тим менеджер', '5');
CALL insertRewardParameter(53, 11, 'Выплаты (%) за групповой оборот для ранга Ассистент сеньора', '5.5');
CALL insertRewardParameter(54, 11, 'Выплаты (%) за групповой оборот для ранга Сеньор менеджер', '6.5');
CALL insertRewardParameter(55, 11, 'Выплаты (%) за групповой оборот для ранга Дивизион менеджер', '7.5');
CALL insertRewardParameter(56, 11, 'Выплаты (%) за групповой оборот для ранга Ареа менеджер', '8');
CALL insertRewardParameter(57, 11, 'Выплаты (%) за групповой оборот для ранга Региональный менеджер', '9');
CALL insertRewardParameter(58, 11, 'Выплаты (%) за групповой оборот для ранга Серебрянный директор', '10');
CALL insertRewardParameter(59, 11, 'Выплаты (%) за групповой оборот для ранга Золотой директор', '11');
CALL insertRewardParameter(60, 11, 'Выплаты (%) за групповой оборот для ранга Платиновый директор', '12');

-- ---------------------------
-- Payment
-- ---------------------------

CALL createDomainTables('payment', 'Оплата');

CALL createEntity(41, 'payment', 'Оплата', 'Оплата');
CALL createEntityAttributeWithReference(41, 1, 11, 20, 'Профконсультант', 'Профконсультант');
CALL createEntityAttribute(41, 2, 6, 'Дата', 'Дата');
CALL createEntityAttribute(41, 3, 6, 'Начало периода', 'Початок періоду');
CALL createEntityAttribute(41, 4, 6, 'Окончание периода', 'Закінчення періоду');
CALL createEntityAttribute(41, 5, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttribute(41, 6, 4, 'Курс балла', 'Курс балла');
CALL createEntityAttribute(41, 7, 4, 'Сумма (в баллах)', 'Сума (в балах)');
CALL createEntityAttribute(41, 8, 2, 'Номер ДКП', 'Номер ДКП');
CALL createEntityAttributeWithReference(41, 9, 11, 28, 'Продажа', 'Продаж');
CALL createEntityAttribute(41, 10, 5, 'Тип', 'Тип');
CALL createEntityAttributeWithReference(41, 11, 11, 42, 'Операционный месяц', 'Операційний місяць');

-- ---------------------------
-- Period
-- ---------------------------

CALL createDomainTables('period', 'Журнал расчетных периодов');

CALL createEntity(42, 'period', 'Журнал расчетных периодов', 'Журнал розрахункових періодів');
CALL createEntityAttribute(42, 1, 6, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttribute(42, 2, 6, 'Отметка времени закрытия', 'Відмітка часу закриття');
CALL createEntityAttributeWithReference(42, 3, 11, 20, 'Пользователь', 'Користувач');

-- ---------------------------
-- Rate
-- ---------------------------

CALL createDomainTables('rate', 'Курс');

CALL createEntity(44, 'rate', 'Курс', 'Курс');
CALL createEntityAttribute(44, 1, 6, 'Дата', 'Дата');
CALL createEntityAttribute(44, 2, 4, 'Курс', 'Курс');

-- ---------------------------
-- Account
-- ---------------------------

CALL createDomainTables('account', 'Счёт');

CALL createEntity(45, 'account', 'Счёт', 'Рахунок');
CALL createEntityAttributeWithReference(45, 1, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(45, 2, 6, 'Дата', 'Дата');
CALL createEntityAttributeWithReference(45, 3, 11, 42, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttributeWithReference(45, 4, 11, 30, 'Локальная валюта', 'Локальна валюта');
CALL createEntityAttribute(45, 5, 4, 'Баланс', 'Баланс');
CALL createEntityAttribute(45, 6, 4, 'Начислено', 'Нараховано');
CALL createEntityAttribute(45, 7, 4, 'Выплачено', 'Виплачено');
CALL createEntityAttribute(45, 8, 4, 'Изъято', 'Вилучено');
CALL createEntityAttribute(45, 9, 4, 'Потрачено', 'Витрачений');


-- Version

INSERT INTO `update` (`version`) VALUE ('2.0.0');
