# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table album (
  album_id                  bigint auto_increment not null,
  title                     varchar(255),
  artist                    varchar(255),
  year                      integer,
  artwork_format            varchar(255),
  constraint uq_album_1 unique (title,artist),
  constraint pk_album primary key (album_id))
;

create table audio (
  audio_id                  bigint auto_increment not null,
  title                     varchar(255),
  album_id                  bigint,
  track_number              integer,
  constraint pk_audio primary key (audio_id))
;

alter table audio add constraint fk_audio_album_1 foreign key (album_id) references album (album_id) on delete restrict on update restrict;
create index ix_audio_album_1 on audio (album_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists album;

drop table if exists audio;

SET REFERENTIAL_INTEGRITY TRUE;

