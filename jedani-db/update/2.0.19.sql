CALL createDomainTables('reward_rank', 'Ранг вознаграждений');

CALL createEntity(51, 'reward_rank', 'Ранг вознаграждений', 'Ранг винагород');
CALL createEntityAttributeWithReference(51, 1, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(51, 2, 5,  'Ранг', 'Ранг');
CALL createEntityAttributeWithReference(51, 3, 11, 42, 'Операционный месяц', 'Операційний місяць');

INSERT INTO `update` (`version`) VALUE ('2.0.19');
