alter table apis drop foreign key fk_apis_app;
drop index ix_apis_app on apis;

alter table items_apis drop foreign key fk_items_apis_apis;
drop index ix_items_apis_apis on items_apis;

alter table items_apis drop foreign key fk_items_apis_items;
drop index ix_items_apis_items on items_apis;

alter table items_apps drop foreign key fk_items_apps_apps;
drop index ix_items_apps_apps on items_apps;

alter table items_apps drop foreign key fk_items_apps_items;
drop index ix_items_apps_items on items_apps;

alter table blacklist drop foreign key fk_blacklist_app_id;
drop index ix_blacklist_app_id on blacklist;

alter table histories drop foreign key fk_histories_item_id;
drop index ix_histories_item_id on histories;

alter table items drop foreign key fk_items_template;
drop index ix_items_template on items;

alter table items drop foreign key fk_items_creator;
drop index ix_items_creator on items;

alter table items drop foreign key fk_items_modifier;
drop index ix_items_modifier on items;

alter table subscribers drop foreign key fk_subscribers_items;
drop index ix_subscribers_items on subscribers;

alter table subscribers drop foreign key fk_subscribers_users;
drop index ix_subscribers_users on subscribers;

alter table template drop foreign key fk_template_creator;
drop index ix_template_creator on template;

alter table user_role drop foreign key fk_user_role_users;
drop index ix_user_role_users on user_role;

alter table user_role drop foreign key fk_user_role_roles;
drop index ix_user_role_roles on user_role;

drop table if exists apis;

drop table if exists items_apis;

drop table if exists apps;

drop table if exists items_apps;

drop table if exists blacklist;

drop table if exists convergence;

drop table if exists histories;

drop table if exists items;

drop table if exists subscribers;

drop table if exists roles;

drop table if exists template;

drop table if exists users;

drop table if exists user_role;

drop index ix_histories_channel on histories;
drop index ix_histories_recipient on histories;
drop index ix_histories_timestamp on histories;
drop index ix_histories_status on histories;
