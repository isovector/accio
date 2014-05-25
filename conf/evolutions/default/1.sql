# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "Dad" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"name" VARCHAR(254) NOT NULL);
create table "Event" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"eventType" VARCHAR(254) NOT NULL,"task" INTEGER,"start" TIMESTAMP NOT NULL,"duration" BIGINT NOT NULL,"where" VARCHAR(254) NOT NULL,"description" VARCHAR(254) NOT NULL);
create table "Task" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"title" VARCHAR(254) NOT NULL,"description" VARCHAR(254),"dueDate" TIMESTAMP,"estimatedTime" BIGINT);

# --- !Downs

drop table "Dad";
drop table "Event";
drop table "Task";

