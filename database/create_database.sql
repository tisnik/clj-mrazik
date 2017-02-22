create table sources (
    id              integer primary key asc,
    source          text not null
);

create table classes (
    id              integer primary key asc,
    class           text not null
);

create table dictionary (
    id              integer primary key asc,
    term            text not null,
    description     text,
    class           integer,
    use             integer,
    incorrect_forms text,
    correct_forms   text,
    see_also        text,
    internal        integer,
    verified        integer,
    copyrighted     integer,
    source          integer,
    foreign key (source) references sources(id)
    foreign key (class)  references classes(id)
);

insert into classes (class) values("verb");
insert into classes (class) values("noun");
insert into classes (class) values("adverb");
insert into classes (class) values("adjective");
insert into classes (class) values("pronoun");
insert into classes (class) values("conjunction");
insert into classes (class) values("preposition");
insert into classes (class) values("interjection");
insert into classes (class) values("article");
insert into classes (class) values("numeral");
insert into classes (class) values("determiner");
insert into classes (class) values("exclamation");

