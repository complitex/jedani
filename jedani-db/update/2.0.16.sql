-- ---------------------------
-- Parameter
-- ---------------------------

CALL createDomainTables('parameter', 'Параметр');

CALL createEntity(50, 'parameter', 'Параметр', 'Параметр');
CALL createEntityAttribute(50, 1, 5, 'Номер', 'Номер');
CALL createEntityAttribute(50, 2, 5, 'Тип', 'Тип');
CALL createEntityAttribute(50, 3, 2, 'Название', 'Назва');

INSERT INTO `update` (`version`) VALUE ('2.0.16');