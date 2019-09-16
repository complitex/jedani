update entity_value_type set value_type = 'text_list' where id = 0;
update entity_value_type set value_type = 'number_list' where id = 1;
update entity_value_type set value_type = 'entity_list' where id = 10;

-- Add sale status

CALL createEntityAttribute(28, 15, 5, 'Статус', 'Статус');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190916_1.0.31');