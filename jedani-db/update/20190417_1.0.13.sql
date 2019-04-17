-- ---------------------------
-- Base price
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
    CONSTRAINT `ft_price__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
    CONSTRAINT `fk_price__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
    CONSTRAINT `fk_price__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Базовая цена';

DROP TABLE IF EXISTS `price_attribute`;
CREATE TABLE `price_attribute`
(
    `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `object_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
    `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
    `text`                VARCHAR(255) COMMENT 'Текст',
    `number`              BIGINT(20) COMMENT 'Число',
    `date`                DATETIME COMMENT 'Дата',
    `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
    `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
    `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
    `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
    PRIMARY KEY (`id`),
    KEY `key_object_id` (`object_id`),
    KEY `key_entity_attribute_id` (`entity_attribute_id`),
    KEY `key_text` (`text`),
    KEY `key_number` (`number`),
    KEY `key_date` (`date`),
    KEY `key_start_date` (`start_date`),
    KEY `key_end_date` (`end_date`),
    KEY `key_status` (`status`),
    CONSTRAINT `fk_price_attribute__price` FOREIGN KEY (`object_id`) REFERENCES `price` (`object_id`),
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

/* Base price */

INSERT INTO `sequence` (`name`) VALUE ('price');

INSERT INTO `entity` (`id`, `name`) VALUE (32, 'price');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (32, 1, 'Базовая цена'), (32, 2, 'Базова ціна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (32, 1, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 1, 1, 'Дата начала'), (32, 1, 2, 'Дата початку');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (32, 2, 6);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 2, 1, 'Дата окончания'), (32, 2, 2, 'Дата закінчення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (32, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (32, 3, 1, 'Цена'), (32, 3, 2, 'Ціна');


-- Update

INSERT INTO `update` (`version`) VALUE ('20190417_1.0.13');