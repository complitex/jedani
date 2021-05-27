DELIMITER //

CREATE PROCEDURE updateTransferDate()
BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE id BIGINT;
    DECLARE start_date TIMESTAMP;

    DECLARE cur CURSOR FOR SELECT f.id, f.start_date FROM transfer f
        LEFT JOIN transfer_attribute f_d ON f_d.domain_id = f.id AND f_d.entity_attribute_id = 14 AND f_d.status = 1
        WHERE f.status = 1 AND f_d.date IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO id, start_date;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO transfer_attribute(domain_id, entity_attribute_id, date) VALUE (id, 14, start_date);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateTransferDate();

DROP PROCEDURE updateTransferDate;


INSERT INTO `update` (`version`) VALUE ('2.0.7');
