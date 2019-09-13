-- Payment

CALL createDomainTables('payment', 'Оплата');

CALL createEntity(41, 'payment', 'Оплата', 'Оплата');
CALL createEntityAttributeWithReference(41, 1, 11, 20, 'Профконсультант', 'Профконсультант');
CALL createEntityAttribute(41, 2, 6, 'Дата', 'Дата');
CALL createEntityAttribute(41, 3, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttribute(41, 4, 4, 'Сумма (в баллах)', 'Сума (в балах)');
CALL createEntityAttributeWithReference(41, 5, 11, 28, 'Продажа', 'Продаж');
CALL createEntityAttribute(41, 6, 4, 'Курс балла', 'Курс балла');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190909_1.0.29');