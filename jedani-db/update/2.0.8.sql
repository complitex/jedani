CALL createEntityAttribute(26, 15, 6, 'Дата приёма', 'Дата прийому');

DELIMITER //

CREATE PROCEDURE updateTransferReceiveDate()
BEGIN
    DECLARE done BOOLEAN DEFAULT FALSE;
    DECLARE id BIGINT;
    DECLARE end_date TIMESTAMP;

    DECLARE cur CURSOR FOR SELECT f.id, f.end_date FROM transfer f
         LEFT JOIN transfer_attribute f_d ON f_d.domain_id = f.id AND f_d.entity_attribute_id = 15 AND f_d.status = 1
         WHERE f.status = 1 AND f_d.date IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;

    OPEN cur;

    _loop: LOOP
        FETCH cur INTO id, end_date;

        IF done THEN
            LEAVE _loop;
        END IF;

        INSERT INTO transfer_attribute(domain_id, entity_attribute_id, date) VALUE (id, 15, end_date);
    END LOOP _loop;

    CLOSE cur;
END //

DELIMITER ;

CALL updateTransferReceiveDate();

DROP PROCEDURE updateTransferReceiveDate;


INSERT INTO `update` (`version`) VALUE ('2.0.8');
