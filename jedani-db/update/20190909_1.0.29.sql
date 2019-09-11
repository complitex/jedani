CALL createDomainTables('payment', 'Учет оплат');

CALL createEntity(41, 'payment', 'Учет оплат', 'Облік оплат');


-- Update

INSERT INTO `update` (`version`) VALUE ('20190929_1.0.29');