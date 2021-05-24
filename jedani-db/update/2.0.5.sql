CALL createDomainTables('ratio', 'Коэффициенты');

CALL createEntity(47, 'ratio', 'Коэффициент', 'Коефіцієнт');
CALL createEntityAttribute(47, 1, 6, 'Дата начала', 'Дата початку');
CALL createEntityAttribute(47, 2, 6, 'Дата окончания', 'Дата закінчення');
CALL createEntityAttributeWithReference(47, 3, 11, 1, 'Страна', 'Країна');
CALL createEntityAttribute(47, 4, 4, 'Значение', 'Значення');


INSERT INTO `update` (`version`) VALUE ('2.0.5');
