ALTER TABLE nomenclature_value ADD COLUMN `number` BIGINT(20) COMMENT 'Числовое значение';
ALTER TABLE nomenclature_value ADD KEY `key_number` (`number`);
ALTER TABLE nomenclature_value CHANGE locale_id locale_id BIGINT(20) COMMENT 'Идентификатор локали';

ALTER TABLE storage_value ADD COLUMN `number` BIGINT(20) COMMENT 'Числовое значение';
ALTER TABLE storage_value ADD KEY `key_number` (`number`);
ALTER TABLE storage_value CHANGE locale_id locale_id BIGINT(20) COMMENT 'Идентификатор локали';


-- Update

INSERT INTO `update` (`version`) VALUE ('20181018_1.0.2');