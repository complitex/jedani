DELIMITER //

CREATE PROCEDURE updateWorkerType() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT UNSIGNED;
    DECLARE cur CURSOR FOR SELECT w.id FROM worker w
        LEFT JOIN worker_attribute wa_t ON wa_t.domain_id = w.id AND wa_t.entity_attribute_id = 21 AND wa_t.status = 1
        WHERE wa_t.id IS NULL OR wa_t.number IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id;
        IF done THEN
            LEAVE _loop;
        END IF;
        INSERT INTO worker_attribute(domain_id, entity_attribute_id, number, status, user_id)
            VALUE (domain_id, 21, 2, 1, 1);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateWorkerType();

-- Update

INSERT INTO `update` (`version`) VALUE ('20190826_1.0.27');