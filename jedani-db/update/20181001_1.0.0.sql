CREATE TABLE `update` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `version` VARCHAR(64) NOT NULL COMMENT 'Версия',
  `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Обновление базы данных';

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (20, 21, 5);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (20, 21, 1, 'Тип сотрудника'), (20, 21, 2, 'Тип сотрудника');

INSERT INTO `update` (`version`) VALUE ('20181001_1.0.0');

