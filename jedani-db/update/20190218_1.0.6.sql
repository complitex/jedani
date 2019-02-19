-- ---------------------------
-- Storage Type
-- ---------------------------

DROP TABLE IF EXISTS storage_type;
CREATE TABLE storage_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип склада';

-- ---------------------------
-- Transaction Type
-- ---------------------------

DROP TABLE IF EXISTS transaction_type;
CREATE TABLE transaction_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип транзакции';

-- ---------------------------
-- Transfer Type
-- ---------------------------

DROP TABLE IF EXISTS transfer_type;
CREATE TABLE transfer_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип перемещения';

-- ---------------------------
-- Recipient Type
-- ---------------------------

DROP TABLE IF EXISTS recipient_type;
CREATE TABLE recipient_type (
  id BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `type` VARCHAR(100) NOT NULL COMMENT 'Тип',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип получателя';

/* Type */

INSERT INTO storage_type (id, type) VALUES (1, 'real'), (2, 'virtual');
INSERT INTO transaction_type (id, type) VALUES (1, 'accept'), (2, 'sell'), (3, 'transfer'), (4, 'withdraw');
INSERT INTO transfer_type (id, type) VALUES (1, 'transfer'), (2, 'gift');
INSERT INTO recipient_type (id, type) VALUES (1, 'storage'), (2, 'worker'), (3, 'client');

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
  CONSTRAINT `ft_sale__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_sale__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_sale__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Продажа';

DROP TABLE IF EXISTS `sale_attribute`;
CREATE TABLE `sale_attribute`
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
  CONSTRAINT `fk_sale_attribute__sale` FOREIGN KEY (`object_id`) REFERENCES `sale` (`object_id`),
  CONSTRAINT `fk_sale_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
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
  CONSTRAINT `ft_sale_item__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_sale_item__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_sale_item__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Позиция продажи';

DROP TABLE IF EXISTS `sale_item_attribute`;
CREATE TABLE `sale_item_attribute`
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
  CONSTRAINT `fk_sale_item_attribute__sale_item` FOREIGN KEY (`object_id`) REFERENCES `sale_item` (`object_id`),
  CONSTRAINT `fk_sale_item_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
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


/* Sale */

INSERT INTO `sequence` (`name`) VALUE ('sale');

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

/* Sale Item*/

INSERT INTO `sequence` (`name`) VALUE ('sale_item');

INSERT INTO `entity` (`id`, `name`) VALUE (29, 'sale_item');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (29, 1, 'Позиция продажи'), (29, 2, 'Позиція продажу');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (29, 1, 11, 23);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (29, 1, 1, 'Номенклатура'), (29, 1, 2, 'Номенклатура');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (29, 2, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (29, 2, 1, 'Количество'), (29, 2, 2, 'Кількість');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (29, 3, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (29, 3, 1, 'Цена'), (29, 3, 2, 'Ціна');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (29, 4, 11, 24);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (29, 4, 1, 'Склад'), (29, 4, 2, 'Склад');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (29, 5, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (29, 5, 1, 'Процент оплаты'), (29, 5, 2, 'Відсоток оплати');


INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (29, 6, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (29, 6, 1, 'Рассрочка'), (29, 6, 2, 'Розстрочка');


-- Add nomenclature type

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (23, 4, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (23, 4, 1, 'Тип номенклатуры'), (23, 4, 2, 'Тип номенклатури');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190218_1.0.6');