-- Manager Mycook bonus reward type

CALL insertDomainText(9, 'reward_type', 'Менеджерский Майкук бонус', 'Менеджерський Майкук бонус');

-- Reward sale attribute

CALL createEntityAttributeWithReference(40, 7, 11, 28, 'Продажа', 'Продаж');

-- Update

INSERT INTO `update` (`version`) VALUE ('20191028_1.0.35');