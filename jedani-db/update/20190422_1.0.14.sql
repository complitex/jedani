-- Update domain attribute model

ALTER TABLE setting_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE setting_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE setting_attribute DROP FOREIGN KEY fk_setting_attribute__setting;
ALTER TABLE setting_attribute ADD CONSTRAINT fk_setting_attribute__setting FOREIGN KEY (domain_id) REFERENCES setting (id);
UPDATE setting_attribute a LEFT JOIN setting d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE setting_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE setting_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on setting;

ALTER TABLE country_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE country_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE country_attribute DROP FOREIGN KEY fk_country_attribute__country;
ALTER TABLE country_attribute ADD CONSTRAINT fk_country_attribute__country FOREIGN KEY (domain_id) REFERENCES country (id);
UPDATE country_attribute a LEFT JOIN country d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE country_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE country_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on country;

ALTER TABLE region_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE region_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE region_attribute DROP FOREIGN KEY fk_region_attribute__region;
ALTER TABLE region_attribute ADD CONSTRAINT fk_region_attribute__region FOREIGN KEY (domain_id) REFERENCES region (id);
UPDATE region_attribute a LEFT JOIN region d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE region_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE region_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on region;

ALTER TABLE city_type_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE city_type_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE city_type_attribute DROP FOREIGN KEY fk_city_type_attribute__city_type;
ALTER TABLE city_type_attribute ADD CONSTRAINT fk_city_type_attribute__city_type FOREIGN KEY (domain_id) REFERENCES city_type (id);
UPDATE city_type_attribute a LEFT JOIN city_type d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE city_type_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE city_type_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on city_type;

ALTER TABLE city_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE city_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE city_attribute DROP FOREIGN KEY fk_city_attribute__city;
ALTER TABLE city_attribute ADD CONSTRAINT fk_city_attribute__city FOREIGN KEY (domain_id) REFERENCES city (id);
UPDATE city_attribute a LEFT JOIN city d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE city_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE city_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on city;

ALTER TABLE last_name_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE last_name_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE last_name_attribute DROP FOREIGN KEY fk_last_name_attribute__city;
ALTER TABLE last_name_attribute ADD CONSTRAINT fk_last_name_attribute__last_name FOREIGN KEY (domain_id) REFERENCES last_name (id);
UPDATE last_name_attribute a LEFT JOIN last_name d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE last_name_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE last_name_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on last_name;

ALTER TABLE first_name_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE first_name_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE first_name_attribute DROP FOREIGN KEY fk_first_name_attribute__city;
ALTER TABLE first_name_attribute ADD CONSTRAINT fk_first_name_attribute__first_name FOREIGN KEY (domain_id) REFERENCES first_name (id);
UPDATE first_name_attribute a LEFT JOIN first_name d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE first_name_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE first_name_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on first_name;

ALTER TABLE middle_name_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE middle_name_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE middle_name_attribute DROP FOREIGN KEY fk_middle_name_attribute__city;
ALTER TABLE middle_name_attribute ADD CONSTRAINT fk_middle_name_attribute__middle_name FOREIGN KEY (domain_id) REFERENCES middle_name (id);
UPDATE middle_name_attribute a LEFT JOIN middle_name d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE middle_name_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE middle_name_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on middle_name;

ALTER TABLE worker_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE worker_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE worker_attribute DROP FOREIGN KEY fk_worker_attribute__city;
ALTER TABLE worker_attribute ADD CONSTRAINT fk_worker_attribute__worker FOREIGN KEY (domain_id) REFERENCES worker (id);
UPDATE worker_attribute a LEFT JOIN worker d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE worker_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE worker_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on worker;

ALTER TABLE mk_status_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE mk_status_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE mk_status_attribute DROP FOREIGN KEY fk_mk_status_attribute__mk_status;
ALTER TABLE mk_status_attribute ADD CONSTRAINT fk_mk_status_attribute__mk_status FOREIGN KEY (domain_id) REFERENCES mk_status (id);
UPDATE mk_status_attribute a LEFT JOIN mk_status d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE mk_status_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE mk_status_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on mk_status;

ALTER TABLE position_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE position_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE position_attribute DROP FOREIGN KEY fk_position_attribute__position;
ALTER TABLE position_attribute ADD CONSTRAINT fk_position_attribute__position FOREIGN KEY (domain_id) REFERENCES position (id);
UPDATE position_attribute a LEFT JOIN position d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE position_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE position_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on position;

