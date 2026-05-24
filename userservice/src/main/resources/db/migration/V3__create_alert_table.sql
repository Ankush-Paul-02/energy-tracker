create table alert (
    id bigserial primary key,
    user_id bigint,
    sent boolean not null default false,
    created_at timestamp not null default current_timestamp,

    constraint fk_alert_user foreign key (user_id) references users(id) on delete cascade
)