-- Migracoes tambem versionam DADOS de referencia, nao so estrutura.
insert into employees (name, email, department, salary, hire_date, active)
values ('Alice', 'alice@corp.com', 'IT', 9000.00, '2020-01-15', true),
       ('Bob', 'bob@corp.com', 'IT', 5000.00, '2021-03-10', true),
       ('Carol', 'carol@corp.com', 'HR', 7000.00, '2019-07-01', true);
