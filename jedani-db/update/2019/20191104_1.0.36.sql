CALL createDomainTables('period', 'Журнал расчетных периодов');

CALL createEntity(42, 'period', 'Журнал расчетных периодов', 'Журнал розрахункових періодів');
CALL createEntityAttribute(42, 1, 6, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttribute(42, 2, 6, 'Дата начала', 'Дата початку');
CALL createEntityAttribute(42, 3, 6, 'Дата окончания', 'Дата закінчення');
CALL createEntityAttribute(42, 4, 6, 'Отметка времени закрытия', 'Відмітка часу закриття');
CALL createEntityAttributeWithReference(42, 5, 11, 20, 'Пользователь', 'Користувач');

-- Update

INSERT INTO `update` (`version`) VALUE ('20191104_1.0.36');