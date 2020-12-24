CALL createEntityAttribute(40, 21, 5, 'Статус', 'Статус');

DELIMITER //

CREATE PROCEDURE updateRewardStatus() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT UNSIGNED;

    DECLARE cur CURSOR FOR SELECT r.id FROM reward r
        LEFT JOIN reward_attribute ra_m on r.id = ra_m.domain_id AND ra_m.entity_attribute_id = 8 AND ra_m.status = 1
        LEFT JOIN reward_attribute ra_s on r.id = ra_s.domain_id AND ra_s.entity_attribute_id = 8 AND ra_s.status = 21
        WHERE ra_m.date <= (SELECT pa_d.date FROM period p
            LEFT JOIN period_attribute pa_d ON p.id = pa_d.domain_id AND pa_d.entity_attribute_id = 1 AND pa_d.status = 1
            LEFT JOIN period_attribute pa_c ON p.id = pa_c.domain_id AND pa_c.entity_attribute_id = 2 AND pa_c.status = 1
          WHERE p.status = 1 AND pa_c.date IS NULL) AND ra_s.number IS NULL ;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id;
        IF done THEN
            LEAVE _loop;
        END IF;
        INSERT INTO reward_attribute(domain_id, entity_attribute_id, number)
            VALUE (domain_id, 21, 2);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateRewardStatus();


DROP PROCEDURE updateNomenclatureType;
DROP PROCEDURE updateWorkerType;
DROP PROCEDURE updateWorkerStatus;
DROP PROCEDURE updateRewardStatus;


INSERT INTO `update` (`version`) VALUE ('20201224_1.0.47');