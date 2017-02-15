create table apis (
  id                            bigint auto_increment not null,
  name                          TEXT not null,
  hash                          char(40) not null,
  app                           bigint not null,
  constraint uq_apis_hash_app unique (hash,app),
  constraint pk_apis primary key (id)
);

create table items_apis (
  apis_id                       bigint not null,
  items_id                      bigint not null,
  constraint pk_items_apis primary key (apis_id,items_id)
);

create table apps (
  id                            bigint auto_increment not null,
  name                          varchar(128) not null,
  constraint uq_apps_name unique (name),
  constraint pk_apps primary key (id)
);

create table items_apps (
  apps_id                       bigint not null,
  items_id                      bigint not null,
  constraint pk_items_apps primary key (apps_id,items_id)
);

create table blacklist (
  id                            bigint auto_increment not null,
  content                       TEXT not null,
  app_id                        bigint not null,
  constraint pk_blacklist primary key (id)
);

create table convergence (
  id                            bigint auto_increment not null,
  hash                          char(40) not null,
  timestamp                     bigint not null,
  count                         bigint not null,
  constraint uq_convergence_hash unique (hash),
  constraint pk_convergence primary key (id)
);

create table histories (
  id                            bigint auto_increment not null,
  channel                       integer not null,
  recipient                     varchar(128) not null,
  message                       longtext not null,
  timestamp                     bigint not null,
  status                        integer not null,
  item_id                       bigint,
  constraint ck_histories_channel check ( channel in (0,1)),
  constraint ck_histories_status check ( status in (0,1,2,3,4)),
  constraint pk_histories primary key (id)
);

create table items (
  id                            bigint auto_increment not null,
  name                          varchar(128) not null,
  enable                        tinyint(1) default 0 not null,
  cron                          varchar(128) not null,
  descriptor                    TEXT,
  convergence                   integer not null,
  template                      bigint not null,
  variables                     LONGTEXT not null,
  creator                       bigint,
  create_time                   datetime(6) not null,
  modifier                      bigint,
  update_time                   datetime(6) not null,
  constraint uq_items_name unique (name),
  constraint pk_items primary key (id)
);

create table subscribers (
  items_id                      bigint not null,
  users_id                      bigint not null,
  constraint pk_subscribers primary key (items_id,users_id)
);

create table roles (
  id                            bigint auto_increment not null,
  name                          varchar(128) not null,
  descriptor                    TEXT,
  constraint uq_roles_name unique (name),
  constraint pk_roles primary key (id)
);

create table template (
  id                            bigint auto_increment not null,
  name                          varchar(128) not null,
  script                        LONGTEXT not null,
  creator                       bigint,
  timestamp                     bigint not null,
  constraint uq_template_name unique (name),
  constraint pk_template primary key (id)
);

create table users (
  id                            bigint auto_increment not null,
  name                          varchar(128) not null,
  email                         varchar(64),
  mobile                        varchar(24),
  is_new                        tinyint(1) default 0,
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id)
);

create table user_role (
  user_id                       bigint not null,
  role_id                       bigint not null,
  constraint pk_user_role primary key (user_id,role_id)
);

create index ix_histories_channel on histories (channel);
create index ix_histories_recipient on histories (recipient);
create index ix_histories_timestamp on histories (timestamp);
create index ix_histories_status on histories (status);
alter table apis add constraint fk_apis_app foreign key (app) references apps (id) on delete restrict on update restrict;
create index ix_apis_app on apis (app);

alter table items_apis add constraint fk_items_apis_apis foreign key (apis_id) references apis (id) on delete restrict on update restrict;
create index ix_items_apis_apis on items_apis (apis_id);

alter table items_apis add constraint fk_items_apis_items foreign key (items_id) references items (id) on delete restrict on update restrict;
create index ix_items_apis_items on items_apis (items_id);

alter table items_apps add constraint fk_items_apps_apps foreign key (apps_id) references apps (id) on delete restrict on update restrict;
create index ix_items_apps_apps on items_apps (apps_id);

alter table items_apps add constraint fk_items_apps_items foreign key (items_id) references items (id) on delete restrict on update restrict;
create index ix_items_apps_items on items_apps (items_id);

alter table blacklist add constraint fk_blacklist_app_id foreign key (app_id) references apps (id) on delete restrict on update restrict;
create index ix_blacklist_app_id on blacklist (app_id);

alter table histories add constraint fk_histories_item_id foreign key (item_id) references items (id) on delete restrict on update restrict;
create index ix_histories_item_id on histories (item_id);

alter table items add constraint fk_items_template foreign key (template) references template (id) on delete restrict on update restrict;
create index ix_items_template on items (template);

alter table items add constraint fk_items_creator foreign key (creator) references users (id) on delete restrict on update restrict;
create index ix_items_creator on items (creator);

alter table items add constraint fk_items_modifier foreign key (modifier) references users (id) on delete restrict on update restrict;
create index ix_items_modifier on items (modifier);

alter table subscribers add constraint fk_subscribers_items foreign key (items_id) references items (id) on delete restrict on update restrict;
create index ix_subscribers_items on subscribers (items_id);

alter table subscribers add constraint fk_subscribers_users foreign key (users_id) references users (id) on delete restrict on update restrict;
create index ix_subscribers_users on subscribers (users_id);

alter table template add constraint fk_template_creator foreign key (creator) references users (id) on delete restrict on update restrict;
create index ix_template_creator on template (creator);

alter table user_role add constraint fk_user_role_users foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_user_role_users on user_role (user_id);

alter table user_role add constraint fk_user_role_roles foreign key (role_id) references roles (id) on delete restrict on update restrict;
create index ix_user_role_roles on user_role (role_id);

