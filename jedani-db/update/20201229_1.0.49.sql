CALL createEntityAttributeWithReference(28, 23, 11, 42, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttributeWithReference(40, 24, 11, 42, 'Операционный месяц', 'Операційний місяць');
CALL createEntityAttributeWithReference(41, 11, 11, 42, 'Операционный месяц', 'Операційний місяць');

DELIMITER //

CREATE PROCEDURE updateSalePeriods() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT;
    DECLARE sale_date DATETIME;
    DECLARE period_id BIGINT;

    DECLARE cur CURSOR FOR SELECT s.id, sa.date, p.object_id FROM sale s
        LEFT JOIN sale_attribute sa ON s.id = sa.domain_id AND sa.entity_attribute_id = 5 AND sa.status = 1
        LEFT JOIN period_attribute pa ON pa.entity_attribute_id = 1 AND pa.status = 1 AND
                 month(sa.date) = month(pa.date) AND year(sa.date) = year(pa.date)
        LEFT JOIN period p ON pa.domain_id = p.id AND p.status = 1
        LEFT JOIN sale_attribute sa_p ON s.id = sa_p.domain_id AND sa_p.entity_attribute_id = 23 AND sa_p.status = 1
    WHERE s.status = 1 AND sa_p.number IS NULL AND p.object_id IS NOT NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id, sale_date, period_id;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO sale_attribute(domain_id, entity_attribute_id, number)
            VALUE (domain_id, 23, period_id);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateSalePeriods();

DROP PROCEDURE updateSalePeriods;


DELIMITER //

CREATE PROCEDURE updatePaymentPeriods() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT;
    DECLARE payment_date DATETIME;
    DECLARE period_id BIGINT;

    DECLARE cur CURSOR FOR SELECT pt.id, pta.date, p.object_id FROM payment pt
        LEFT JOIN payment_attribute pta ON pt.id = pta.domain_id AND pta.entity_attribute_id = 2 AND pta.status = 1
        LEFT JOIN period_attribute pa ON pa.entity_attribute_id = 1 AND pa.status = 1 AND
                 month(pta.date) = month(pa.date) AND year(pta.date) = year(pa.date)
        LEFT JOIN period p ON pa.domain_id = p.id AND p.status = 1
        LEFT JOIN payment_attribute pta_p ON pt.id = pta_p.domain_id AND pta_p.entity_attribute_id = 11 AND pta_p.status = 1
    WHERE pt.status = 1 AND pta_p.number IS NULL AND p.object_id IS NOT NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id, payment_date, period_id;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO payment_attribute(domain_id, entity_attribute_id, number)
            VALUE (domain_id, 11, period_id);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updatePaymentPeriods();

DROP PROCEDURE updatePaymentPeriods;


DELIMITER //

CREATE PROCEDURE updateRewardPeriods() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT;
    DECLARE reward_date DATETIME;
    DECLARE period_id BIGINT;

    DECLARE cur CURSOR FOR SELECT r.id, ra.date, p.object_id FROM reward r
        LEFT JOIN reward_attribute ra ON r.id = ra.domain_id AND ra.entity_attribute_id = 8 AND ra.status = 1
        LEFT JOIN period_attribute pa ON pa.entity_attribute_id = 1 AND pa.status = 1 AND
                 month(ra.date) = month(pa.date) AND year(ra.date) = year(pa.date)
        LEFT JOIN period p ON pa.domain_id = p.id AND p.status = 1
        LEFT JOIN reward_attribute ra_p ON r.id = ra_p.domain_id AND ra_p.entity_attribute_id = 24 AND ra_p.status = 1
    WHERE r.status = 1 AND ra_p.number IS NULL AND p.object_id IS NOT NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id, reward_date, period_id;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO reward_attribute(domain_id, entity_attribute_id, number)
            VALUE (domain_id, 24, period_id);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateRewardPeriods();

DROP PROCEDURE updateRewardPeriods;



INSERT INTO `update` (`version`) VALUE ('20201229_1.0.49');