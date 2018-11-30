-- Fix constraints

ALTER TABLE country_attribute DROP FOREIGN KEY fk_country_attribute__entity_attribute;
ALTER TABLE region_attribute DROP FOREIGN KEY fk_region_attribute__entity_attribute;
ALTER TABLE city_type_attribute DROP FOREIGN KEY fk_city_type_attribute__entity_attribute;
ALTER TABLE city_attribute DROP FOREIGN KEY fk_city_attribute__entity_attribute;
ALTER TABLE last_name_attribute DROP FOREIGN KEY fk_last_name_attribute__entity_attribute;
ALTER TABLE first_name_attribute DROP FOREIGN KEY fk_first_name_attribute__entity_attribute;
ALTER TABLE middle_name_attribute DROP FOREIGN KEY fk_middle_name_attribute__entity_attribute;
ALTER TABLE worker_attribute DROP FOREIGN KEY fk_worker_attribute__entity_attribute;
ALTER TABLE mk_status_attribute DROP FOREIGN KEY fk_mk_status_attribute__entity_attribute;
ALTER TABLE position_attribute DROP FOREIGN KEY fk_position_attribute__entity_attribute;
ALTER TABLE nomenclature_attribute DROP FOREIGN KEY fk_nomenclature_attribute__entity_attribute;
ALTER TABLE storage_attribute DROP FOREIGN KEY fk_storage_attribute__entity_attribute;
ALTER TABLE product_attribute DROP FOREIGN KEY fk_product_attribute__entity_attribute;


/* Product */

DELETE FROM product_value where id > 0;
DELETE FROM product_attribute where id > 0;
DELETE FROM product where id > 0;

DELETE FROM entity_value WHERE entity_id = 25;
DELETE FROM entity_attribute WHERE entity_id = 25;

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


/* Transaction */

INSERT INTO `sequence` (`name`) VALUE ('transaction');

INSERT INTO `entity` (`id`, `name`) VALUE (26, 'transaction');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (26, 1, 'Транзакция'), (26, 2, 'Транзакція');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 1, 11, 23);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 1, 1, 'Номенклатура'), (26, 1, 2, 'Номенклатура');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 2, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 2, 1, 'Количество'), (26, 2, 2, 'Кількість');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 3, 11, 24);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 3, 1, 'Из склада'), (26, 3, 2, 'Зі складу');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 4, 11, 24);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 4, 1, 'В склад'), (26, 4, 2, 'В склад');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 5, 11, 20);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 5, 1, 'Сотрудник'), (26, 5, 2, 'Співробітник');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, reference_id) VALUES (26, 6, 11, 11);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 6, 1, 'Имя'), (26, 6, 2, 'Ім''я');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 7, 11, 12);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 7, 1, 'Отчество'), (26, 7, 2, 'По батькові');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (26, 8, 11, 13);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 8, 1, 'Фамилия'), (26, 8, 2, 'Прізвище');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 9, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 9, 1, 'Тип транзакции'), (26, 9, 2, 'Тип транзакції');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 10, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 10, 1, 'Тип перемещения'), (26, 10, 2, 'Тип переміщення');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 11, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 11, 1, 'Серийный номер'), (26, 11, 2, 'Серійний номер');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (26, 12, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (26, 12, 1, 'Комментарии'), (26, 12, 2, 'Комментарии');


-- ---------------------------
-- Transaction
-- ---------------------------

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
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
  CONSTRAINT `ft_transaction__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_transaction__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_transaction__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Транзакция';

DROP TABLE IF EXISTS `transaction_attribute`;
CREATE TABLE `transaction_attribute` (
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
  CONSTRAINT `fk_transaction_attribute__transaction` FOREIGN KEY (`object_id`) REFERENCES `transaction`(`object_id`),
  CONSTRAINT `fk_transaction_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
  CONSTRAINT `fk_transaction_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты транзакции';

DROP TABLE IF EXISTS `transaction_value`;
CREATE TABLE `transaction_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `text` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`,`locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_transaction_value__transaction_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `transaction_attribute` (`id`),
  CONSTRAINT `fk_transaction_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Значения атрибутов транзакции';


-- Update

INSERT INTO `update` (`version`) VALUE ('20181105_1.0.3');