-- Reward Parameter

CALL createDomainTables('reward_parameter', 'Параметры вознаграждений');

CALL createEntity(43, 'reward_parameter', 'Параметры вознаграждений', 'Параметри винагород');
CALL createEntityAttribute(43, 1, 6, 'Дата начала', 'Дата початку');
CALL createEntityAttribute(43, 2, 6, 'Дата окончания', 'Дата закінчення');
CALL createEntityAttributeWithReference(43, 3, 11, 39, 'Тип вознаграждения', 'Тип винагороди');
CALL createEntityAttribute(43, 4, 0, 'Название', 'Назва');
CALL createEntityAttribute(43, 5, 4, 'Значение', 'Значення');

DELIMITER //

CREATE PROCEDURE insertRewardParameter(IN objectId BIGINT, IN rewardTypeId BIGINT, IN text VARCHAR(128) CHARSET utf8,
    IN value VARCHAR(64) CHARSET utf8)
BEGIN
    SET @insertDomain = CONCAT('INSERT INTO `reward_parameter`(id, object_id) VALUE (', objectId, ', ', objectId, ');');
    PREPARE QUERY FROM @insertDomain; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `reward_parameter_attribute`(domain_id, entity_attribute_id, number) VALUE (',
                                  objectId, ', 3,', rewardTypeId, ');');
    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `reward_parameter_attribute`(domain_id, entity_attribute_id) VALUE (',
                                  objectId, ', 4);');
    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @attributeId = LAST_INSERT_ID();
    SET @insertAttributeValue = CONCAT('INSERT INTO `reward_parameter_value`(attribute_id, locale_id, text) VALUES (',
                                       @attributeId , ', 1, ''', text, '''), (',
                                       @attributeId , ', 2, ''', text, ''');');
    PREPARE QUERY FROM @insertAttributeValue; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insertAttribute = CONCAT('INSERT INTO `reward_parameter_attribute`(domain_id, entity_attribute_id, text) VALUE (',
                                  objectId, ', 5,', value, ');');
    PREPARE QUERY FROM @insertAttribute; EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;


CALL insertRewardParameter(1, 4, 'Базовое вознаграждение при продаже МК по заявке из САП', '80');
CALL insertRewardParameter(2, 4, 'Базовое вознаграждение для ПК со статусом "Promo" (МК премиум)', '80');
CALL insertRewardParameter(3, 4, 'Базовое вознаграждение для ПК со статусом "Promo" (МК тач)', '90');
CALL insertRewardParameter(4, 4, 'Базовое вознаграждение для ПК со статусом "Just" (МК премиум)', '120');
CALL insertRewardParameter(5, 4, 'Базовое вознаграждение для ПК со статусом "Just" (МК тач)', '130');
CALL insertRewardParameter(6, 4, 'Базовое вознаграждение для ПК со статусом "VIP" (МК премиум)', '170');
CALL insertRewardParameter(7, 4, 'Базовое вознаграждение для ПК со статусом "VIP" (МК тач)', '195');
CALL insertRewardParameter(8, 4, 'Базовое вознаграждение при продаже БА (% от суммы ДКП)', '0.15');

CALL insertRewardParameter(9, 9, 'Менеджерский Майкук бонус (МК премиум)', '50');
CALL insertRewardParameter(10, 9, 'Менеджерский Майкук бонус (МК тач)', '65');

CALL insertRewardParameter(11, 10, 'Групповой оборот для получения ранга Менеджер ассистент', '3000');
CALL insertRewardParameter(12, 10, 'Групповой оборот для получения ранга Тим менеджер', '6000');
CALL insertRewardParameter(13, 10, 'Групповой оборот для получения ранга Ассистент сеньора', '8000');
CALL insertRewardParameter(14, 10, 'Групповой оборот для получения ранга Сеньор менеджер', '12000');
CALL insertRewardParameter(15, 10, 'Групповой оборот для получения ранга Дивизион менеджер', '16000');
CALL insertRewardParameter(16, 10, 'Групповой оборот для получения ранга Ареа менеджер', '22000');
CALL insertRewardParameter(17, 10, 'Групповой оборот для получения ранга Региональный менеджер', '35000');
CALL insertRewardParameter(18, 10, 'Групповой оборот для получения ранга Серебрянный директор', '60000');
CALL insertRewardParameter(19, 10, 'Групповой оборот для получения ранга Золотой директор', '75000');
CALL insertRewardParameter(20, 10, 'Групповой оборот для получения ранга Платиновый директор', '100000');

CALL insertRewardParameter(21, 10, 'Менеджерские надбавки МК для ранга Менеджер ассистент', '20');
CALL insertRewardParameter(22, 10, 'Менеджерские надбавки МК для ранга Тим менеджер', '20');
CALL insertRewardParameter(23, 10, 'Менеджерские надбавки МК для ранга Ассистент сеньора', '25');
CALL insertRewardParameter(24, 10, 'Менеджерские надбавки МК для ранга Сеньор менеджер', '50');
CALL insertRewardParameter(25, 10, 'Менеджерские надбавки МК для ранга Дивизион менеджер', '50');
CALL insertRewardParameter(26, 10, 'Менеджерские надбавки МК для ранга Ареа менеджер', '70');
CALL insertRewardParameter(27, 10, 'Менеджерские надбавки МК для ранга Региональный менеджер', '70');
CALL insertRewardParameter(28, 10, 'Менеджерские надбавки МК для ранга Серебрянный директор', '90');
CALL insertRewardParameter(29, 10, 'Менеджерские надбавки МК для ранга Золотой директор', '90');
CALL insertRewardParameter(30, 10, 'Менеджерские надбавки МК для ранга Платиновый директор', '90');

CALL insertRewardParameter(31, 10, 'Менеджерские надбавки БА (%) для ранга Менеджер ассистент', '0.02');
CALL insertRewardParameter(32, 10, 'Менеджерские надбавки БА (%) для ранга Тим менеджер', '0.02');
CALL insertRewardParameter(33, 10, 'Менеджерские надбавки БА (%) для ранга Ассистент сеньора', '0.03');
CALL insertRewardParameter(34, 10, 'Менеджерские надбавки БА (%) для ранга Сеньор менеджер', '0.05');
CALL insertRewardParameter(35, 10, 'Менеджерские надбавки БА (%) для ранга Дивизион менеджер', '0.05');
CALL insertRewardParameter(36, 10, 'Менеджерские надбавки БА (%) для ранга Ареа менеджер', '0.07');
CALL insertRewardParameter(37, 10, 'Менеджерские надбавки БА (%) для ранга Региональный менеджер', '0.07');
CALL insertRewardParameter(38, 10, 'Менеджерские надбавки БА (%) для ранга Серебрянный директор', '0.09');
CALL insertRewardParameter(39, 10, 'Менеджерские надбавки БА (%) для ранга Золотой директор', '0.09');
CALL insertRewardParameter(40, 10, 'Менеджерские надбавки БА (%) для ранга Платиновый директор', '0.09');

CALL insertRewardParameter(41, 8, 'Вознаграждение за КП', '25');
CALL insertRewardParameter(42, 8, 'Вознаграждение за КП по заявке из САП', '15');

CALL insertRewardParameter(43, 6, 'Минимальное количество баллов для получения бонуса за личный финансовый оборот', '2000');
CALL insertRewardParameter(44, 6, 'Среднее значение количества баллов для получения бонуса за личный финансовый оборот', '3000');
CALL insertRewardParameter(45, 6, 'Бонус за личный финансовый оборот меньше среднего значения', '50');
CALL insertRewardParameter(46, 6, 'Бонус за личный финансовый оборот больше средного значения', '100');


-- Update

INSERT INTO `update` (`version`) VALUE ('20191120_1.0.39');