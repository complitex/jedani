-- ---------------------------
-- Account
-- ---------------------------

CALL createDomainTables('account', 'Счёт');

CALL createEntity(45, 'account', 'Счёт', 'Рахунок');
CALL createEntityAttributeWithReference(45, 1, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(45, 2, 6, 'Дата', 'Дата');
CALL createEntityAttributeWithReference(45, 3, 11, 42, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttribute(45, 4, 4, 'Баланс', 'Баланс');
CALL createEntityAttribute(45, 5, 4, 'Баланс (в локальной валюте)', 'Баланс (в локальній валюті)');
CALL createEntityAttribute(45, 6, 4, 'Рассчитано', 'Розраховане');
CALL createEntityAttribute(45, 7, 4, 'Рассчитано (в локальной валюте)', 'Розраховано (в локальній валюті)');
CALL createEntityAttribute(45, 8, 4, 'Начислено', 'Нараховано');
CALL createEntityAttribute(45, 9, 4, 'Начислено (в локальной валюте)', 'Нараховано (в локальній валюті)');
CALL createEntityAttribute(45, 10, 4, 'Выплачено', 'Виплачено');
CALL createEntityAttribute(45, 11, 4, 'Выплачено (в локальной валюте)', 'Виплачено (в локальній валюті)');
CALL createEntityAttribute(45, 12, 4, 'Изъято', 'Вилучено');
CALL createEntityAttribute(45, 13, 4, 'Изъято (в локальной валюте)', 'Вилучено (в локальній валюті)');

DROP TABLE recipient_type;
DROP TABLE transfer_type;
RENAME TABLE transaction_transfer_type TO transaction_relocation_type;



INSERT INTO `update` (`version`) VALUE ('1.0.50');