CALL createDomainTables('reward_node', 'Структура вознаграждений');

CALL createEntity(49, 'worker_node', 'Структура вознаграждений', 'Структура винагород');
CALL createEntityAttributeWithReference(49, 1, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(49, 2, 4, 'Оборот', 'Оборот');
CALL createEntityAttribute(49, 3, 4, 'Финансовый оборот', 'Фінансовий оборот');
CALL createEntityAttribute(49, 4, 4, 'Годовой финансовый оборот', 'Річний фінансовий оборот');
CALL createEntityAttribute(49, 5, 4, 'Оборот группы', 'Оборот групи');
CALL createEntityAttribute(49, 6, 4, 'Финансовый оборот группы', 'Фінансовий оборот групи');
CALL createEntityAttribute(49, 7, 4, 'Оборот структуры', 'Оборот структури');
CALL createEntityAttribute(49, 8, 4, 'Финансовый оборот структуры', 'Фінансовий оборот структури');
CALL createEntityAttribute(49, 9, 5, 'Статус сотрудника', 'Статус співробітника');
CALL createEntityAttribute(49, 10, 5, 'Статус профконсультанта', 'Статус профконсультанта');
CALL createEntityAttribute(49, 11, 5, 'Количество приглашенных', 'Кількість запрошених');
CALL createEntityAttribute(49, 12, 5, 'Количество приглашенных лично', 'Кількість запрошених особисто');
CALL createEntityAttribute(49, 13, 5, 'Количество новых сотрудников', 'Кількість нових співробітників');
CALL createEntityAttribute(49, 14, 5, 'Количество новых сотрудников в группе', 'Кількість нових співробітників в групі');
CALL createEntityAttribute(49, 15, 5, 'Количество менеджеров в структуре', 'Кількість менеджерів в структурі');
CALL createEntityAttribute(49, 16, 5, 'Ранг', 'Ранг');
CALL createEntityAttributeWithReference(49, 17, 11, 42, 'Операционный месяц', 'Операційний місяць');

INSERT INTO `update` (`version`) VALUE ('2.0.13');