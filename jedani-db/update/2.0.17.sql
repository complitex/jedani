DROP PROCEDURE insertRewardParameter;

DELIMITER //

CREATE PROCEDURE insertParameter(IN parameterId BIGINT, IN parameterTypeId BIGINT, IN name VARCHAR(128) CHARSET utf8)
BEGIN
    SET @insert = CONCAT('INSERT INTO `parameter` (object_id) VALUE(0);');
    PREPARE QUERY FROM @insert;
    EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @objectId = last_insert_id();

    SET @update = CONCAT('UPDATE `parameter` SET object_id = ', @objectId, ' WHERE id = ', @objectId);
    PREPARE QUERY FROM @update;
    EXECUTE QUERY; DEALLOCATE PREPARE QUERY;


    SET @insert = CONCAT('INSERT INTO `parameter_attribute`(domain_id, entity_attribute_id, number) ',
                         'VALUE (', @objectId, ', 1,', parameterId, ');');
    PREPARE QUERY FROM @insert;
    EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insert = CONCAT('INSERT INTO `parameter_attribute`(domain_id, entity_attribute_id, number) ',
                         'VALUE (', @objectId, ', 2,', parameterTypeId, ');');
    PREPARE QUERY FROM @insert;
    EXECUTE QUERY; DEALLOCATE PREPARE QUERY;

    SET @insert = CONCAT('INSERT INTO `parameter_attribute`(domain_id, entity_attribute_id, text) ',
                         'VALUE (', @objectId, ', 3,''', name, ''');');
    PREPARE QUERY FROM @insert;
    EXECUTE QUERY; DEALLOCATE PREPARE QUERY;
END //

DELIMITER ;

CALL insertParameter(1, 1, 'Базовое вознаграждение при продаже МК по заявке из САП');
CALL insertParameter(2, 1, 'Базовое вознаграждение для ПК со статусом "Promo" (МК премиум)');
CALL insertParameter(3, 1, 'Базовое вознаграждение для ПК со статусом "Promo" (МК тач)');
CALL insertParameter(4, 1, 'Базовое вознаграждение для ПК со статусом "Just" (МК премиум)');
CALL insertParameter(5, 1, 'Базовое вознаграждение для ПК со статусом "Just" (МК тач)');
CALL insertParameter(6, 1, 'Базовое вознаграждение для ПК со статусом "VIP" (МК премиум)');
CALL insertParameter(7, 1, 'Базовое вознаграждение для ПК со статусом "VIP" (МК тач)');
CALL insertParameter(8, 1, 'Базовое вознаграждение при продаже БА (% от суммы ДКП)');

CALL insertParameter(9, 1, 'Менеджерский Майкук бонус (МК премиум)');
CALL insertParameter(10, 1, 'Менеджерский Майкук бонус (МК тач)');

CALL insertParameter(11, 1, 'Групповой оборот для получения ранга Менеджер ассистент');
CALL insertParameter(12, 1, 'Групповой оборот для получения ранга Менеджер юниор');
CALL insertParameter(13, 1, 'Групповой оборот для получения ранга Тим менеджер');
CALL insertParameter(14, 1, 'Групповой оборот для получения ранга Ассистент сеньора');
CALL insertParameter(15, 1, 'Групповой оборот для получения ранга Сеньор менеджер');
CALL insertParameter(16, 1, 'Групповой оборот для получения ранга Дивизион менеджер');
CALL insertParameter(17, 1, 'Групповой оборот для получения ранга Ареа менеджер');
CALL insertParameter(18, 1, 'Групповой оборот для получения ранга Региональный менеджер');
CALL insertParameter(19, 1, 'Групповой оборот для получения ранга Серебрянный директор');
CALL insertParameter(20, 1, 'Групповой оборот для получения ранга Золотой директор');
CALL insertParameter(21, 1, 'Групповой оборот для получения ранга Платиновый директор');

CALL insertParameter(22, 1, 'Менеджерские надбавки МК для ранга Менеджер ассистент');
CALL insertParameter(23, 1, 'Менеджерские надбавки МК для ранга Менеджер юниор');
CALL insertParameter(24, 1, 'Менеджерские надбавки МК для ранга Тим менеджер');
CALL insertParameter(25, 1, 'Менеджерские надбавки МК для ранга Ассистент сеньора');
CALL insertParameter(26, 1, 'Менеджерские надбавки МК для ранга Сеньор менеджер');
CALL insertParameter(27, 1, 'Менеджерские надбавки МК для ранга Дивизион менеджер');
CALL insertParameter(28, 1, 'Менеджерские надбавки МК для ранга Ареа менеджер');
CALL insertParameter(29, 1, 'Менеджерские надбавки МК для ранга Региональный менеджер');
CALL insertParameter(30, 1, 'Менеджерские надбавки МК для ранга Серебрянный директор');
CALL insertParameter(31, 1, 'Менеджерские надбавки МК для ранга Золотой директор');
CALL insertParameter(32, 1, 'Менеджерские надбавки МК для ранга Платиновый директор');

CALL insertParameter(33, 1, 'Менеджерские надбавки БА (%) для ранга Менеджер ассистент');
CALL insertParameter(34, 1, 'Менеджерские надбавки БА (%) для ранга Менеджер юниор');
CALL insertParameter(35, 1, 'Менеджерские надбавки БА (%) для ранга Тим менеджер');
CALL insertParameter(36, 1, 'Менеджерские надбавки БА (%) для ранга Ассистент сеньора');
CALL insertParameter(37, 1, 'Менеджерские надбавки БА (%) для ранга Сеньор менеджер');
CALL insertParameter(38, 1, 'Менеджерские надбавки БА (%) для ранга Дивизион менеджер');
CALL insertParameter(39, 1, 'Менеджерские надбавки БА (%) для ранга Ареа менеджер');
CALL insertParameter(40, 1, 'Менеджерские надбавки БА (%) для ранга Региональный менеджер');
CALL insertParameter(41, 1, 'Менеджерские надбавки БА (%) для ранга Серебрянный директор');
CALL insertParameter(42, 1, 'Менеджерские надбавки БА (%) для ранга Золотой директор');
CALL insertParameter(43, 1, 'Менеджерские надбавки БА (%) для ранга Платиновый директор');

CALL insertParameter(44, 1, 'Вознаграждение за КП');
CALL insertParameter(45, 1, 'Вознаграждение за КП по заявке из САП');

CALL insertParameter(46, 1, 'Минимальное количество баллов для получения бонуса за личный финансовый оборот');
CALL insertParameter(47, 1, 'Среднее значение количества баллов для получения бонуса за личный финансовый оборот');
CALL insertParameter(48, 1, 'Бонус за личный финансовый оборот меньше среднего значения');
CALL insertParameter(49, 1, 'Бонус за личный финансовый оборот больше средного значения');

CALL insertParameter(50, 1, 'Выплаты (%) за групповой оборот для ранга Менеджер ассистент');
CALL insertParameter(51, 1, 'Выплаты (%) за групповой оборот для ранга Менеджер юниор');
CALL insertParameter(52, 1, 'Выплаты (%) за групповой оборот для ранга Тим менеджер');
CALL insertParameter(53, 1, 'Выплаты (%) за групповой оборот для ранга Ассистент сеньора');
CALL insertParameter(54, 1, 'Выплаты (%) за групповой оборот для ранга Сеньор менеджер');
CALL insertParameter(55, 1, 'Выплаты (%) за групповой оборот для ранга Дивизион менеджер');
CALL insertParameter(56, 1, 'Выплаты (%) за групповой оборот для ранга Ареа менеджер');
CALL insertParameter(57, 1, 'Выплаты (%) за групповой оборот для ранга Региональный менеджер');
CALL insertParameter(58, 1, 'Выплаты (%) за групповой оборот для ранга Серебрянный директор');
CALL insertParameter(59, 1, 'Выплаты (%) за групповой оборот для ранга Золотой директор');
CALL insertParameter(60, 1, 'Выплаты (%) за групповой оборот для ранга Платиновый директор');

DROP PROCEDURE insertParameter;

INSERT INTO `update` (`version`) VALUE ('2.0.17');