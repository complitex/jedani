DROP TABLE recipient_type;
DROP TABLE transfer_type;

UPDATE entity SET name = 'transfer' WHERE id = 26;

RENAME TABLE transaction TO transfer;
ALTER TABLE transfer DROP FOREIGN KEY fk_transaction__entity;
ALTER TABLE transfer DROP FOREIGN KEY fk_transaction__permission;
ALTER TABLE transfer DROP FOREIGN KEY fk_transaction__user;
ALTER TABLE transfer ADD CONSTRAINT fk_transfer__entity FOREIGN KEY (parent_entity_id) REFERENCES entity (id);
ALTER TABLE transfer ADD CONSTRAINT fk_transfer__permission FOREIGN KEY (permission_id) REFERENCES permission (permission_id);
ALTER TABLE transfer ADD CONSTRAINT fk_transfer__user FOREIGN KEY (user_id) REFERENCES user (id);

RENAME TABLE transaction_attribute TO transfer_attribute;
ALTER TABLE transfer_attribute DROP FOREIGN KEY fk_transaction_attribute__transaction;
ALTER TABLE transfer_attribute DROP FOREIGN KEY fk_transaction_attribute__user;
ALTER TABLE transfer_attribute ADD CONSTRAINT fk_transfer_attribute__transfer FOREIGN KEY (domain_id) REFERENCES transfer (id);
ALTER TABLE transfer_attribute ADD CONSTRAINT fk_transfer_attribute__user FOREIGN KEY (user_id) REFERENCES user (id);

RENAME TABLE transaction_value TO transfer_value;
ALTER TABLE transfer_value DROP FOREIGN KEY fk_transaction_value__locale;
ALTER TABLE transfer_value DROP FOREIGN KEY fk_transaction_value__transaction_attribute;
ALTER TABLE transfer_value ADD CONSTRAINT fk_transfer_value__locale FOREIGN KEY (locale_id) REFERENCES locale (id);
ALTER TABLE transfer_value ADD CONSTRAINT fk_transfer_value__transfer_attribute FOREIGN KEY (attribute_id) REFERENCES transfer_attribute (id);

RENAME TABLE transaction_type TO transfer_type;
RENAME TABLE transaction_recipient_type TO transfer_recipient_type;
RENAME TABLE transaction_transfer_type TO transfer_relocation_type;

CALL createEntityAttribute(20, 25, 5, 'Счёт', 'Рахунок');

-- ---------------------------
-- Account
-- ---------------------------

CALL createDomainTables('account', 'Счёт');

CALL createEntity(45, 'account', 'Счёт', 'Рахунок');
CALL createEntityAttribute(45, 1, 5, 'Номер', 'Номер');
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

-- ---------------------------
-- Transaction
-- ---------------------------

CALL createDomainTables('transaction', 'Транзакция');

CALL createEntity(46, 'transaction', 'Транзакция', 'Транзакція ');
CALL createEntityAttribute(46, 1, 6, 'Дата', 'Дата');
CALL createEntityAttributeWithReference(46, 2, 11, 42, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttribute(46, 3, 5, 'Со счёта', 'З рахунку');
CALL createEntityAttribute(46, 4, 5, 'На счёт', 'На рахунок');
CALL createEntityAttribute(46, 5, 5, 'Тип', 'Тип');
CALL createEntityAttribute(46, 6, 4, 'Сумма', 'Сума');
CALL createEntityAttribute(46, 7, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');


INSERT INTO `update` (`version`) VALUE ('1.0.50');