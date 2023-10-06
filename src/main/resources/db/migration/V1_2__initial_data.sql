insert ignore into currency (currency_code, currency_name, exchange_rate)
values ('USD', 'US Dollar', 1.00),
       ('EUR', 'Euro', 0.85),
       ('GBP', 'British Pound', 0.73);

insert ignore into `financial_manager`.`account` (`id`, `balance`, `currency`, `created_at`)
values ('1', '150', 'EUR', '2023-09-30');
insert ignore into `financial_manager`.`account` (`id`, `balance`, `currency`, `created_at`)
values ('2', '220', 'EUR', '2023-10-01');
insert ignore into `financial_manager`.`account` (`id`, `balance`, `currency`, `created_at`)
values ('3', '270', 'USD', '2023-10-02');