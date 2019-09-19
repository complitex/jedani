update entity_value set `text` = 'Номер договора' where entity_id = 41 and entity_attribute_id = 8 and locale_id = 1;
update entity_value set `text` = 'Номер договору' where entity_id = 41 and entity_attribute_id = 8 and locale_id = 2;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190919_1.0.32');