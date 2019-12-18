-- Add Reward attributes

CALL createEntityAttribute(40, 11, 4, 'Курс', 'Курс');
CALL createEntityAttribute(40, 12, 4, 'Скидка', 'Знижка');
CALL createEntityAttribute(40, 13, 4, 'Сумма (в локальной валюте)', 'Сума (в локальній валюті)');

-- Update

INSERT INTO `update` (`version`) VALUE ('20191218_1.0.41');