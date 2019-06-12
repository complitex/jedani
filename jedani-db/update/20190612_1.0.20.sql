-- Update Sale

DELETE FROM `entity_value` WHERE `entity_id` = 28 AND `entity_attribute_id` = 12;
DELETE FROM `entity_attribute` WHERE `entity_id` = 28 AND `entity_attribute_id` = 12;
DELETE FROM `sale_attribute` WHERE `entity_attribute_id` = 12;

CALL createEntityAttribute(28, 12, 4, 'Сумма договора (в локальной валюте)', 'Сума договору (в локальній валюті)');

UPDATE `sale_attribute` SET `entity_attribute_id` = 12 WHERE `entity_attribute_id` = 11;

-- Update Sale Item

DELETE FROM `entity_value` WHERE `entity_id` = 29 AND `entity_attribute_id` IN (3, 4);
DELETE FROM `entity_attribute` WHERE `entity_id` = 29 AND `entity_attribute_id` IN (3, 4);

CALL createEntityAttribute(29, 3, 4, 'Цена', 'Ціна');
CALL createEntityAttribute(29, 4, 4, 'Сумма', 'Сума');
CALL createEntityAttribute(29, 5, 4, 'Стоимость балла', 'Вартість бала');
CALL createEntityAttribute(29, 6, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttribute(29, 7, 4, 'Базовая цена', 'Базова ціна');
CALL createEntityAttributeWithReference(29, 8, 11, 36, 'Условия продаж', 'Умови продажу');

DELETE FROM sale_item_attribute WHERE `entity_attribute_id` = 4;
UPDATE `sale_item_attribute` SET `entity_attribute_id` = 6 WHERE entity_attribute_id = 3;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190612_1.0.20');