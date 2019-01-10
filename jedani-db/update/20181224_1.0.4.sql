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
  CONSTRAINT `ft_promotion__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_promotion__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_promotion__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Акция';

DROP TABLE IF EXISTS `promotion_attribute`;
CREATE TABLE `promotion_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text` VARCHAR(255) COMMENT 'Текст',
  `number` BIGINT(20) COMMENT 'Число',
  `date` DATETIME COMMENT 'Дата',
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
  CONSTRAINT `fk_promotion_attribute__promotion` FOREIGN KEY (`object_id`) REFERENCES `promotion`(`object_id`),
  CONSTRAINT `fk_promotion_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
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

/* Promotion */

INSERT INTO `sequence` (`name`) VALUE ('promotion');

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


-- Update

INSERT INTO `update` (`version`) VALUE ('20181224_1.0.4');