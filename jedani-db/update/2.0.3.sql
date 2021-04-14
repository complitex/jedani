CALL createDomainTables('payout', 'Выплата');

CALL createEntity(46, 'payout', 'Выплата', 'Виплат');
CALL createEntityAttributeWithReference(46, 1, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(46, 2, 6, 'Дата', 'Дата');
CALL createEntityAttributeWithReference(46, 3, 11, 42, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttributeWithReference(46, 4, 11, 30, 'Локальная валюта', 'Локальна валюта');
CALL createEntityAttribute(46,5, 4, 'Сумма', 'Сумма');

INSERT INTO `update` (`version`) VALUE ('2.0.3');
