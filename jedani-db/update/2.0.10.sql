DELIMITER //

CREATE PROCEDURE updateSaleStatus()
BEGIN
DECLARE done BOOLEAN DEFAULT FALSE;
DECLARE id BIGINT;

DECLARE cur CURSOR FOR
    SELECT s.id FROM sale s
        LEFT JOIN sale_attribute sa ON s.id = sa.domain_id AND sa.entity_attribute_id = 15 AND sa.status = 1
            WHERE sa.number IS NULL;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

OPEN cur;

_loop: LOOP
        FETCH cur INTO id;

IF done THEN
            LEAVE _loop;
END IF;

INSERT INTO sale_attribute(domain_id, entity_attribute_id, number) VALUE (id, 15, 7);

END LOOP _loop;

CLOSE cur;
END //

DELIMITER ;


CALL updateSaleStatus();

DROP PROCEDURE updateSaleStatus;


DELIMITER //

CREATE PROCEDURE updateSalePeriod1()
BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE id BIGINT;

    DECLARE cur CURSOR FOR
        SELECT s.id FROM sale s
            LEFT JOIN sale_attribute sa ON s.id = sa.domain_id AND sa.entity_attribute_id = 23 AND sa.status = 1
                WHERE sa.number IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO id;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO sale_attribute(domain_id, entity_attribute_id, number) VALUE (id, 23, 1);

    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateSalePeriod1();

DROP PROCEDURE updateSalePeriod1;


DROP PROCEDURE IF EXISTS updatePaymentPeriods;
DROP PROCEDURE IF EXISTS updateRewardPeriods;
DROP PROCEDURE IF EXISTS updateSalePeriods;
