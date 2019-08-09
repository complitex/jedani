create view v_worker_status (object_id, last_name, first_name, middle_name, login, j_id, email, mk)
as select w.object_id, ln_v.text, fn_v.text, mn_v.text, u.login, wa_jid.text, wa_email.text, wa_mk.number
   from worker w

            left join worker_attribute wa_ln on w.id = wa_ln.domain_id and wa_ln.entity_attribute_id = 4
            left join last_name ln on ln.object_id = wa_ln.number and ln.status = 1
            left join last_name_attribute ln_a on ln_a.domain_id = ln.id and ln_a.entity_attribute_id = 1 and ln_a.status = 1
            left join last_name_value ln_v on ln_v.attribute_id = ln_a.id and ln_v.locale_id = 1

            left join worker_attribute wa_fn on w.id = wa_fn.domain_id and wa_fn.entity_attribute_id = 2 and wa_fn.status = 1
            left join first_name fn on fn.object_id = wa_fn.number and fn.status = 1
            left join first_name_attribute fn_a on fn_a.domain_id = fn.id and fn_a.entity_attribute_id = 1 and fn_a.status = 1
            left join first_name_value fn_v on fn_v.attribute_id = fn_a.id and fn_v.locale_id = 1

            left join worker_attribute wa_mn on w.id = wa_mn.domain_id and wa_mn.entity_attribute_id = 3 and wa_mn.status = 1
            left join middle_name mn on mn.object_id = wa_mn.number and mn.status = 1
            left join middle_name_attribute mn_a on mn_a.domain_id = mn.id and mn_a.entity_attribute_id = 1 and mn_a.status = 1
            left join middle_name_value mn_v on mn_v.attribute_id = mn_a.id and mn_v.locale_id = 1

            left join user u on u.id = w.parent_id

            left join worker_attribute wa_jid on w.id = wa_jid.domain_id and wa_jid.entity_attribute_id = 1 and wa_jid.status = 1

            left join worker_attribute wa_email on w.id = wa_email.domain_id and wa_email.entity_attribute_id = 9 and wa_email.status = 1
            left join worker_attribute wa_mk on w.id = wa_mk.domain_id and wa_mk.entity_attribute_id = 15 and wa_mk.status = 1;

-- Update

INSERT INTO `update` (`version`) VALUE ('20190809_1.0.25');