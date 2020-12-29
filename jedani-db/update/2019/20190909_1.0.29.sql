-- Payment

CALL createDomainTables('payment', 'Оплата');

CALL createEntity(41, 'payment', 'Оплата', 'Оплата');
CALL createEntityAttributeWithReference(41, 1, 11, 20, 'Профконсультант', 'Профконсультант');
CALL createEntityAttribute(41, 2, 6, 'Дата', 'Дата');
CALL createEntityAttribute(41, 3, 6, 'Начало периода', 'Початок періоду');
CALL createEntityAttribute(41, 4, 6, 'Окончание периода', 'Закінчення періоду');
CALL createEntityAttribute(41, 5, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');
CALL createEntityAttribute(41, 6, 4, 'Курс балла', 'Курс балла');
CALL createEntityAttribute(41, 7, 4, 'Сумма (в баллах)', 'Сума (в балах)');
CALL createEntityAttribute(41, 8, 2, 'Номер ДКП', 'Номер ДКП');
CALL createEntityAttributeWithReference(41, 9, 11, 28, 'Продажа', 'Продаж');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190909_1.0.29');