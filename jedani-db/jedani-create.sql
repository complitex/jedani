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
  `login` VARCHAR(64) NOT NULL COMMENT 'Имя пользователя',
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
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Группа пользователей';

-- ------------------------------
-- Sequence
-- ------------------------------

DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence`(
  `name` VARCHAR(100) NOT NULL COMMENT 'Название таблицы сущности',
  `value` bigint UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Значение идентификатора',
  PRIMARY KEY (`name`)
)ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Генератор идентификаторов объектов';

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

-- ------------------------------
-- Entity
-- ------------------------------

DROP TABLE IF EXISTS `entity`;
CREATE TABLE `entity` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор сущности',
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
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `start_date` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL default NULL COMMENT 'Дата окончания периода действия атрибута',
  `value_type_id` BIGINT(20) COMMENT  'Тип значения атрибута',
  `reference_id` BIGINT(20) COMMENT  'Внешний ключ',
  `system` TINYINT(1) default 1 NOT NULL COMMENT 'Является ли тип атрибута системным',
  `required` TINYINT(1) default 1 NOT NULL COMMENT 'Является ли атрибут обязательным',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`attribute_id`, `entity_id`),
  KEY `key_entity_id` (`entity_id`),
  KEY `key_value_type_id` (`value_type_id`),
  CONSTRAINT `fk_attribute_type__entity` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_entity_attribute__entity_value_type` FOREIGN KEY (`value_type_id`) REFERENCES entity_value_type (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип атрибута сущности';

DROP TABLE IF EXISTS `entity_value`;
CREATE TABLE `entity_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `entity_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа аттрибута',
  `attribute_id` BIGINT(20) NULL COMMENT 'Идентификатор типа аттрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`entity_id`, `attribute_id`, `locale_id`),
  KEY `key_entity_id` (`entity_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_entity_value__entity` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_entity_value__entity_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `entity_attribute` (`id`),
  CONSTRAINT `fk_entity_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация';

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

-- --------------------------------------------------------------------------------------------------------------------
-- Address
-- --------------------------------------------------------------------------------------------------------------------

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
  `permission_id` BIGINT(20) NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` VARCHAR(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_region__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_region__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Регион';

DROP TABLE IF EXISTS `region_attribute`;
CREATE TABLE `region_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_id` (`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_region_attribute__region` FOREIGN KEY (`object_id`) REFERENCES `region`(`object_id`),
  CONSTRAINT `fk_region_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`attribute_id`)
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
  CONSTRAINT `fk_region_attribute_value__region_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `region_attribute` (`id`),
  CONSTRAINT `fk_region_attribute_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов региона';

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
  `permission_id` BIGINT(20) NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` VARCHAR(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`,`start_date`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_city_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип населенного пункта';

DROP TABLE IF EXISTS `city_type_attribute`;
CREATE TABLE `city_type_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id` (`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_type_attribute__city_type` FOREIGN KEY (`object_id`) REFERENCES `city_type`(`object_id`),
  CONSTRAINT `fk_city_type_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`attribute_id`)
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
  CONSTRAINT `fk_city_type_attribute_value__city_type_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `city_type_attribute` (`id`),
  CONSTRAINT `fk_city_type_attribute_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов типа населенного пункта';

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
  `permission_id` BIGINT(20) NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` VARCHAR(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `ft_city__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Населенный пункт';

DROP TABLE IF EXISTS `city_attribute`;
CREATE TABLE `city_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_id` (`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_attribute__city` FOREIGN KEY (`object_id`) REFERENCES `city`(`object_id`),
  CONSTRAINT `fk_city_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`attribute_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты населенного пункта';

DROP TABLE IF EXISTS `city_value`;
CREATE TABLE `city_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_city_attribute_value__city_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `city_attribute` (`id`),
  CONSTRAINT `fk_city_attribute_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов населенного пункта';

-- --------------------------------------------------------------------------------------------------------------------
-- Domain
-- --------------------------------------------------------------------------------------------------------------------

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
  `permission_id` BIGINT(20) NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` VARCHAR(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `ft_worker__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_worker__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Профиль сотрудника';

DROP TABLE IF EXISTS `worker_attribute`;
CREATE TABLE `worker_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_id` (`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_worker_attribute__city` FOREIGN KEY (`object_id`) REFERENCES `worker`(`object_id`),
  CONSTRAINT `fk_worker_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
  REFERENCES entity_attribute (`attribute_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты сотрудника';

DROP TABLE IF EXISTS `worker_value`;
CREATE TABLE `worker_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_worker_attribute_value__worker_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `worker_attribute` (`id`),
  CONSTRAINT `fk_worker_attribute_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализация атрибутов сотрудника';

-- todo dev
DROP TABLE IF EXISTS `profile_value`;
DROP TABLE IF EXISTS `profile_attribute`;
DROP TABLE IF EXISTS `profile`;













