-- ---------------------------
-- Currency
-- ---------------------------

DROP TABLE IF EXISTS `currency`;
CREATE TABLE `currency`
(
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `ft_currency__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_currency__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_currency__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Нац.валюта';

DROP TABLE IF EXISTS `currency_attribute`;
CREATE TABLE `currency_attribute`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text`                VARCHAR(255) COMMENT 'Текст',
  `number`              BIGINT(20) COMMENT 'Число',
  `date`                DATETIME COMMENT 'Дата',
  `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_currency_attribute__currency` FOREIGN KEY (`object_id`) REFERENCES `currency` (`object_id`),
  CONSTRAINT `fk_currency_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
  CONSTRAINT `fk_currency_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты нац.валюты';

DROP TABLE IF EXISTS `currency_value`;
CREATE TABLE `currency_value`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
  `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
  `number`       BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_currency_value__currency_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `currency_attribute` (`id`),
  CONSTRAINT `fk_currency_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов нац.валюты';

/* Currency */

INSERT INTO `sequence` (`name`) VALUE ('currency');

INSERT INTO `entity` (`id`, `name`) VALUE (30, 'currency');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (30, 1, 'Нац.валюта'), (30, 2, 'Нац.валюті');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (30, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (30, 1, 1, 'Название'), (30, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (30, 2, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (30, 2, 1, 'Код'), (30, 2, 2, 'Код');

-- Currency

INSERT INTO currency (id, object_id, status) VALUES (1, 1, 1);
INSERT INTO currency_attribute (id, object_id, entity_attribute_id, status) VALUES (1, 1, 1, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (1, 1, 'Рубль');
INSERT INTO currency_attribute (id, object_id, entity_attribute_id, status, text) VALUES (2, 1, 2, 1, 'RUB');

INSERT INTO currency (id, object_id, status) VALUES (2, 2, 1);
INSERT INTO currency_attribute (id, object_id, entity_attribute_id, status) VALUES (3, 2, 1, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (3, 1, 'Гривна');
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (3, 2, 'Гривня');
INSERT INTO currency_attribute (id, object_id, entity_attribute_id, status, text) VALUES (4, 2, 2, 1, 'UAH');

INSERT INTO currency (id, object_id, status) VALUES (3, 3, 1);
INSERT INTO currency_attribute (id, object_id, entity_attribute_id, status) VALUES (5, 3, 1, 1);
INSERT INTO currency_value (attribute_id, locale_id, text) VALUES (5, 1, 'Евро');
INSERT INTO currency_attribute (id, object_id, entity_attribute_id, status, text) VALUES (6, 3, 2, 1, 'EUR');

-- ---------------------------
-- Exchange rate
-- ---------------------------

DROP TABLE IF EXISTS `exchange_rate`;
CREATE TABLE `exchange_rate`
(
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`        BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id`        BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date`       TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date`         TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status`           INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `permission_id`    BIGINT(20) NULL COMMENT 'Ключ прав доступа к объекту',
  `user_id`          BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_object_id__status` (`object_id`, `status`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `ft_exchange_rate__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_exchange_rate__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `fk_exchange_rate__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Курсы нац.валют';

DROP TABLE IF EXISTS `exchange_rate_attribute`;
CREATE TABLE `exchange_rate_attribute`
(
  `id`                  BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id`           BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `text`                VARCHAR(255) COMMENT 'Текст',
  `number`              BIGINT(20) COMMENT 'Число',
  `date`                DATETIME COMMENT 'Дата',
  `start_date`          TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date`            TIMESTAMP  NULL     DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status`              INTEGER    NOT NULL DEFAULT 1 COMMENT 'Статус',
  `user_id`             BIGINT(20) NULL COMMENT 'Идентифитактор пользователя',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_text` (`text`),
  KEY `key_number` (`number`),
  KEY `key_date` (`date`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_exchange_rate_attribute__exchange_rate` FOREIGN KEY (`object_id`) REFERENCES `exchange_rate` (`object_id`),
  CONSTRAINT `fk_exchange_rate_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
    REFERENCES entity_attribute (`entity_attribute_id`),
  CONSTRAINT `fk_exchange_rate_attribute__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Атрибуты курсов нац.валют';

DROP TABLE IF EXISTS `exchange_rate_value`;
CREATE TABLE `exchange_rate_value`
(
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `locale_id`    BIGINT(20) COMMENT 'Идентификатор локали',
  `text`         VARCHAR(1000) COMMENT 'Текстовое значение',
  `number`       BIGINT(20) COMMENT 'Числовое значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`attribute_id`, `locale_id`),
  KEY `key_attribute_id` (`attribute_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`text`(128)),
  CONSTRAINT `fk_exchange_rate_value__exchange_rate_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `exchange_rate_attribute` (`id`),
  CONSTRAINT `fk_exchange_rate_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE = InnoDB
  CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT 'Значения атрибутов курсов нац.валют';

/* Exchange rate */

INSERT INTO `sequence` (`name`) VALUE ('exchange_rate');

INSERT INTO `entity` (`id`, `name`) VALUE (31, 'exchange_rate');
INSERT INTO `entity_value`(`entity_id`, `locale_id`, `text`) VALUES (31, 1, 'Курс нац.валюты'), (31, 2, 'Курс нац.валюті');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 1, 0);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 1, 1, 'Название'), (31, 1, 2, 'Назва');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 2, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 2, 1, 'Код'), (31, 2, 2, 'Код');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (31, 3, 11, 30);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 3, 1, 'Базовая валюта'), (31, 3, 2, 'Базова валюта');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`, `reference_id`) VALUES (31, 4, 11, 30);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 4, 1, 'Котируемая валюта'), (31, 4, 2, 'Котирувана валюта');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 5, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 5, 1, 'uri_xml');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 6, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 6, 1, 'xpath_name');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 7, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 7, 1, 'xpath_code');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 8, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 8, 1, 'xpath_date');

INSERT INTO `entity_attribute`(`entity_id`, `entity_attribute_id`, `value_type_id`) VALUES (31, 9, 2);
INSERT INTO `entity_value`(`entity_id`, `entity_attribute_id`, `locale_id`, `text`) VALUES (31, 9, 1, 'xpath_value');

-- Exchange rate

INSERT INTO exchange_rate (id, object_id, status) VALUES (1, 1, 1);
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status) VALUES (1, 1, 1, 1);
INSERT INTO exchange_rate_value (attribute_id, locale_id, text) VALUES (1, 1, 'EUR/RUB (ЦБ РФ');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (2, 1, 2, 1, 'EUR/RUB');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, number) VALUES (3, 1, 3, 1, 3);
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, number) VALUES (4, 1, 4, 1, 1);
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (5, 1, 5, 1, 'http://www.cbr.ru/scripts/XML_daily.asp');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (6, 1, 6, 1, '/ValCurs/Valute[@ID=\'R01239\']/Name/text()');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (7, 1, 7, 1, '/ValCurs/Valute[@ID=\'R01239\']/CharCode/text()');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (8, 1, 8, 1, '/ValCurs/@Date');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (9, 1, 9, 1, '/ValCurs/Valute[@ID=\'R01239\']/Value/text()');

INSERT INTO exchange_rate (id, object_id, status) VALUES (2, 2, 1);
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status) VALUES (10, 2, 1, 1);
INSERT INTO exchange_rate_value (attribute_id, locale_id, text) VALUES (10, 1, 'EUR/UAH (НБУ)');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (11, 2, 2, 1, 'EUR/UAH');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, number) VALUES (12, 2, 3, 1, 3);
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, number) VALUES (13, 2, 4, 1, 2);
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (14, 2, 5, 1, 'http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=EUR&date=');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (15, 2, 6, 1, 'exchange/currency/txt/text()');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (16, 2, 7, 1, 'exchange/currency/cc/text()');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (17, 2, 8, 1, 'exchange/currency/exchangedate/text()');
INSERT INTO exchange_rate_attribute (id, object_id, entity_attribute_id, status, text) VALUES (18, 2, 9, 1, 'exchange/currency/rate/text()');


-- Update

INSERT INTO `update` (`version`) VALUE ('20190227_1.0.7');