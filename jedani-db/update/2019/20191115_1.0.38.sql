-- Payment type

CALL createEntityAttribute(41, 10, 5, 'Тип', 'Тип');

-- Sale reward point

CALL createEntityAttribute(28, 19, 4, 'Сумма вознаграждения', 'Сума винагороди');
CALL createEntityAttribute(28, 20, 4, 'Сумма вознаграждения ММБ', 'Сума винагороди ММБ');
CALL createEntityAttribute(28, 21, 4, 'Сумма вознаграждения КП', 'Сума ММБ винагороди КП');

-- Period attributes

DELETE FROM period_attribute WHERE id > 0;
DELETE FROM period WHERE id > 0;

DELETE FROM entity_value WHERE entity_id = 42 AND entity_attribute_id IS NOT NULL;
DELETE FROM entity_attribute WHERE entity_id = 42;

UPDATE entity_value SET `text` = 'Журнал расчетных месяцев' WHERE entity_id = 42 AND entity_attribute_id IS NULL AND locale_id = 1;
UPDATE entity_value SET `text` = 'Журнал розрахункових місяців' WHERE entity_id = 42 AND entity_attribute_id IS NULL AND locale_id = 2;

CALL createEntityAttribute(42, 1, 6, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttribute(42, 2, 6, 'Отметка времени закрытия', 'Відмітка часу закриття');
CALL createEntityAttributeWithReference(42, 3, 11, 20, 'Пользователь', 'Користувач');

-- Reward volumes

CALL createEntityAttribute(40, 9, 4, 'Личный оборот', 'Особистий оборот');
CALL createEntityAttribute(40, 10, 4, 'Групповой оборот', 'Груповий оборот');

-- Reward Type

CALL insertDomainText(10, 'reward_type', 'Менеджерская надбавка', 'Менеджерська надбавка');

-- Reward month

DELETE FROM reward_attribute WHERE id > 0;
DELETE FROM reward WHERE id > 0;

DELETE FROM entity_value WHERE entity_id = 40 AND entity_attribute_id = 8;
DELETE FROM entity_attribute WHERE entity_id = 40 and entity_attribute_id = 8;

CALL createEntityAttribute(40, 8, 6, 'Операционный месяц', 'Операційний місяць');

-- Reward type

DELETE rtv FROM reward_type_value rtv
    LEFT JOIN reward_type_attribute rta ON rtv.attribute_id = rta.id
    LEFT JOIN reward_type rt ON rta.domain_id = rt.id
WHERE rt.object_id IN (4, 5);

DELETE rta FROM reward_type_attribute rta
    LEFT JOIN reward_type rt ON rta.domain_id = rt.id
WHERE rt.object_id IN (4, 5);

DELETE FROM reward_type WHERE object_id IN (4, 5);

CALL insertDomainText(4, 'reward_type', 'Базовое вознаграждение МК', 'Базова винагорода МК');
CALL insertDomainText(5, 'reward_type', 'Базовое вознаграждение БА', 'Базова винагорода БА');

-- Update

INSERT INTO `update` (`version`) VALUE ('20191115_1.0.38');