--liquibase formatted sql
--changeset limpygnome:initial db setup

create table users
(
    userid varchar(36) not null,
    email varchar(50) not null,
    nickname varchar(14) not null,
    password_hash varchar(255) not null,
    password_salt varchar(255) not null,
    registered datetime not null,

    deaths bigint not null,
    kills bigint not null,
    score bigint not null,

    last_updated datetime not null,

    primary key (userid)
);

create table users_roles
(
    userid varchar(36) not null,
    role integer not null,

    foreign key(userid) references users(userid) on update cascade on delete cascade,
    primary key (userid, role)
);

create table game_sessions
(
    token varchar(36) not null,

    connected bit not null,
    created datetime not null,
    nickname varchar(14),

    deaths bigint not null,
    kills bigint not null,
    score bigint not null,

    last_updated datetime not null,

    userid varchar(36),

    foreign key(userid) references users(userid) on update cascade on delete cascade,
    primary key (token)
);

create table game_sessions_kv
(
    token varchar(36) not null,
    v tinyblob,
    k varchar(255) not null,

    foreign key(token) references game_sessions(token) on update cascade on delete cascade,
    primary key (token, k)
);

--rollback drop table game_sessions_kv;
--rollback drop table users_roles;
--rollback drop table game_sessions;
--rollback drop table users;
