DELETE FROM exchange_rate_attribute WHERE status = 3;

-- Rate

CALL createDomainTables('rate', 'Курс');

CALL createEntity(44, 'rate', 'Курс', 'Курс');
CALL createEntityAttribute(44, 1, 6, 'Дата', 'Дата');
CALL createEntityAttribute(44, 2, 4, 'Курс', 'Курс');

-- Update

INSERT INTO `update` (`version`) VALUE ('20191210_1.0.40');