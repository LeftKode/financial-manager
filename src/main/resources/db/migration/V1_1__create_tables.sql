create table if not exists currency (
                          currency_code varchar(3) primary key,
                          currency_name varchar(255) not null,
                          exchange_rate decimal(18, 5) not null,
                          created_at timestamp default current_timestamp,
                          updated_at timestamp default current_timestamp on update current_timestamp
);

create table if not exists account (
                         id int primary key auto_increment,
                         balance decimal(18, 5) not null,
                         currency varchar(3) not null,
                         created_at timestamp not null,
                         foreign key (currency)
                             references currency (currency_code),
                         constraint chk_balance_not_negative check (balance >= 0)
);

create table if not exists transaction (
                             id varchar(255) primary key,
                             source_account_id int not null,
                             target_account_id int not null,
                             amount DECIMAL(18 , 5) not null,
                             currency VARCHAR(3) not null,
                             foreign key (source_account_id)
                                 references account (id),
                             foreign key (target_account_id)
                                 references account (id),
                             foreign key (currency)
                                 references currency (currency_code)
);