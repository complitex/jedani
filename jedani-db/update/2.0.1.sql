UPDATE `update` SET `version` = '2.0.0' WHERE `version` = '1.0.50';

DELETE FROM entity_value WHERE entity_id = 20 AND entity_attribute_id = 6;
DELETE FROM entity_attribute WHERE entity_id = 20 AND entity_attribute_id = 6;

UPDATE entity_attribute SET value_type_id = 11, reference_id = 4 WHERE entity_id = 20 AND entity_attribute_id = 7;

UPDATE worker_attribute wa
    LEFT JOIN worker_value wv on wa.id = wv.attribute_id
SET wa.number = wv.number
    WHERE wa.entity_attribute_id = 7;

DELETE wv FROM worker_value wv
    LEFT JOIN worker_attribute wa on wa.id = wv.attribute_id
WHERE entity_attribute_id in (6, 7);

DELETE FROM worker_attribute WHERE entity_attribute_id = 6;


INSERT INTO `update` (`version`) VALUE ('2.0.1');
