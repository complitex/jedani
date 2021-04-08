UPDATE last_name_value SET text = UPPER(text);
UPDATE first_name_value SET text = UPPER(text);
UPDATE middle_name_value SET text = UPPER(text);


INSERT INTO `update` (`version`) VALUE ('2.0.2');
