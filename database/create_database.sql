create table sources (
    id              integer primary key asc,
    source          text not null
);

create table classes (
    id              integer primary key asc,
    class           text not null
);

create table product_names (
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

