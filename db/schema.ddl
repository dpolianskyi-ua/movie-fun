drop table if exists album_scheduler_task;

create table album_scheduler_task (started_at timestamp null default null);

insert into album_scheduler_task (started_at) values (null);