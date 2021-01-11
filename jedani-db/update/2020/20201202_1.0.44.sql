CALL insertRewardParameter(50, 11, 'Выплаты (%) за групповой оборот для ранга Менеджер ассистент', '1.5');
CALL insertRewardParameter(51, 11, 'Выплаты (%) за групповой оборот для ранга Менеджер юниор', '3');
CALL insertRewardParameter(52, 11, 'Выплаты (%) за групповой оборот для ранга Тим менеджер', '5');
CALL insertRewardParameter(53, 11, 'Выплаты (%) за групповой оборот для ранга Ассистент сеньора', '5.5');
CALL insertRewardParameter(54, 11, 'Выплаты (%) за групповой оборот для ранга Сеньор менеджер', '6.5');
CALL insertRewardParameter(55, 11, 'Выплаты (%) за групповой оборот для ранга Дивизион менеджер', '7.5');
CALL insertRewardParameter(56, 11, 'Выплаты (%) за групповой оборот для ранга Ареа менеджер', '8');
CALL insertRewardParameter(57, 11, 'Выплаты (%) за групповой оборот для ранга Региональный менеджер', '9');
CALL insertRewardParameter(58, 11, 'Выплаты (%) за групповой оборот для ранга Серебрянный директор', '10');
CALL insertRewardParameter(59, 11, 'Выплаты (%) за групповой оборот для ранга Золотой директор', '11');
CALL insertRewardParameter(60, 11, 'Выплаты (%) за групповой оборот для ранга Платиновый директор', '12');

CALL insertDomainText(11, 'reward_type', 'Оборот группы', 'Оборот групи');
CALL insertDomainText(12, 'reward_type', 'Оборот структуры', 'Оборот структури');
CALL insertDomainText(13, 'reward_type', 'Ранг', 'Ранг');

CALL createEntityAttribute(40, 14, 4, 'Личный финансовый оборот', 'Особистий фінансовий оборот');
CALL createEntityAttribute(40, 15, 4, 'Финансовый оборот группы', 'Фінансовий оборот групи');
CALL createEntityAttribute(40, 16, 4, 'Оборот структуры', 'Оборот структури');
CALL createEntityAttribute(40, 17, 4, 'Финансовый оборот структуры', 'Фінансовий оборот структури');
CALL createEntityAttributeWithReference(40, 18, 11, 20, 'Менеджер', 'Менеджер');
CALL createEntityAttributeWithReference(40, 19, 11, 38, 'Ранг менеджера', 'Ранг менеджера');

-- Update

INSERT INTO `update` (`version`) VALUE ('20201202_1.0.44');