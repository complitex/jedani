-- Update worker status

UPDATE entity_value SET text = 'Статус' WHERE entity_id = 20 AND entity_attribute_id = 16;
UPDATE worker_attribute SET status = 2 WHERE entity_attribute_id = 16 AND text = '0';

DELIMITER //

CREATE PROCEDURE updateWorkerStatus() BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE domain_id BIGINT UNSIGNED;
    DECLARE cur CURSOR FOR SELECT w.id FROM worker w WHERE
        (SELECT COUNT(wa_m.id) > 1 FROM worker_attribute wa_m WHERE wa_m.domain_id = w.id
            AND wa_m.entity_attribute_id = 17)
         AND NOT EXISTS(SELECT wa_s.id FROM worker_attribute wa_s WHERE wa_s.domain_id = w.id
            AND wa_s.entity_attribute_id = 16 AND wa_s.number = 1 AND wa_s.status = 1);

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO domain_id;
        IF done THEN
            LEAVE _loop;
        END IF;
        INSERT INTO worker_attribute(domain_id, entity_attribute_id, number, status, user_id)
            VALUE (domain_id, 16, 1, 1, 1);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateWorkerStatus();

-- Update

INSERT INTO `update` (`version`) VALUE ('20190825_1.0.26');