ALTER TABLE nomenclature_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE nomenclature_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE nomenclature_attribute DROP FOREIGN KEY fk_nomenclature_attribute__nomenclature;
ALTER TABLE nomenclature_attribute ADD CONSTRAINT fk_nomenclature_attribute__nomenclature FOREIGN KEY (domain_id) REFERENCES nomenclature (id);
UPDATE nomenclature_attribute a LEFT JOIN nomenclature d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE nomenclature_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE nomenclature_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on nomenclature;

ALTER TABLE storage_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE storage_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE storage_attribute DROP FOREIGN KEY fk_storage_attribute__storage;
ALTER TABLE storage_attribute ADD CONSTRAINT fk_storage_attribute__storage FOREIGN KEY (domain_id) REFERENCES storage (id);
UPDATE storage_attribute a LEFT JOIN storage d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE storage_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE storage_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on storage;

ALTER TABLE product_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE product_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE product_attribute DROP FOREIGN KEY fk_product_attribute__product;
ALTER TABLE product_attribute ADD CONSTRAINT fk_product_attribute__product FOREIGN KEY (domain_id) REFERENCES product (id);
UPDATE product_attribute a LEFT JOIN product d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE product_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE product_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on product;

ALTER TABLE transaction_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE transaction_attribute ADD KEY key_domain_id(domain_id);
-- ALTER TABLE transaction_attribute DROP FOREIGN KEY  fk_transaction_attribute__transaction;
ALTER TABLE transaction_attribute ADD CONSTRAINT fk_transaction_attribute__transaction FOREIGN KEY (domain_id) REFERENCES transaction (id);
UPDATE transaction_attribute a LEFT JOIN transaction d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE transaction_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE transaction_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on transaction;

ALTER TABLE promotion_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE promotion_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE promotion_attribute DROP FOREIGN KEY fk_promotion_attribute__promotion;
ALTER TABLE promotion_attribute ADD CONSTRAINT fk_promotion_attribute__promotion FOREIGN KEY (domain_id) REFERENCES promotion (id);
UPDATE promotion_attribute a LEFT JOIN promotion d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE promotion_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE promotion_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on promotion;

ALTER TABLE sale_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE sale_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE sale_attribute DROP FOREIGN KEY fk_sale_attribute__sale;
ALTER TABLE sale_attribute ADD CONSTRAINT fk_sale_attribute__sale FOREIGN KEY (domain_id) REFERENCES sale (id);
UPDATE sale_attribute a LEFT JOIN sale d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE sale_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE sale_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on sale;

ALTER TABLE sale_item_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE sale_item_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE sale_item_attribute DROP FOREIGN KEY fk_sale_item_attribute__sale_item;
ALTER TABLE sale_item_attribute ADD CONSTRAINT fk_sale_item_attribute__sale_item FOREIGN KEY (domain_id) REFERENCES sale_item (id);
UPDATE sale_item_attribute a LEFT JOIN sale_item d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE sale_item_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE sale_item_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on sale_item;

ALTER TABLE currency_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE currency_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE currency_attribute DROP FOREIGN KEY fk_currency_attribute__currency;
ALTER TABLE currency_attribute ADD CONSTRAINT fk_currency_attribute__currency FOREIGN KEY (domain_id) REFERENCES currency (id);
UPDATE currency_attribute a LEFT JOIN currency d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE currency_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE currency_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on currency;

ALTER TABLE exchange_rate_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE exchange_rate_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE exchange_rate_attribute DROP FOREIGN KEY fk_exchange_rate_attribute__exchange_rate;
ALTER TABLE exchange_rate_attribute ADD CONSTRAINT fk_exchange_rate_attribute__exchange_rate FOREIGN KEY (domain_id) REFERENCES exchange_rate (id);
UPDATE exchange_rate_attribute a LEFT JOIN exchange_rate d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE exchange_rate_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE exchange_rate_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on exchange_rate;

ALTER TABLE price_attribute ADD COLUMN domain_id BIGINT(20);
ALTER TABLE price_attribute ADD KEY key_domain_id(domain_id);
ALTER TABLE price_attribute DROP FOREIGN KEY fk_price_attribute__price;
ALTER TABLE price_attribute ADD CONSTRAINT fk_price_attribute__price FOREIGN KEY (domain_id) REFERENCES price (id);
UPDATE price_attribute a LEFT JOIN price d on a.object_id = d.object_id SET a.domain_id = d.id WHERE a.id > 0;
ALTER TABLE price_attribute MODIFY COLUMN domain_id BIGINT(20) NOT NULL COMMENT 'Идентификатор домена' AFTER id;
ALTER TABLE price_attribute DROP COLUMN object_id;
DROP INDEX unique_object_id__status on price;

-- Update nomenclature entity value

UPDATE `entity_value` SET `text` = 'Номенклатура' WHERE  entity_id = 23 and entity_attribute_id is null;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190422_1.0.14');