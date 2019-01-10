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
  CONSTRAINT `ft_setting__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_setting__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_setting__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Настройки';

DROP TABLE IF EXISTS `setting_attribute`;
CREATE TABLE `setting_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `date` BIGINT(20) COMMENT 'Дата',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id` BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY  (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_setting_attribute__setting` FOREIGN KEY (`object_id`) REFERENCES `setting`(`object_id`),
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

-- Entity

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
ALTER TABLE `entity` MODIFY COLUMN `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор сущности';

/* Setting */

INSERT INTO `sequence` (`name`) VALUE ('setting');

INSERT INTO `entity` (`id`, `name`) VALUE (0, 'setting');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (0, 1, 'Настройки'), (0, 2, 'Налаштування');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (0, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (0, 1, 1, 'Значение'), (0, 1, 2, 'Значення');

-- Setting

INSERT INTO `setting` (`object_id`) VALUE (1);
INSERT INTO `setting_attribute` (`object_id`, `entity_attribute_id`, `text`) VALUES (1, 1, '/jedani/data/promotion/');
UPDATE `sequence` SET `value` = 2 WHERE `name` = 'setting';

-- Update

INSERT INTO `update` (`version`) VALUE ('20190110_1.0.5');
