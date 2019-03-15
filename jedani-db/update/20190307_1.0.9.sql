-- Add rate value parameter

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 10, 4);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 10, 1, 'Курс'), (31, 10, 2, 'Курс');

-- Add uri date parameter

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 11, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 11, 1, 'uri_date_param');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 12, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 12, 1, 'uri_date_format');

UPDATE exchange_rate_attribute set text = 'https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=EUR' WHERE id = 14;;

INSERT INTO exchange_rate_attribute (object_id, entity_attribute_id, status, text) VALUES (1, 11, 1, 'date_req');
INSERT INTO exchange_rate_attribute (object_id, entity_attribute_id, status, text) VALUES (1, 12, 1, 'dd/MM/yyyy');

INSERT INTO exchange_rate_attribute (object_id, entity_attribute_id, status, text) VALUES (2, 11, 1, 'date');
INSERT INTO exchange_rate_attribute (object_id, entity_attribute_id, status, text) VALUES (2, 12, 1, 'yyyyMMdd');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190307_1.0.9');