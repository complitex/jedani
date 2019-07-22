DELIMITER //

CREATE PROCEDURE insertDomainText(IN objectId BIGINT, IN entityName VARCHAR(128) CHARSET utf8,
                                  IN textRU VARCHAR(128) CHARSET utf8, IN textUA VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insertDomain = CONCAT('INSERT INTO `', entityName, '`(id, object_id, status) VALUE (', objectId, ', ', objectId, ', 1);');

    PREPARE QUERY FROM @insertDomain; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `', entityName, '_attribute`(domain_id, entity_attribute_id, status) VALUE (',
                                  objectId, ', 1, 1);');

    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @attributeId = LAST_INSERT_ID();

    SET @insertAttributeValue = CONCAT('INSERT INTO `', entityName, '_value`(attribute_id, locale_id, text) VALUES (',
                                       @attributeId , ', 1, ''', textRU, '''), (',
                                       @attributeId , ', 2, ''', textUA, ''');');

    PREPARE QUERY FROM @insertAttributeValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

-- Rank

CALL createDomainTables('rank', 'Ранг');
CALL createEntity(38, 'rank', 'Ранг', 'Ранг');
CALL createEntityAttribute(38, 1, 0, 'Название', 'Название');

CALL insertDomainText(1, 'rank', 'Рекомендующий гостей', 'Рекомендує гостей');
CALL insertDomainText(2, 'rank', 'Профконсультант', 'Профконсультант');
CALL insertDomainText(3, 'rank', 'Менеджер ассистент', 'Менеджер асистент');
CALL insertDomainText(4, 'rank', 'Тим менеджер', 'Тім менеджер');
CALL insertDomainText(5, 'rank', 'Ассистент сеньора', 'Асистент сеньйора');
CALL insertDomainText(6, 'rank', 'Сеньор менеджер', 'Сеньйор менеджер');
CALL insertDomainText(7, 'rank', 'Дивизион менеджер', 'Дивізіон менеджер');
CALL insertDomainText(8, 'rank', 'Ареа менеджер', 'Ареа менеджер');
CALL insertDomainText(9, 'rank', 'Региональный менеджер', 'Регіональний менеджер');
CALL insertDomainText(10, 'rank', 'Серебряный директор', 'Срібний директор');
CALL insertDomainText(11, 'rank', 'Золотой  директор', 'Золотий директор');
CALL insertDomainText(12, 'rank', 'Платиновый  директор', 'Платиновий директор');

-- RewardType

CALL createDomainTables('reward_type', 'Тип вознаграждения');
CALL createEntity(39, 'reward_type', 'Тип вознаграждения', 'Тип винагорода');
CALL createEntityAttribute(39, 1, 0, 'Название', 'Название');

CALL insertDomainText(1, 'reward_type', 'Покупка Майкук', 'Купівля Майкук');
CALL insertDomainText(2, 'reward_type', 'Покупка БА', 'Купівля БА');
CALL insertDomainText(3, 'reward_type', 'Регистрация ПК', 'Реєстрація ПК');
CALL insertDomainText(4, 'reward_type', 'Личная продажа за месяц Майкук', 'Особистий продаж за місяць Майкук');
CALL insertDomainText(5, 'reward_type', 'Личная продажа за месяц БА', 'Особистий продаж за місяць БА');
CALL insertDomainText(6, 'reward_type', 'Личный финансовый оборот', 'Особистий фінансовий оборот');
CALL insertDomainText(7, 'reward_type', 'Личная продажа', 'Особистий продаж');
CALL insertDomainText(8, 'reward_type', 'Кулинарный практикум', 'Кулінарний практикум');


-- Reward

CALL createDomainTables('reward', 'Вознагражднение');
CALL createEntity(40, 'reward', 'Вознагражднение', 'Винагорода');
CALL createEntityAttribute(40, 1, 6, 'Дата', 'Дата');
CALL createEntityAttributeWithReference(40, 2, 11, 20, 'Сотрудник', 'Співробітник');
CALL createEntityAttribute(40, 3, 4, 'Баллы', 'Бали');
CALL createEntityAttributeWithReference(40, 4, 11, 39, 'Тип', 'Тип');
CALL createEntityAttributeWithReference(40, 5, 11, 38, 'Ранг', 'Ранг');
CALL createEntityAttribute(40, 6, 2, 'Комментарий', 'Коментар');

-- Add worker rank and point entity attributes

CALL createEntityAttributeWithReference(20, 22, 11, 38, 'Ранг', 'Ранг');
CALL createEntityAttribute(20, 23, 4, 'Баллы', 'Бали');

-- Update

INSERT INTO `update` (`version`) VALUE ('20190627_1.0.21');