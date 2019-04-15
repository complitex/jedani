ALTER TABLE currency COMMENT = 'Валюта';
ALTER TABLE currency_attribute COMMENT = 'Атрибуты валюты';
ALTER TABLE currency_value COMMENT = 'Значения атрибутов валюты';

UPDATE entity_value set text = 'Валюта' where entity_id = 30 and entity_attribute_id is null;


ALTER TABLE exchange_rate COMMENT = 'Курсы валют';
ALTER TABLE exchange_rate_attribute COMMENT = 'Атрибуты курсов валют';
ALTER TABLE exchange_rate_value COMMENT = 'Значения атрибутов курсов валют';

UPDATE entity_value set text = 'Курс валюты' where entity_id = 31 and entity_attribute_id is null and locale_id = 1;
UPDATE entity_value set text = 'Курс валюті' where entity_id = 31 and entity_attribute_id is null and locale_id = 2;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190415_1.0.11');