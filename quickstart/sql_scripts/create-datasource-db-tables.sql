-- !! database name is hardcoded here and parameter elsewhere
CREATE DATABASE IF NOT EXISTS testdata;
USE testdata;
-- the schema is dumb and not optimized
create table if not exists transactions (
    timestamp_millis bigint(20),
    transaction_value double,
    device varchar(30)
) ENGINE=InnoDB;

INSERT INTO transactions (timestamp_millis, transaction_value, device)
VALUES
        (1633132800000,12,'desktop'),
        (1633132800000,96,'mobile'),
        (1633219200000,11,'desktop'),
        (1633219200000,99,'mobile'),
        (1633305600000,10,'desktop'),
        (1633305600000,95,'mobile'),
        (1633392000000,10,'desktop'),
        (1633392000000,105,'mobile'),
        (1633478400000,8,'desktop'),
        (1633478400000,105,'mobile'),
        (1633564800000,8,'desktop'),
        (1633564800000,99,'mobile'),
        (1633651200000,11,'desktop'),
        (1633651200000,100,'mobile'),
        (1633737600000,12,'desktop'),
        (1633737600000,97,'mobile'),
        (1633824000000,9,'desktop'),
        (1633824000000,103,'mobile'),
        (1633910400000,12,'desktop'),
        (1633910400000,97,'mobile'),
        (1633996800000,12,'desktop'),
        (1633996800000,70,'mobile'),
        (1634083200000,12,'desktop'),
        (1634083200000,71,'mobile'),
        (1634169600000,12,'desktop'),
        (1634169600000,70,'mobile'),
        (1634256000000,12,'desktop'),
        (1634256000000,70,'mobile'),
        (1634342400000,12,'desktop'),
        (1634342400000,68,'mobile'),
        (1634428800000,12,'desktop'),
        (1634428800000,69,'mobile'),
        (1634515200000,12,'desktop'),
        (1634515200000,72,'mobile'),
        (1634601600000,12,'desktop'),
        (1634601600000,69,'mobile'),
        (1634688000000,12,'desktop'),
        (1634688000000,65,'mobile'),
        (1634774400000,12,'desktop'),
        (1634774400000,67,'mobile')
        ;
